/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
 * philipp@seerainer.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.MAX_FILE_SIZE;
import static io.github.seerainer.secpwdman.config.StringConstants.ERROR;
import static io.github.seerainer.secpwdman.config.StringConstants.FILE_NOT_NULL;
import static io.github.seerainer.secpwdman.config.StringConstants.FILE_TOO_LARGE;
import static io.github.seerainer.secpwdman.config.StringConstants.TIME_TO_OPEN;
import static io.github.seerainer.secpwdman.config.StringConstants.TIME_TO_SAVE;
import static io.github.seerainer.secpwdman.config.StringConstants.WARN;
import static io.github.seerainer.secpwdman.config.StringConstants.empty;
import static io.github.seerainer.secpwdman.config.StringConstants.errorFil;
import static io.github.seerainer.secpwdman.config.StringConstants.errorImp;
import static io.github.seerainer.secpwdman.config.StringConstants.errorInp;
import static io.github.seerainer.secpwdman.config.StringConstants.errorOut;
import static io.github.seerainer.secpwdman.config.StringConstants.errorPwd;
import static io.github.seerainer.secpwdman.config.StringConstants.errorSev;
import static io.github.seerainer.secpwdman.config.StringConstants.titleErr;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.VAULT_FORMAT_VERSION;
import static io.github.seerainer.secpwdman.crypto.EnvelopeCrypto.decryptWithDek;
import static io.github.seerainer.secpwdman.crypto.EnvelopeCrypto.generateDek;
import static io.github.seerainer.secpwdman.crypto.EnvelopeCrypto.seal;
import static io.github.seerainer.secpwdman.crypto.EnvelopeCrypto.unwrapDek;
import static io.github.seerainer.secpwdman.crypto.KeyStoreManager.putPasswordInKeyStore;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.util.RandomPassword.generateKeyStorePassword;
import static io.github.seerainer.secpwdman.util.Util.clear;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.SWT;
import org.slf4j.Logger;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class IO.
 *
 * <p>
 * Handles all file encryption and decryption using the OWASP envelope
 * encryption pattern (see
 * {@link io.github.seerainer.secpwdman.crypto.EnvelopeCrypto}):
 * </p>
 * <ul>
 * <li>A random 256-bit Data Encryption Key (DEK) is generated once per
 * vault.</li>
 * <li>The DEK is wrapped by a Key Encryption Key (KEK) derived from the user's
 * master password via the configured KDF (Argon2/PBKDF2/scrypt).</li>
 * <li>The vault data is encrypted with the DEK directly; the KEK is never
 * stored.</li>
 * <li>The encrypted DEK is stored alongside the ciphertext in the JSON file,
 * allowing the master password to be changed without re-encrypting vault
 * data.</li>
 * </ul>
 */
public class IO {

    private static final Logger LOG = LogFactory.getLog(IO.class);

    private final Action action;

    /**
     * Instantiates a new IO.
     *
     * @param action the action
     */
    public IO(final Action action) {
	this.action = action;
    }

    static InputStream open(final String filePath) throws IOException {
	final var path = IOUtil.getPath(filePath);
	if (Files.size(path) >= MAX_FILE_SIZE) {
	    LOG.warn(WARN, FILE_TOO_LARGE);
	    throw new IOException(errorFil.formatted(IOUtil.getFilePath(filePath)));
	}
	// The size pre-check races file growth: cap the stream itself so a file
	// that grows between check and read fails instead of exhausting memory.
	return new CappedStream(Files.newInputStream(path), MAX_FILE_SIZE);
    }

    static void save(final String filePath, final byte[] fileBytes) throws IOException {
	final var path = IOUtil.getPath(filePath).toAbsolutePath();
	final var parent = path.getParent();
	if (parent != null) {
	    Files.createDirectories(parent);
	    IOUtil.restrictDirectoryPermissions(parent);
	}
	// Crash-safe write: temp file in same directory + fsync + atomic move.
	// A direct Files.write truncate risks a torn vault on power loss.
	final var tmp = Files.createTempFile(parent, ".secpwdman-", ".tmp");
	// Restrict before writing: close the window where vault bytes sit in a
	// file with creation-default permissions.
	IOUtil.restrictFilePermissions(tmp);
	try {
	    Files.write(tmp, fileBytes);
	    try (final var channel = FileChannel.open(tmp, StandardOpenOption.WRITE)) {
		channel.force(true);
	    } catch (final IOException _) {
		// Best effort: filesystems that do not support force still get atomic move.
	    }
	    try {
		Files.move(tmp, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
	    } catch (final AtomicMoveNotSupportedException _) {
		Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
	    }
	    IOUtil.restrictFilePermissions(path);
	    fsyncParent(parent);
	} finally {
	    Files.deleteIfExists(tmp);
	}
    }

    /**
     * Best-effort fsync of the parent directory so the rename itself survives power
     * loss on POSIX. No-op where directories cannot be opened (Windows).
     *
     * @param parent the directory containing the vault file, may be null
     */
    private static void fsyncParent(final Path parent) {
	if (parent == null) {
	    return;
	}
	try (final var dir = FileChannel.open(parent, StandardOpenOption.READ)) {
	    dir.force(true);
	} catch (final IOException _) {
	    // Best effort only.
	}
    }

    /**
     * Stores the master password in a PKCS12 KeyStore so it can be retrieved later
     * in the session without prompting the user again (e.g. for auto-save).
     */
    private static void savePassword(final byte[] password, final ConfigData cData) {
	final var sensitiveData = cData.getSensitiveData();
	sensitiveData.setKeyStorePassword(generateKeyStorePassword());
	sensitiveData.setKeyStoreData(putPasswordInKeyStore(sensitiveData.getKeyStorePassword(), password));
    }

    /**
     * Returns the DEK held in session, or generates a new one if this is the first
     * save of a vault (no DEK exists yet).
     *
     * <p>
     * The returned array is the live reference stored in {@code SensitiveData};
     * callers must <em>not</em> zero it.
     * </p>
     */
    private static byte[] getOrCreateDek(final ConfigData cData) {
	final var sensitiveData = cData.getSensitiveData();
	var dek = sensitiveData.getDek();
	if (dek == null || dek.length == 0) {
	    dek = generateDek();
	    sensitiveData.setDek(dek);
	}
	return dek;
    }

    /**
     * Opens a password-protected vault file using envelope encryption.
     *
     * <p>
     * Steps:
     * </p>
     * <ol>
     * <li>Parse JSON metadata and read the encrypted DEK + ciphertext.</li>
     * <li>Unwrap the DEK using a KEK derived from {@code password}.</li>
     * <li>Decrypt vault data with the recovered DEK.</li>
     * <li>Store both the master password (PKCS12) and the plaintext DEK in
     * {@code SensitiveData} for the session.</li>
     * </ol>
     *
     * @param password the master password bytes; zeroed after use
     * @param file     path to the vault file
     * @return {@code true} if the file was opened successfully
     */
    public boolean openFile(final byte[] password, final String file) {
	if (Objects.isNull(file)) {
	    throw new IllegalArgumentException(FILE_NOT_NULL);
	}
	final var startTime = System.currentTimeMillis();
	final var cData = action.getCData();
	byte[] bytes = null;
	byte[] dek = null;
	var exMsg = empty;
	try (final var is = open(file)) {
	    if (Objects.nonNull(password) && password.length > 0) {
		savePassword(password, cData);

		// Parse JSON: populates CryptoConfig and reads encData + wrappedDek
		final var envelope = JsonUtil.setJsonFile(cData, is);
		final var wrappedDek = envelope.wrappedDek();
		final var ciphertext = envelope.encryptedData();
		final var cConf = cData.getCryptoConfig();

		// Single KDF: unwrap once, then decrypt with the recovered DEK.
		// Previously unseal() + unwrapDek() derived the KEK twice.
		dek = unwrapDek(wrappedDek, password, cConf);
		bytes = decryptWithDek(ciphertext, dek, cConf);

		// Keep both the plaintext DEK and the wrapped DEK for this session
		final var sensitiveData = cData.getSensitiveData();
		sensitiveData.setDek(dek);
		sensitiveData.setWrappedDek(wrappedDek);
		dek = null; // ownership transferred to SensitiveData

		bytes = cData.isCompress() ? IOUtil.inflate(bytes) : bytes;
	    } else {
		bytes = is.readAllBytes();
	    }
	    action.fillTable(true, bytes);
	    LOG.info(TIME_TO_OPEN, Long.valueOf(System.currentTimeMillis() - startTime));
	    return true;
	} catch (final BadPaddingException e) {
	    LOG.warn(WARN, e);
	    exMsg = errorPwd;
	} catch (final IOException e) {
	    LOG.warn(WARN, e);
	    exMsg = errorInp.formatted(file);
	} catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException | JsonParserException e) {
	    LOG.warn(WARN, e);
	    exMsg = errorImp.formatted(IOUtil.getFilePath(file));
	} catch (final IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException
		| NoSuchAlgorithmException | NoSuchPaddingException | OutOfMemoryError | RuntimeException e) {
	    LOG.error(ERROR, e);
	    exMsg = errorSev;
	} finally {
	    clear(password);
	    clear(bytes);
	    clear(dek);
	}
	msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, exMsg);
	return false;
    }

    /**
     * Saves the vault to disk using envelope encryption.
     *
     * <p>
     * Steps:
     * </p>
     * <ol>
     * <li>Obtain (or generate) the session DEK.</li>
     * <li>Wrap the DEK with a KEK derived from {@code password} (seal).</li>
     * <li>Encrypt vault data with the DEK (seal).</li>
     * <li>Serialize both the encrypted DEK and ciphertext into the JSON file.</li>
     * </ol>
     *
     * <p>
     * If {@code password} is null or empty the vault is exported as plain CSV
     * (unencrypted).
     * </p>
     *
     * @param password the master password bytes; zeroed after use
     * @param file     destination path
     * @return {@code true} if the file was saved successfully
     */
    public boolean saveFile(final byte[] password, final String file) {
	if (Objects.isNull(file)) {
	    throw new IllegalArgumentException(FILE_NOT_NULL);
	}
	final var startTime = System.currentTimeMillis();
	final var cData = action.getCData();
	byte[] bytes = null;
	var exMsg = empty;
	try {
	    if (Objects.nonNull(password) && password.length > 0) {
		savePassword(password, cData);

		// Obtain the session DEK (creates one if this is a new vault)
		final var dek = getOrCreateDek(cData);
		final var cConf = cData.getCryptoConfig();

		bytes = action.extractData(false);
		bytes = cData.isCompress() ? IOUtil.deflate(bytes) : bytes;

		// New saves always use the current format (with AAD binding);
		// opening a legacy file and saving migrates it forward.
		cConf.setVaultFormatVersion(VAULT_FORMAT_VERSION);

		// Envelope-encrypt: data → DEK layer, DEK → KEK layer
		final var result = seal(bytes, dek, password, cConf);

		// Store the new wrappedDek for this session
		cData.getSensitiveData().setWrappedDek(result.wrappedDek());

		bytes = JsonUtil.getJsonFile(cData, result.encryptedData(), result.wrappedDek());
	    } else {
		bytes = action.extractData(true);
	    }
	    save(file, bytes);
	    LOG.info(TIME_TO_SAVE, Long.valueOf(System.currentTimeMillis() - startTime));
	    return true;
	} catch (final IOException e) {
	    LOG.error(ERROR, e);
	    exMsg = errorOut.formatted(IOUtil.getFilePath(file));
	} catch (final BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
		| InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | RuntimeException e) {
	    // seal() failures (KDF, AES-GCM, bad config) previously escaped as
	    // uncaught exceptions with no user feedback; fail closed instead.
	    LOG.error(ERROR, e);
	    exMsg = errorSev;
	} finally {
	    clear(password);
	    clear(bytes);
	}
	msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, exMsg);
	return false;
    }

    /**
     * Input stream that fails once more than {@code limit} bytes have been read,
     * bounding every downstream consumer ({@code readAllBytes}, JSON parsing)
     * regardless of file growth after the size pre-check.
     */
    static final class CappedStream extends FilterInputStream {

	private final long limit;
	private long read;

	CappedStream(final InputStream in, final long limit) {
	    super(in);
	    this.limit = limit;
	}

	@Override
	public int read() throws IOException {
	    final var value = super.read();
	    if (value >= 0 && ++read > limit) {
		throw new IOException("Stream exceeds size limit.");
	    }
	    return value;
	}

	@Override
	public int read(final byte[] bytes, final int offset, final int length) throws IOException {
	    final var count = super.read(bytes, offset, length);
	    if (count > 0 && (read += count) > limit) {
		throw new IOException("Stream exceeds size limit.");
	    }
	    return count;
	}
    }
}
