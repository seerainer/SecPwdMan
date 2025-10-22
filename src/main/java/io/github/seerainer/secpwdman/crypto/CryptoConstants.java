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
 * The interface CryptoConstants.
 */
public interface CryptoConstants {

    int IV_LENGTH = 12;
    int KEY_LENGTH = 256;
    int MEM_SIZE = 1024;
    int OUT_LENGTH = 32;
    int SALT_LENGTH = 16;
    int TAG_LENGTH = 128;

    int ARGON2_MEMO = 64;
    int ARGON2_MEMO_MIN = 19;
    int ARGON2_MEMO_MAX = 512;
    int ARGON2_ITER = 8;
    int ARGON2_ITER_MIN = 2;
    int ARGON2_ITER_MAX = 256;
    int ARGON2_PARA_MIN = 1;
    int ARGON2_PARA_MAX = 8;

    int PBKDF2_ITER = 600000;
    int PBKDF2_MIN_SHA256 = 600000;
    int PBKDF2_MIN_SHA512 = 210000;
    int PBKDF2_MAX = 0x1000000;

    int[] SCRYPT_N = { 8, 16, 32, 64, 128, 256, 512 };
    int SCRYPT_R = 8;
    int SCRYPT_P_MIN = 1;
    int SCRYPT_P_MAX = 10;

    String alias = "secpwdman";
    String cipher = "Cipher";
    String keyStore = "KeyStore";
    String mac = "Mac";
    String messageDigest = "MessageDigest";
    String pkcs12 = "PKCS12";
    String signature = "Signature";

    String keyAES = "AES";
    String keyChaCha20 = "CHACHA20";
    String cipherAES = "AES_256/GCM/NOPADDING";
    String cipherChaCha20 = "CHACHA20-POLY1305";

    String argon2 = "Argon2";
    String argon2id = "Argon2id";
    String argon2d = "Argon2d";
    String pbkdf2 = "PBKDF2";
    String SCRYPT = "scrypt";

    String configNotSet = "CryptoConfig is not set.";
    String configNull = "CryptoConfig is null.";
    String kdfNotSet = "KeyDerivation is not set.";
    String noCipher = "No encryption cipher available.";
    String noSecureRandom = "No strong SecureRandom instance available.";
    String noEntryFound = "No SecretKeyEntry found for alias.";
    String secureKeyTransFailed = "Secure key transformation failed";
    String secureSealedObjectFailed = "Secure sealed object generation failed";
    String unexpectedValue = "Unexpected value: ";
}
