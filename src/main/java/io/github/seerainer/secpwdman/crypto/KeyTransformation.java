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

import static io.github.seerainer.secpwdman.util.Util.toChars;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.password4j.Argon2Function;
import com.password4j.Password;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The class KeyTransformation.
 */
final class KeyTransformation implements CryptoConstants {

	static SecretKey argon2(final byte[] password, final byte[] salt, final ConfigData cData) {
		final var hashingFunction = Argon2Function.getInstance(cData.getArgonMemo() * MEM_SIZE, cData.getArgonIter(),
				cData.getArgonPara(), OUT_LENGTH, cData.getArgonType());
		return CryptoUtil.getSecretKey(Password.hash(password).addSalt(salt).with(hashingFunction).getBytes(),
				cData.getKeyALGO());
	}

	static SecretKey pbkdf2(final byte[] password, final byte[] salt, final ConfigData cData)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		final var keySpec = new PBEKeySpec(toChars(password), salt, cData.getPBKDFIter(), KEY_LENGTH);
		return CryptoUtil.getSecretKey(SecretKeyFactory.getInstance(pbkdf2).generateSecret(keySpec).getEncoded(),
				cData.getKeyALGO());
	}

	private KeyTransformation() {
	}
}
