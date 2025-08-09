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

/**
 * The class CryptoFactory.
 */
public class CryptoFactory implements CryptoConstants {

    private CryptoFactory() {
    }

    /**
     * Instantiates a new crypto.
     *
     * @param cConf the crypto config
     */
    public static EncryptionContext crypto(final CryptoConfig cConf) {
	Crypto.checkCryptoConfig(cConf);
	return switch (cConf.getKeyALGO()) {
	case keyAES -> new EncryptionContext(new AESEncryptionStrategy(cConf));
	case keyChaCha20 -> new EncryptionContext(new ChaCha20EncryptionStrategy(cConf));
	default -> throw new IllegalArgumentException(unexpectedValue + cConf.getKeyALGO());
	};
    }
}
