/*
 * Secure Password Manager
 * Copyright (C) 2025  Philipp Seerainer
 * philipp@seerainer.com
 * https://www.seerainer.com/
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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The class CryptoUtil.
 */
public final class CryptoUtil implements CryptoConstants {

	static byte[] appendValues(final byte[] iv, final byte[] salt, final byte[] ciphertext) {
		final var encrypted = new byte[iv.length + salt.length + ciphertext.length];
		System.arraycopy(iv, 0, encrypted, 0, iv.length);
		System.arraycopy(salt, 0, encrypted, iv.length, salt.length);
		System.arraycopy(ciphertext, 0, encrypted, iv.length + salt.length, ciphertext.length);
		return encrypted;
	}

	/**
	 * Generates a sealed Object.
	 *
	 * @param data           the data to encrypt
	 * @param SecretKey      the secret key
	 * @param transformation the transformation
	 * @param algorithm      the algorithm
	 * @return SealedObject the encrypted data
	 * @throws IllegalBlockSizeException the illegal block size exception
	 * @throws InvalidKeyException       the invalid key exception
	 * @throws IOException               the IO exception
	 * @throws NoSuchPaddingException    the no such padding exception
	 * @throws NoSuchAlgorithmException  the no such algorithm exception
	 */
	public static SealedObject generateSealedObject(final byte[] data, final byte[] key, final String transformation,
			final String algorithm)
			throws IllegalBlockSizeException, InvalidKeyException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var cipherInstance = Cipher.getInstance(transformation);
		cipherInstance.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm));
		return new SealedObject(new String(data), cipherInstance);
	}

	/**
	 * Generates a secret key.
	 *
	 * @param algorithm the algorithm
	 * @return SecretKey the SecretKey
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public static SecretKey generateSecretKey(final String algorithm) throws NoSuchAlgorithmException {
		final var key = KeyGenerator.getInstance(algorithm);
		key.init(KEY_LENGTH, getSecureRandom());
		return key.generateKey();
	}

	private static Stream<String> getAlgorithms(final String serviceName) {
		return Security.getAlgorithms(serviceName).stream();
	}

	static Cipher getCipher(final String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance(transformation);
	}

	private static SecretKey getKeyTransformation(final byte[] password, final byte[] salt, final ConfigData cData)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return cData.isArgon2() ? KeyTransformation.argon2(password, salt, cData)
				: KeyTransformation.pbkdf2(password, salt, cData);
	}

	static byte[] getRandomValue(final int length) {
		final var value = new byte[length];
		getSecureRandom().nextBytes(value);
		return value;
	}

	/**
	 * Gets a new secret key with the specified algorithm.
	 *
	 * @param key       the key
	 * @param algorithm the algorithm
	 * @return SecretKey the SecretKey
	 */
	public static SecretKey getSecretKey(final byte[] key, final String algorithm) {
		return new SecretKeySpec(key, algorithm);
	}

	/**
	 * Get a new secure random instance strong.
	 *
	 * @return InstanceStrong
	 * @throws NoSuchAlgorithmException if no strong instance is available throw a
	 *                                  RuntimeException
	 */
	public static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstanceStrong();
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("No strong SecureRandom instance available.", e);
		}
	}

	static byte[] getValueFromData(final byte[] original, final int from, final int to) {
		return Arrays.copyOfRange(original, from, to);
	}

	static Cipher initCipher(final Cipher cipherInstance, final int mode, final byte[] password, final byte[] salt,
			final byte[] nonce, final ConfigData cData)
			throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
		final var params = cipherAES.equals(cData.getCipherALGO()) ? new GCMParameterSpec(TAG_LENGTH, nonce)
				: new IvParameterSpec(nonce);
		cipherInstance.init(mode, getKeyTransformation(password, salt, cData), params);
		return cipherInstance;
	}

	private static boolean isCipherAvailable(final String cipherName) {
		return getAlgorithms(cipher).anyMatch(availableCipher -> availableCipher.equalsIgnoreCase(cipherName));
	}

	private static boolean isPKCS12Available() {
		return getAlgorithms(keyStore).anyMatch(pkcs12::equalsIgnoreCase);
	}

	/**
	 * Checks if Ciphers and strong SecureRandom are available.
	 */
	public static void selfTest() {
		// Test SecureRandom strong instance
		getSecureRandom();
		// Test AES_256/GCM/NOPADDING, CHACHA20-POLY1305 and PKCS12 availability
		if (!isCipherAvailable(cipherAES) || !isCipherAvailable(cipherChaCha20) || !isPKCS12Available()) {
			throw new RuntimeException("Cipher not available");
		}
	}

	private CryptoUtil() {
	}
}
