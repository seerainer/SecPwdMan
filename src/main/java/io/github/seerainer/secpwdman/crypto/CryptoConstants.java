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

/**
 * The interface CryptoConstants.
 */
public interface CryptoConstants {

	int IV_LENGTH = 12;
	int TAG_LENGTH = 128;
	int KEY_LENGTH = 256;
	int SALT_LENGTH = 16;
	int OUT_LENGTH = 32;
	int MEM_SIZE = 1024;

	int ARGON_MEMO_MIN = 19;
	int ARGON_MEMO_MAX = 1024;
	int ARGON_ITER_MIN = 2;
	int ARGON_ITER_MAX = 256;
	int ARGON_PARA_MIN = 1;
	int ARGON_PARA_MAX = 8;

	int PBKDF2_MIN = 600000;
	int PBKDF2_MAX = 0x1000000;

	String alias = "secpwdman";
	String messageDigest = "MessageDigest";
	String signature = "Signature";

	String keyStore = "KeyStore";
	String pkcs12 = "PKCS12";

	String cipher = "Cipher";
	String keyAES = "AES";
	String keyChaCha20 = "CHACHA20";
	String cipherAES = "AES_256/GCM/NOPADDING";
	String cipherChaCha20 = "CHACHA20-POLY1305";

	String mac = "Mac";
	String pbkdf2 = "PBKDF2WithHmacSHA256";

	String argon2id = "Argon2id";
	String argon2d = "Argon2d";
}
