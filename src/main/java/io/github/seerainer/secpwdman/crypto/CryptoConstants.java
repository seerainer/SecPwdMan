/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
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
 * The class CryptoConstants.
 */
public final class CryptoConstants {

    public static final int IV_LENGTH = 12;
    public static final int KEY_LENGTH = 256;
    public static final int DEK_BYTES = 32; // 256-bit Data Encryption Key
    public static final int MEM_SIZE = 1024;
    public static final int OUT_LENGTH = 32;
    public static final int SALT_LENGTH = 16;
    public static final int TAG_LENGTH = 128;
    public static final int ARGON2_MEMO = 64;
    public static final int ARGON2_MEMO_MIN = 19;
    public static final int ARGON2_MEMO_MAX = 512;
    public static final int ARGON2_ITER = 8;
    public static final int ARGON2_ITER_MIN = 2;
    public static final int ARGON2_ITER_MAX = 256;
    public static final int ARGON2_PARA_MIN = 1;
    public static final int ARGON2_PARA_MAX = 8;
    public static final int PBKDF2_ITER = 600000;
    public static final int PBKDF2_MIN_SHA256 = 600000;
    public static final int PBKDF2_MIN_SHA512 = 210000;
    public static final int PBKDF2_MAX = 0x1000000;
    public static final int[] SCRYPT_N = { 8, 16, 32, 64, 128, 256, 512 };
    public static final int SCRYPT_R = 8;
    public static final int SCRYPT_P_MIN = 1;
    public static final int SCRYPT_P_MAX = 10;
    public static final String alias = "secpwdman";
    public static final String cipher = "Cipher";
    public static final String keyStore = "KeyStore";
    public static final String mac = "Mac";
    public static final String messageDigest = "MessageDigest";
    public static final String pkcs12 = "PKCS12";
    public static final String signature = "Signature";
    public static final String keyAES = "AES";
    public static final String keyChaCha20 = "CHACHA20";
    public static final String cipherAES = "AES/GCM/NoPadding";
    public static final String cipherChaCha20 = "CHACHA20-POLY1305";
    /**
     * Pre-1.x persisted spelling of {@link #cipherAES}; accepted on load and
     * normalized.
     */
    public static final String legacyCipherAES = "AES_256/GCM/NOPADDING";
    public static final String argon2 = "Argon2";
    public static final String argon2id = "Argon2id";
    public static final String argon2d = "Argon2d";
    public static final String pbkdf2 = "PBKDF2";
    public static final String SCRYPT = "scrypt";
    public static final String configNotSet = "CryptoConfig is not set.";
    public static final String configNull = "CryptoConfig is null.";
    public static final String kdfNotSet = "KeyDerivation is not set.";
    public static final String noCipher = "No encryption cipher available.";
    public static final String noSecureRandom = "No strong SecureRandom instance available.";
    public static final String noEntryFound = "No SecretKeyEntry found for alias.";
    public static final String secureKeyTransFailed = "Secure key transformation failed";
    public static final String secureSealedObjectFailed = "Secure sealed object generation failed";
    public static final String unexpectedValue = "Unexpected value: ";
    /** Vault files without a {@code formatVersion} field predate AAD binding. */
    public static final int VAULT_FORMAT_LEGACY = 0;
    /** Current vault format: KDF/cipher metadata bound as AEAD associated data. */
    public static final int VAULT_FORMAT_VERSION = 1;
    public static final String unsupportedFormat = "Unsupported vault format version.";
    public static final String dekMissing = "Encrypted DEK is missing from file; cannot decrypt.";
    public static final int MIN_WRAPPED_DEK_LENGTH = IV_LENGTH + SALT_LENGTH + 16;

    private CryptoConstants() {
	throw new UnsupportedOperationException("Class not instantiable");
    }
}
