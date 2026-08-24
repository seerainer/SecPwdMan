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
package io.github.seerainer.secpwdman.crypto;

import static io.github.seerainer.secpwdman.crypto.CryptoConstants.DEK_BYTES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.IV_LENGTH;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SALT_LENGTH;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.TAG_LENGTH;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.cipherAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.dekMissing;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.dekUnwrapFailed;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.keyAES;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

import io.github.seerainer.secpwdman.util.SecureMemory;
import io.github.seerainer.secpwdman.util.Util;

/**
 * Implements OWASP envelope encryption (Cryptographic Storage Cheat Sheet —
 * "Encrypting Stored Keys").
 *
 * <h2>Key hierarchy</h2>
 *
 * <pre>
 *   master password  ──[KDF]──▶  KEK  ──[AES-256-GCM]──▶  eDEK  (stored in file)
 *                                                ▲
 *   random 256-bit DEK  ──────────────────────────
 *
 *   plaintext data  ──[DEK + AES-256-GCM]──▶  ciphertext  (stored in file)
 * </pre>
 *
 * <h2>Properties</h2>
 * <ul>
 * <li>The DEK is generated once per vault and is independent of the master
 * password. Changing the master password only re-wraps the DEK; the vault data
 * does not need to be re-encrypted.</li>
 * <li>The KEK is ephemeral: it is derived from the user's password on demand
 * and is never persisted.</li>
 * <li>The eDEK (IV + salt + AES-GCM ciphertext of the DEK) is stored in the
 * password-file JSON alongside the encrypted vault data.</li>
 * <li>Both layers use AES-256/GCM/NoPadding with independent random IVs and
 * salts, satisfying the OWASP requirement that the KEK is "at least as strong
 * as the DEK".</li>
 * </ul>
 */
public final class EnvelopeCrypto {

    private EnvelopeCrypto() {
	throw new UnsupportedOperationException("Class not instantiable");
    }

    // -------------------------------------------------------------------------
    // DEK generation
    // -------------------------------------------------------------------------

    /**
     * Generates a fresh 256-bit Data Encryption Key using a
     * cryptographically-secure random source.
     *
     * @return raw DEK bytes (32 bytes, never stored in plaintext)
     */
    public static byte[] generateDek() {
	return Crypto.getRandomValue(DEK_BYTES);
    }

    // -------------------------------------------------------------------------
    // DEK wrap / unwrap (KEK layer — password-derived)
    // -------------------------------------------------------------------------

    /**
     * Wraps (encrypts) the DEK using a KEK derived from the user's master password.
     *
     * <p>
     * Layout of the returned byte array:
     * </p>
     *
     * <pre>
     *   [ 12-byte IV | 16-byte salt | AES-256-GCM(DEK) + 16-byte auth-tag ]
     * </pre>
     *
     * @param dek      plaintext DEK (32 bytes); zeroed after use
     * @param password master password bytes; zeroed after use
     * @param cConf    crypto configuration (selects KDF and parameters)
     * @return the encrypted DEK blob
     */
    public static byte[] wrapDek(final byte[] dek, final byte[] password, final CryptoConfig cConf) {
	return SecureMemory.withSecretMemory(dek.clone(), dekSegment -> {
	    final var dekBytes = SecureMemory.readFromNative(dekSegment);
	    try {
		final var iv = Crypto.getRandomValue(IV_LENGTH);
		final var salt = Crypto.getRandomValue(SALT_LENGTH);
		// The KEK layer always uses AES-256-GCM regardless of the data-layer cipher.
		// A temporary AES-keyed config is used so the KDF always outputs an AES key.
		final var kekConf = kekConfig(cConf);
		final var kek = Crypto.getKeyTransformation(password, salt, kekConf);
		final var cipher = Cipher.getInstance(cipherAES);
		cipher.init(Cipher.ENCRYPT_MODE, kek, gcmParams(iv));
		final var encryptedDek = cipher.doFinal(dekBytes);
		return Crypto.appendValues(iv, salt, encryptedDek);
	    } catch (final Exception e) {
		throw new RuntimeException(dekUnwrapFailed, e);
	    } finally {
		Util.clear(dekBytes);
	    }
	});
    }

    /**
     * Unwraps (decrypts) the DEK using a KEK derived from the user's master
     * password.
     *
     * @param wrappedDek the encrypted DEK blob produced by {@link #wrapDek}
     * @param password   master password bytes; zeroed after use
     * @param cConf      crypto configuration
     * @return plaintext DEK bytes (caller is responsible for zeroing)
     * @throws BadPaddingException if the password is wrong or the blob is corrupted
     */
    public static byte[] unwrapDek(final byte[] wrappedDek, final byte[] password, final CryptoConfig cConf)
	    throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
	    InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
	if (wrappedDek == null || wrappedDek.length == 0) {
	    throw new IllegalArgumentException(dekMissing);
	}
	final var iv = Arrays.copyOfRange(wrappedDek, 0, IV_LENGTH);
	final var salt = Arrays.copyOfRange(wrappedDek, IV_LENGTH, IV_LENGTH + SALT_LENGTH);
	// The KEK layer always uses AES-256-GCM; use a temporary AES-keyed config.
	final var kekConf = kekConfig(cConf);
	final var kek = Crypto.getKeyTransformation(password, salt, kekConf);
	final var cipher = Cipher.getInstance(cipherAES);
	cipher.init(Cipher.DECRYPT_MODE, kek, gcmParams(iv));
	return cipher.doFinal(wrappedDek, IV_LENGTH + SALT_LENGTH, wrappedDek.length - IV_LENGTH - SALT_LENGTH);
    }

    // -------------------------------------------------------------------------
    // Data encrypt / decrypt (DEK layer)
    // -------------------------------------------------------------------------

    /**
     * Encrypts {@code data} directly with the DEK using the cipher selected in
     * {@code cConf} (AES-256-GCM or ChaCha20-Poly1305).
     *
     * @param data  plaintext bytes
     * @param dek   256-bit Data Encryption Key (plaintext); zeroed inside
     * @param cConf crypto configuration
     * @return ciphertext blob (IV + salt + ciphertext+tag)
     */
    public static byte[] encryptWithDek(final byte[] data, final byte[] dek, final CryptoConfig cConf) {
	return SecureMemory.withSecretMemory(dek.clone(), dekSegment -> {
	    final var dekBytes = SecureMemory.readFromNative(dekSegment);
	    try {
		final var secretKey = Crypto.getSecretKey(dekBytes, cConf.getKeyALGO());
		return new EncryptionContext(selectStrategy(cConf)).encryptWithKey(data, secretKey);
	    } catch (final Exception e) {
		throw new RuntimeException(e);
	    } finally {
		Util.clear(dekBytes);
	    }
	});
    }

    /**
     * Decrypts {@code ciphertext} with the DEK using the cipher selected in
     * {@code cConf}.
     *
     * @param ciphertext encrypted blob produced by {@link #encryptWithDek}
     * @param dek        256-bit Data Encryption Key (plaintext); zeroed inside
     * @param cConf      crypto configuration
     * @return plaintext bytes
     */
    public static byte[] decryptWithDek(final byte[] ciphertext, final byte[] dek, final CryptoConfig cConf) {
	return SecureMemory.withSecretMemory(dek.clone(), dekSegment -> {
	    final var dekBytes = SecureMemory.readFromNative(dekSegment);
	    try {
		final var secretKey = Crypto.getSecretKey(dekBytes, cConf.getKeyALGO());
		return new EncryptionContext(selectStrategy(cConf)).decryptWithKey(ciphertext, secretKey);
	    } catch (final Exception e) {
		throw new RuntimeException(e);
	    } finally {
		Util.clear(dekBytes);
	    }
	});
    }

    // -------------------------------------------------------------------------
    // Convenience: full envelope cycle
    // -------------------------------------------------------------------------

    /**
     * Full envelope-encryption in one call:
     * <ol>
     * <li>Wrap the DEK with the user's password (KEK derived via KDF).</li>
     * <li>Encrypt {@code data} with the DEK.</li>
     * </ol>
     *
     * @param data     plaintext vault data
     * @param dek      256-bit DEK (not zeroed — caller manages lifecycle)
     * @param password master password bytes
     * @param cConf    crypto configuration
     * @return {@link EnvelopeResult} containing both encrypted artefacts
     */
    public static EnvelopeResult seal(final byte[] data, final byte[] dek, final byte[] password,
	    final CryptoConfig cConf) {
	final var encData = encryptWithDek(data, dek, cConf);
	final var wrappedDk = wrapDek(dek, password, cConf);
	return new EnvelopeResult(encData, wrappedDk);
    }

    /**
     * Full envelope-decryption in one call:
     * <ol>
     * <li>Unwrap the DEK using the user's password.</li>
     * <li>Decrypt {@code ciphertext} with the recovered DEK.</li>
     * </ol>
     *
     * @param ciphertext encrypted vault data
     * @param wrappedDek encrypted DEK blob stored in the file
     * @param password   master password bytes
     * @param cConf      crypto configuration
     * @return plaintext vault data
     */
    public static byte[] unseal(final byte[] ciphertext, final byte[] wrappedDek, final byte[] password,
	    final CryptoConfig cConf) throws BadPaddingException, IllegalBlockSizeException,
	    InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
	final var dek = unwrapDek(wrappedDek, password, cConf);
	try {
	    return decryptWithDek(ciphertext, dek, cConf);
	} finally {
	    Util.clear(dek);
	}
    }

    private static AlgorithmParameterSpec gcmParams(final byte[] iv) {
	return new GCMParameterSpec(TAG_LENGTH, iv);
    }

    private static EncryptionStrategy selectStrategy(final CryptoConfig cConf) {
	return switch (cConf.getKeyALGO()) {
	case keyAES -> new AESEncryptionStrategy(cConf);
	default -> new ChaCha20EncryptionStrategy(cConf);
	};
    }

    /**
     * Returns a {@link CryptoConfig} configured to use AES as the key algorithm,
     * while preserving all KDF parameters from {@code source}.
     *
     * <p>
     * The KEK wrapping layer always uses AES-256-GCM irrespective of the data-layer
     * cipher (AES or ChaCha20). This ensures that the KDF always produces an
     * AES-compatible key for the wrap/unwrap operations.
     * </p>
     */
    private static CryptoConfig kekConfig(final CryptoConfig source) {
	final var kek = new CryptoConfig();
	kek.setKeyALGO(keyAES);
	kek.setCipherALGO(cipherAES);
	kek.setKeyDerivation(source.getKeyDerivation());
	kek.setArgon2Type(source.getArgon2Type());
	kek.setArgon2Memo(source.getArgon2Memo());
	kek.setArgon2Iter(source.getArgon2Iter());
	kek.setArgon2Para(source.getArgon2Para());
	kek.setHmac(source.getHmac());
	kek.setPBKDF2Iter(source.getPBKDF2Iter());
	kek.setScryptN(source.getScryptN());
	kek.setScryptR(source.getScryptR());
	kek.setScryptP(source.getScryptP());
	return kek;
    }

    /**
     * Convenience record grouping the outputs of a full envelope-encryption
     * operation so callers receive both artefacts in one call.
     *
     * @param encryptedData encrypted vault data (DEK layer)
     * @param wrappedDek    encrypted DEK (KEK layer)
     */
    public record EnvelopeResult(byte[] encryptedData, byte[] wrappedDek) {
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

}
