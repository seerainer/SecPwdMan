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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The record ChaCha20EncryptionStrategy.
 */
record ChaCha20EncryptionStrategy(ConfigData cData) implements CryptoConstants, EncryptionStrategy {

	@Override
	public byte[] encrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var instance = CryptoUtil.getCipher(cipherChaCha20);
		final var nonce = CryptoUtil.getRandomValue(IV_LENGTH);
		final var salt = CryptoUtil.getRandomValue(SALT_LENGTH);
		final var ciphertext = CryptoUtil.initCipher(instance, Cipher.ENCRYPT_MODE, password, salt, nonce, cData).doFinal(data);
		return CryptoUtil.appendValues(nonce, salt, ciphertext);
	}

	@Override
	public byte[] decrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		final var instance = CryptoUtil.getCipher(cipherChaCha20);
		final var nonce = CryptoUtil.getValueFromData(data, 0, IV_LENGTH);
		final var salt = CryptoUtil.getValueFromData(data, IV_LENGTH, IV_LENGTH + SALT_LENGTH);
		final var ciphertext = CryptoUtil.initCipher(instance, Cipher.DECRYPT_MODE, password, salt, nonce, cData);
		return ciphertext.doFinal(data, IV_LENGTH + SALT_LENGTH, data.length - IV_LENGTH - SALT_LENGTH);
	}
}
