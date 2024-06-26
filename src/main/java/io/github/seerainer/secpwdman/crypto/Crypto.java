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
package io.github.seerainer.secpwdman.crypto;

import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.getSecureRandom;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

import com.password4j.Argon2Function;
import com.password4j.Password;
import com.password4j.types.Argon2;

import io.github.seerainer.secpwdman.config.ConfData;

/**
 * The Class Crypto.
 */
public class Crypto {

	private static final int GCM_IV_LENGTH = ConfData.GCM_IV_LENGTH;
	private static final int GCM_TAG_LENGTH = ConfData.GCM_TAG_LENGTH;
	private static final int KEY_LENGTH = ConfData.KEY_LENGTH;
	private static final int MEM_SIZE = ConfData.MEM_SIZE;
	private static final int SALT_LENGTH = ConfData.SALT_LENGTH;
	private static final int OUT_LENGTH = ConfData.OUT_LENGTH;

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
	 * @throws ArrayIndexOutOfBoundsException     the array index out of bounds
	 *                                            exception
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
	public byte[] decrypt(final byte[] txt, final byte[] pwd) throws ArrayIndexOutOfBoundsException, BadPaddingException,
			IllegalArgumentException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var cipher = Cipher.getInstance(cData.cMode);
		final var decoded = Base64.getDecoder().decode(txt);
		final var iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
		final var salt = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, GCM_IV_LENGTH + SALT_LENGTH);

		cipher.init(Cipher.DECRYPT_MODE, keyDerivation(pwd, salt), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

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
	public byte[] encrypt(final byte[] txt, final byte[] pwd)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var cipher = Cipher.getInstance(cData.cMode);
		final var secureRandom = getSecureRandom();
		final var iv = new byte[GCM_IV_LENGTH];
		final var salt = new byte[SALT_LENGTH];
		secureRandom.nextBytes(iv);
		secureRandom.nextBytes(salt);

		cipher.init(Cipher.ENCRYPT_MODE, keyDerivation(pwd, salt), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

		final var ciphertext = cipher.doFinal(txt);
		final var encrypted = new byte[iv.length + salt.length + ciphertext.length];
		System.arraycopy(iv, 0, encrypted, 0, iv.length);
		System.arraycopy(salt, 0, encrypted, iv.length, salt.length);
		System.arraycopy(ciphertext, 0, encrypted, iv.length + salt.length, ciphertext.length);

		return Base64.getEncoder().encode(encrypted);
	}

	/**
	 * Key derivation function, Argon2id or PBKDF2WithHmacSHA512
	 *
	 * @param pwd  the password
	 * @param salt the salt
	 * @return the SecretKeySpec
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	private SecretKey keyDerivation(final byte[] pwd, final byte[] salt)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		if (cData.isArgon2id()) {
			final var m = cData.getArgonMemo();
			final var t = cData.getArgonIter();
			final var p = cData.getArgonPara();
			final var argon = Argon2Function.getInstance(m * MEM_SIZE, t, p, OUT_LENGTH, Argon2.ID);
			final var hash = Password.hash(pwd).addSalt(salt).with(argon).getBytes();
			return new SecretKeySpec(hash, cData.cCiph);
		}

		final var charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(pwd));
		final var charArray = new char[charBuffer.remaining()];
		charBuffer.get(charArray);

		final var spec = new PBEKeySpec(charArray, salt, cData.getPBKDFIter(), KEY_LENGTH);
		final var factory = SecretKeyFactory.getInstance(cData.pbkdf);
		final var key = factory.generateSecret(spec).getEncoded();

		clear(charBuffer.array());
		clear(charArray);

		return new SecretKeySpec(key, cData.cCiph);
	}
}
