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

import javax.crypto.SecretKey;

import com.password4j.Password;
import com.password4j.ScryptFunction;

/**
 * The record ScryptKeyDerivation.
 */
record ScryptKeyDerivation(CryptoConfig cConf) implements CryptoConstants, KeyDerivationStrategy {

    @Override
    public SecretKey deriveKey(final byte[] password, final byte[] salt) {
	final var hashingFunction = ScryptFunction.getInstance(cConf.getScryptN() * MEM_SIZE, cConf.getScryptR(),
		cConf.getScryptP(), OUT_LENGTH);
	return Crypto.getSecretKey(Password.hash(password).addSalt(salt).with(hashingFunction).getBytes(),
		cConf.getKeyALGO());
    }
}
