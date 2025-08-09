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
 */
package io.github.seerainer.secpwdman.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

/**
 * Comprehensive unit tests for the crypto module. Tests encryption, decryption,
 * key derivation, and security features.
 */
@Tag("unit")
@DisplayName("Crypto Module Unit Tests")
class CryptoTest {

    private static final String TEST_DATA = "This is a test message for encryption/decryption";
    private static final char[] TEST_PASSWORD = "TestPassword123!".toCharArray();
    private static final byte[] TEST_BYTES = TEST_DATA.getBytes(StandardCharsets.UTF_8);
    private static final byte[] PASSWORD_BYTES = new String(TEST_PASSWORD).getBytes(StandardCharsets.UTF_8);
    private CryptoConfig config;
    private EncryptionContext crypto;

    private static Stream<Arguments> provideCryptoConfigurationCombinations() {
	return Stream.of(Arguments.of(CryptoConfig.KDF.Argon2, "AES/GCM/NoPadding", Argon2.D, Hmac.SHA256),
		Arguments.of(CryptoConfig.KDF.Argon2, "ChaCha20-Poly1305", Argon2.ID, Hmac.SHA256),
		Arguments.of(CryptoConfig.KDF.PBKDF2, "AES/GCM/NoPadding", Argon2.D, Hmac.SHA256),
		Arguments.of(CryptoConfig.KDF.PBKDF2, "ChaCha20-Poly1305", Argon2.D, Hmac.SHA512),
		Arguments.of(CryptoConfig.KDF.scrypt, "AES/GCM/NoPadding", Argon2.D, Hmac.SHA256),
		Arguments.of(CryptoConfig.KDF.scrypt, "ChaCha20-Poly1305", Argon2.D, Hmac.SHA512));
    }

    @BeforeEach
    void setUp() {
	config = new CryptoConfig();
	crypto = CryptoFactory.crypto(config);
    }

    @Test
    @DisplayName("Should encrypt and decrypt data successfully")
    void shouldEncryptAndDecryptSuccessfully() throws Exception {
	// Test encryption
	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);

	assertThat(encrypted).isNotNull().isNotEmpty().isNotEqualTo(TEST_BYTES);

	// Test decryption
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isNotNull().isEqualTo(TEST_BYTES);
	assertThat(new String(decrypted, StandardCharsets.UTF_8)).isEqualTo(TEST_DATA);
    }

    @Test
    @DisplayName("Should fail decryption with wrong password")
    void shouldFailDecryptionWithWrongPassword() throws Exception {
	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var wrongPassword = "WrongPassword".getBytes(StandardCharsets.UTF_8);

	assertThrows(Exception.class, () -> crypto.decrypt(encrypted, wrongPassword));
    }

    @Test
    @DisplayName("Should generate different encrypted outputs for same input")
    void shouldGenerateDifferentEncryptedOutputsForSameInput() throws Exception {
	final var encrypted1 = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var encrypted2 = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);

	assertThat(encrypted1).isNotEqualTo(encrypted2);

	// Both should decrypt to same plaintext
	final var decrypted1 = crypto.decrypt(encrypted1, PASSWORD_BYTES);
	final var decrypted2 = crypto.decrypt(encrypted2, PASSWORD_BYTES);

	assertThat(decrypted1).isEqualTo(decrypted2).isEqualTo(TEST_BYTES);
    }

    @Test
    @DisplayName("Should handle empty data")
    void shouldHandleEmptyData() throws Exception {
	final byte[] emptyData = {};

	final var encrypted = crypto.encrypt(emptyData, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(emptyData);
    }

    @Test
    @DisplayName("Should handle large data")
    void shouldHandleLargeData() throws Exception {
	// Create 1MB of test data
	final var largeData = new byte[1024 * 1024];
	new SecureRandom().nextBytes(largeData);

	final var encrypted = crypto.encrypt(largeData, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(largeData);
    }

    @Test
    @DisplayName("Should perform crypto self-test successfully")
    void shouldPerformSelfTestSuccessfully() {
	assertDoesNotThrow(() -> Crypto.selfTest(config));
    }

    @Test
    @DisplayName("Should validate secure random generation")
    @SuppressWarnings("static-method")
    void shouldValidateSecureRandomGeneration() {
	final var random1 = Crypto.getSecureRandom();
	final var random2 = Crypto.getSecureRandom();

	assertThat(random1).isNotNull();
	assertThat(random2).isNotNull();

	// Generate some random bytes and ensure they're different
	final var bytes1 = new byte[32];
	final var bytes2 = new byte[32];

	random1.nextBytes(bytes1);
	random2.nextBytes(bytes2);

	assertThat(bytes1).isNotEqualTo(bytes2);
    }

    @ParameterizedTest
    @EnumSource(CryptoConfig.KDF.class)
    @DisplayName("Should work with all key derivation functions")
    void shouldWorkWithAllKeyDerivationFunctions(final CryptoConfig.KDF kdf) throws Exception {
	config.setKeyDerivation(kdf);
	crypto = CryptoFactory.crypto(config);

	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(TEST_BYTES);
    }

    @ParameterizedTest
    @EnumSource(Argon2.class)
    @DisplayName("Should work with different Argon2 types")
    void shouldWorkWithDifferentArgon2Types(final Argon2 argon2Type) throws Exception {
	config.setKeyDerivation(CryptoConfig.KDF.Argon2);
	config.setArgon2Type(argon2Type);
	crypto = CryptoFactory.crypto(config);

	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(TEST_BYTES);
    }

    @ParameterizedTest
    @ValueSource(strings = { "AES/GCM/NoPadding", "ChaCha20-Poly1305" })
    @DisplayName("Should work with different cipher algorithms")
    void shouldWorkWithDifferentCipherAlgorithms(final String cipher) throws Exception {
	config.setCipherALGO(cipher);
	crypto = CryptoFactory.crypto(config);

	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(TEST_BYTES);
    }

    @ParameterizedTest
    @EnumSource(Hmac.class)
    @DisplayName("Should work with different HMAC algorithms")
    void shouldWorkWithDifferentHmacAlgorithms(final Hmac hmac) throws Exception {
	config.setKeyDerivation(CryptoConfig.KDF.PBKDF2);
	config.setHmac(hmac);
	crypto = CryptoFactory.crypto(config);

	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(TEST_BYTES);
    }

    @ParameterizedTest
    @MethodSource("provideCryptoConfigurationCombinations")
    @DisplayName("Should work with various crypto configuration combinations")
    void shouldWorkWithVariousCryptoConfigurationCombinations(final CryptoConfig.KDF kdf, final String cipher,
	    final Argon2 argon2Type, final Hmac hmac) throws Exception {

	config.setKeyDerivation(kdf);
	config.setCipherALGO(cipher);

	if (kdf == CryptoConfig.KDF.Argon2) {
	    config.setArgon2Type(argon2Type);
	} else if (kdf == CryptoConfig.KDF.PBKDF2) {
	    config.setHmac(hmac);
	}

	crypto = CryptoFactory.crypto(config);

	final var encrypted = crypto.encrypt(TEST_BYTES, PASSWORD_BYTES);
	final var decrypted = crypto.decrypt(encrypted, PASSWORD_BYTES);

	assertThat(decrypted).isEqualTo(TEST_BYTES);
    }
}
