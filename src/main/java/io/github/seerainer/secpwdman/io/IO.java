/*
 * SecPwdMan
 * Copyright (C) 2025  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.crypto.CryptoFactory.crypto;
import static io.github.seerainer.secpwdman.crypto.KeyStoreManager.putPasswordInKeyStore;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.util.RandomPassword.generateKeyStorePassword;
import static io.github.seerainer.secpwdman.util.Util.clear;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.SWT;
import org.slf4j.Logger;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class IO.
 */
public class IO implements PrimitiveConstants, StringConstants {

    private static final Logger LOG = LogFactory.getLog();

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
	return Files.newInputStream(path);
    }

    static void save(final String filePath, final byte[] fileBytes) throws IOException {
	Files.write(IOUtil.getPath(filePath), fileBytes);
    }

    private static void savePassword(final byte[] password, final ConfigData cData) {
	final var sensitiveData = cData.getSensitiveData();
	sensitiveData.setKeyStorePassword(generateKeyStorePassword());
	sensitiveData.setKeyStoreData(putPasswordInKeyStore(sensitiveData.getKeyStorePassword(), password));
    }

    /**
     * Opens the file.
     *
     * @param password the password
     * @param file     the file
     * @return true, if successful
     */
    public boolean openFile(final byte[] password, final String file) {
	if (Objects.isNull(file)) {
	    throw new IllegalArgumentException(FILE_NOT_NULL);
	}
	final var startTime = System.currentTimeMillis();
	final var cData = action.getCData();
	byte[] bytes = null;
	var exMsg = empty;
	try (final var is = open(file)) {
	    if (Objects.nonNull(password) && password.length > 0) {
		savePassword(password, cData);
		bytes = JsonUtil.setJsonFile(cData, is);
		bytes = crypto(cData.getCryptoConfig()).decrypt(bytes, password);
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
		| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | OutOfMemoryError e) {
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
     * Saves the file.
     *
     * @param password the password
     * @param file     the file
     * @return true, if successful
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
		bytes = action.extractData(false);
		bytes = cData.isCompress() ? IOUtil.deflate(bytes) : bytes;
		bytes = crypto(cData.getCryptoConfig()).encrypt(bytes, password);
		bytes = JsonUtil.getJsonFile(cData, bytes);
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
		| InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e) {
	    LOG.error(ERROR, e);
	    exMsg = errorSev;
	} finally {
	    clear(password);
	    clear(bytes);
	}
	msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, exMsg);
	return false;
    }
}
