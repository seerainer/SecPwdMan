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
package io.github.seerainer.secpwdman.crypto;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Set;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.github.seerainer.secpwdman.io.ByteContainer;
import io.github.seerainer.secpwdman.util.SecureMemory;
import io.github.seerainer.secpwdman.util.Util;

/**
 * The class Crypto.
 */
public class Crypto implements CryptoConstants {

    private static final SecureRandom RANDOM_INSTANCE;

    static {
	try {
	    RANDOM_INSTANCE = SecureRandom.getInstanceStrong();
	} catch (final NoSuchAlgorithmException e) {
	    throw new RuntimeException(noSecureRandom, e);
	}
    }

    private Crypto() {
    }

    static byte[] appendValues(final byte[] iv, final byte[] salt, final byte[] ciphertext) {
	final var encrypted = new byte[iv.length + salt.length + ciphertext.length];
	System.arraycopy(iv, 0, encrypted, 0, iv.length);
	System.arraycopy(salt, 0, encrypted, iv.length, salt.length);
	System.arraycopy(ciphertext, 0, encrypted, iv.length + salt.length, ciphertext.length);
	return encrypted;
    }

    static void checkCryptoConfig(final CryptoConfig cConf) {
	if (isNull(cConf)) {
	    throw new IllegalArgumentException(configNull);
	}
	if (isNull(cConf.getCipherALGO()) || isNull(cConf.getKeyALGO())) {
	    throw new IllegalArgumentException(configNotSet);
	}
	if (isNull(cConf.getKeyDerivation())) {
	    throw new IllegalArgumentException(kdfNotSet);
	}
	resetConfig(cConf);
    }

    /**
     * Generates a sealed Object using secure native memory for key handling.
     *
     * @param data           the data to encrypt
     * @param key            the secret key
     * @param transformation the transformation
     * @param algorithm      the algorithm
     * @return SealedObject the encrypted data
     * @throws IllegalBlockSizeException the illegal block size exception
     * @throws InvalidKeyException       the invalid key exception
     * @throws IOException               the IO exception
     * @throws NoSuchPaddingException    the no such padding exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws ClassNotFoundException    the class not found exception
     */
    public static SealedObject generateSealedObject(final byte[] data, final byte[] key, final String transformation,
	    final String algorithm) throws IllegalBlockSizeException, InvalidKeyException, IOException,
	    NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
	return SecureMemory.withSecretMemory(key.clone(), keySegment -> {
	    try {
		final var cipherInstance = Cipher.getInstance(transformation);
		final var keyBytes = SecureMemory.readFromNative(keySegment);
		final var secretKey = getSecretKey(keyBytes, algorithm);
		cipherInstance.init(Cipher.ENCRYPT_MODE, secretKey);

		final var bc = new ByteContainer(data);
		final var so = new SealedObject(bc, cipherInstance);

		Util.clear(data);
		Util.clear(keyBytes);
		bc.clear();

		return so;
	    } catch (final Exception e) {
		throw new RuntimeException(secureSealedObjectFailed, e);
	    }
	});
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
	key.init(KEY_LENGTH, RANDOM_INSTANCE);
	return key.generateKey();
    }

    private static Stream<String> getAlgorithms(final String serviceName) {
	return Security.getAlgorithms(serviceName).stream();
    }

    static SecretKey getKeyTransformation(final byte[] password, final byte[] salt, final CryptoConfig cConf) {
	return SecureMemory.withSecretMemory(password.clone(), passwordSegment -> {
	    try {
		final var passwordBytes = SecureMemory.readFromNative(passwordSegment);
		final var result = switch (cConf.getKeyDerivation()) {
		case CryptoConfig.KDF.Argon2 ->
		    new KeyDerivationContext(new Argon2KeyDerivation(cConf)).deriveKey(passwordBytes, salt);
		case CryptoConfig.KDF.PBKDF2 ->
		    new KeyDerivationContext(new PBKDF2KeyDerivation(cConf)).deriveKey(passwordBytes, salt);
		case CryptoConfig.KDF.scrypt ->
		    new KeyDerivationContext(new ScryptKeyDerivation(cConf)).deriveKey(passwordBytes, salt);
		default -> throw new IllegalArgumentException(unexpectedValue + cConf.getKeyDerivation());
		};

		Util.clear(passwordBytes);
		return result;
	    } catch (final Exception e) {
		throw new RuntimeException(secureKeyTransFailed, e);
	    }
	});
    }

    /**
     * Gets a random value.
     *
     * @param length the length
     * @return byte[] the random value
     */
    public static byte[] getRandomValue(final int length) {
	final var value = new byte[length];
	RANDOM_INSTANCE.nextBytes(value);
	return value;
    }

    /**
     * Gets a new secret key with the specified algorithm using secure native
     * memory.
     *
     * @param key       the key bytes
     * @param algorithm the algorithm
     * @return SecretKey the SecretKey
     */
    public static SecretKey getSecretKey(final byte[] key, final String algorithm) {
	return SecureMemory.withSecretMemory(key.clone(), keySegment -> {
	    final var keyBytes = SecureMemory.readFromNative(keySegment);
	    final var secretKey = new SecretKeySpec(keyBytes, algorithm);
	    Util.clear(keyBytes);
	    return secretKey;
	});
    }

    /**
     * Gets the secure random instance strong.
     *
     * @return InstanceStrong
     * @throws NoSuchAlgorithmException if no strong instance is available throw a
     *                                  RuntimeException
     */
    public static SecureRandom getSecureRandom() {
	return RANDOM_INSTANCE;
    }

    private static boolean isCipherAvailable(final String cipherName) {
	return getAlgorithms(cipher).anyMatch(availableCipher -> availableCipher.equalsIgnoreCase(cipherName));
    }

    private static boolean isPKCS12Available() {
	return getAlgorithms(keyStore).anyMatch(pkcs12::equalsIgnoreCase);
    }

    private static void resetConfig(final CryptoConfig cConf) {
	if (!cConf.getCipherALGO().startsWith(cConf.getKeyALGO())) {
	    cConf.setCipherALGO(cipherAES);
	    cConf.setKeyALGO(keyAES);
	}
	if (!Set.of(CryptoConfig.KDF.Argon2, CryptoConfig.KDF.PBKDF2, CryptoConfig.KDF.scrypt)
		.contains(cConf.getKeyDerivation())) {
	    cConf.setKeyDerivation(CryptoConfig.KDF.Argon2);
	}
    }

    /**
     * Checks if Ciphers and strong SecureRandom are available. Test SecureRandom
     * strong, AES_256/GCM/NOPADDING, CHACHA20-POLY1305 and PKCS12
     *
     * @throws RuntimeException         if any of the required ciphers or
     *                                  SecureRandom is not available
     * @throws IllegalArgumentException if the CryptoConfig is null or not set
     */
    public static void selfTest(final CryptoConfig cConf) {
	if (isNull(getSecureRandom()) || !isCipherAvailable(cipherAES) || !isCipherAvailable(cipherChaCha20)
		|| !isPKCS12Available()) {
	    throw new RuntimeException(noCipher);
	}
	checkCryptoConfig(cConf);
    }
}
