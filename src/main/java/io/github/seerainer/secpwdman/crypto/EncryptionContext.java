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
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The class CryptoContext.
 */
public final class EncryptionContext {
	private final EncryptionStrategy strategy;

	EncryptionContext(final EncryptionStrategy strategy) {
		this.strategy = strategy;
	}

	public byte[] decrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
			InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		return strategy.decrypt(data, password);
	}

	public byte[] encrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
			InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		return strategy.encrypt(data, password);
	}
}
