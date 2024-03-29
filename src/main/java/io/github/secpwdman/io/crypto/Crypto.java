/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
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
package io.github.secpwdman.io.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.secpwdman.config.ConfData;

/**
 * The Class Crypto.
 */
public class Crypto {
	public static final int GCM_IV_LENGTH = 12;
	public static final int GCM_TAG_LENGTH = 128; // 16 * Byte.SIZE;
	public static final int KEY_LENGTH = 256;
	public static final int SALT_LENGTH = 128;

	private final ConfData cData;

	/**
	 * Instantiates a new crypto.
	 *
	 * @param cData the cdata
	 */
	public Crypto(final ConfData cData) {
		this.cData = cData;
	}

	/**
	 * Decrypt.
	 *
	 * @param txt the text
	 * @param pwd the password
	 * @return the byte[]
	 * @throws BadPaddingException                the bad padding exception
	 * @throws IllegalArgumentException           the illegal argument exception
	 * @throws IllegalBlockSizeException          the illegal block size exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter
	 *                                            exception
	 * @throws InvalidKeyException                the invalid key exception
	 * @throws InvalidKeySpecException            the invalid key spec exception
	 * @throws NoSuchAlgorithmException           the no such algorithm exception
	 * @throws NoSuchPaddingException             the no such padding exception
	 */
	public byte[] decrypt(final byte[] txt, final char[] pwd) throws BadPaddingException, IllegalArgumentException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
			InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var decoded = Base64.getDecoder().decode(txt);
		final var iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
		final var salt = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, GCM_IV_LENGTH + SALT_LENGTH);

		final var cipher = Cipher.getInstance(cData.cMode);
		final var ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		final var spec = new PBEKeySpec(pwd, salt, cData.getIterCount(), KEY_LENGTH);
		final var factory = SecretKeyFactory.getInstance(cData.keySt);
		final SecretKey skey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), cData.cCiph);
		cipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);

		return cipher.doFinal(decoded, GCM_IV_LENGTH + SALT_LENGTH, decoded.length - GCM_IV_LENGTH - SALT_LENGTH);
	}

	/**
	 * Encrypt.
	 *
	 * @param txt the text
	 * @param pwd the password
	 * @return the byte[]
	 * @throws BadPaddingException                the bad padding exception
	 * @throws IllegalBlockSizeException          the illegal block size exception
	 * @throws InvalidAlgorithmParameterException the invalid algorithm parameter
	 *                                            exception
	 * @throws InvalidKeyException                the invalid key exception
	 * @throws InvalidKeySpecException            the invalid key spec exception
	 * @throws NoSuchAlgorithmException           the no such algorithm exception
	 * @throws NoSuchPaddingException             the no such padding exception
	 */
	public byte[] encrypt(final byte[] txt, final char[] pwd) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var iv = new byte[GCM_IV_LENGTH];
		SecureRandom.getInstanceStrong().nextBytes(iv);
		final var salt = new byte[SALT_LENGTH];
		SecureRandom.getInstanceStrong().nextBytes(salt);

		final var cipher = Cipher.getInstance(cData.cMode);
		final var ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
		final var spec = new PBEKeySpec(pwd, salt, cData.getIterCount(), KEY_LENGTH);
		final var factory = SecretKeyFactory.getInstance(cData.keySt);
		final SecretKey skey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), cData.cCiph);
		cipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);

		final var ciphertext = cipher.doFinal(txt);
		final var encrypted = new byte[iv.length + salt.length + ciphertext.length];
		System.arraycopy(iv, 0, encrypted, 0, iv.length);
		System.arraycopy(salt, 0, encrypted, iv.length, salt.length);
		System.arraycopy(ciphertext, 0, encrypted, iv.length + salt.length, ciphertext.length);

		return Base64.getEncoder().encodeToString(encrypted).getBytes();
	}
}
