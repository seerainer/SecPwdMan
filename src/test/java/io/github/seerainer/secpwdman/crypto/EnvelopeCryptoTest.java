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
 */
package io.github.seerainer.secpwdman.crypto;

import static io.github.seerainer.secpwdman.crypto.CryptoConstants.DEK_BYTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

/**
 * Unit tests for {@link EnvelopeCrypto}.
 *
 * <p>
 * These tests verify the OWASP envelope-encryption contract:
 * <ul>
 * <li>DEK generation produces 256-bit independent random keys.</li>
 * <li>Wrapping and unwrapping the DEK with the correct password
 * round-trips.</li>
 * <li>A wrong password causes unwrap to throw
 * {@link javax.crypto.BadPaddingException}.</li>
 * <li>Changing the password (re-wrapping the same DEK) does not alter the
 * plaintext recovered after decryption — vault data is not re-encrypted.</li>
 * <li>Full seal/unseal round-trip works for all KDF × cipher combinations.</li>
 * </ul>
 * </p>
 */
@Tag("unit")
@DisplayName("Envelope Encryption Unit Tests")
class EnvelopeCryptoTest {

    private static final byte[] PASSWORD = "Str0ng!MasterPassword#99".getBytes(StandardCharsets.UTF_8);
    private static final byte[] PLAINTEXT = "uuid,group,title,url,user,password,notes\n"
	    .getBytes(StandardCharsets.UTF_8);

    private CryptoConfig config;

    @BeforeEach
    void setUp() {
	config = new CryptoConfig();
    }

    // -------------------------------------------------------------------------
    // DEK generation
    // -------------------------------------------------------------------------

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("generateDek produces 32 random bytes")
    void generateDekIsCorrectLength() {
	final var dek = EnvelopeCrypto.generateDek();
	assertThat(dek).hasSize(DEK_BYTES);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("generateDek produces independent keys")
    void generateDekIsRandom() {
	final var dek1 = EnvelopeCrypto.generateDek();
	final var dek2 = EnvelopeCrypto.generateDek();
	assertThat(dek1).isNotEqualTo(dek2);
    }

    // -------------------------------------------------------------------------
    // DEK wrap / unwrap
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("wrapDek + unwrapDek round-trips the DEK")
    void wrapUnwrapRoundTrip() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var wrapped = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	final var recovered = EnvelopeCrypto.unwrapDek(wrapped, PASSWORD.clone(), config);
	assertThat(recovered).isEqualTo(dek);
    }

    @Test
    @DisplayName("Wrong password causes unwrapDek to throw")
    void wrongPasswordThrowsOnUnwrap() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var wrapped = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	final var wrongPwd = "WrongPassword!".getBytes(StandardCharsets.UTF_8);

	assertThatThrownBy(() -> EnvelopeCrypto.unwrapDek(wrapped, wrongPwd, config)).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Each wrapDek call produces a distinct blob (random IV+salt)")
    void wrapProducesDifferentBlobsEachCall() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var wrapped1 = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	final var wrapped2 = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	assertThat(wrapped1).isNotEqualTo(wrapped2);
	// But both must unwrap to the same DEK
	final var r1 = EnvelopeCrypto.unwrapDek(wrapped1, PASSWORD.clone(), config);
	final var r2 = EnvelopeCrypto.unwrapDek(wrapped2, PASSWORD.clone(), config);
	assertThat(r1).isEqualTo(dek).isEqualTo(r2);
    }

    @Test
    @DisplayName("unwrapDek with null/empty wrapped blob throws IllegalArgumentException")
    void unwrapEmptyDekThrows() {
	assertThatThrownBy(() -> EnvelopeCrypto.unwrapDek(null, PASSWORD.clone(), config))
		.isInstanceOf(IllegalArgumentException.class);
	assertThatThrownBy(() -> EnvelopeCrypto.unwrapDek(new byte[0], PASSWORD.clone(), config))
		.isInstanceOf(IllegalArgumentException.class);
    }

    // -------------------------------------------------------------------------
    // Data encrypt / decrypt with DEK
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("encryptWithDek + decryptWithDek round-trips plaintext")
    void encryptDecryptWithDekRoundTrip() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var cipher = EnvelopeCrypto.encryptWithDek(PLAINTEXT, dek.clone(), config);
	assertThat(cipher).isNotEqualTo(PLAINTEXT);
	final var recovered = EnvelopeCrypto.decryptWithDek(cipher, dek.clone(), config);
	assertThat(recovered).isEqualTo(PLAINTEXT);
    }

    @Test
    @DisplayName("Different DEKs produce different ciphertexts")
    void differentDeksProduceDifferentCiphertexts() throws Exception {
	final var dek1 = EnvelopeCrypto.generateDek();
	final var dek2 = EnvelopeCrypto.generateDek();
	final var c1 = EnvelopeCrypto.encryptWithDek(PLAINTEXT, dek1.clone(), config);
	final var c2 = EnvelopeCrypto.encryptWithDek(PLAINTEXT, dek2.clone(), config);
	assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    @DisplayName("Wrong DEK causes decryptWithDek to throw")
    void wrongDekThrowsOnDecrypt() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var wrongDek = EnvelopeCrypto.generateDek();
	final var cipher = EnvelopeCrypto.encryptWithDek(PLAINTEXT, dek.clone(), config);
	assertThatThrownBy(() -> EnvelopeCrypto.decryptWithDek(cipher, wrongDek, config)).isInstanceOf(Exception.class);
    }

    // -------------------------------------------------------------------------
    // Full seal / unseal
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("seal + unseal round-trips with default config (AES + Argon2)")
    void sealUnsealRoundTrip() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);

	assertThat(result.encryptedData()).isNotEqualTo(PLAINTEXT);
	assertThat(result.wrappedDek()).isNotEmpty();

	final var recovered = EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(),
		config);
	assertThat(recovered).isEqualTo(PLAINTEXT);
    }

    @Test
    @DisplayName("Wrong password causes unseal to throw")
    void wrongPasswordCausesUnsealToThrow() throws Exception {
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	final var wrong = "WrongPass!".getBytes(StandardCharsets.UTF_8);

	assertThatThrownBy(() -> EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), wrong, config))
		.isInstanceOf(Exception.class);
    }

    // -------------------------------------------------------------------------
    // OWASP key property: re-wrapping the DEK (password change) does not
    // require re-encrypting the data
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Password change re-wraps DEK without touching vault data")
    void passwordChangeRewrapsDekOnly() throws Exception {
	// Original seal
	final var dek = EnvelopeCrypto.generateDek();
	final var original = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);

	// Simulated password change: unwrap DEK with old password, re-wrap with new one
	final var newPassword = "N3wP@ssword!Updated#2026".getBytes(StandardCharsets.UTF_8);
	final var recoveredDek = EnvelopeCrypto.unwrapDek(original.wrappedDek(), PASSWORD.clone(), config);
	final var newWrappedDek = EnvelopeCrypto.wrapDek(recoveredDek.clone(), newPassword.clone(), config);

	// The encryptedData is UNCHANGED — only the wrappedDek changes
	final var recovered = EnvelopeCrypto.unseal(original.encryptedData(), // same ciphertext
		newWrappedDek, // new wrapped DEK
		newPassword.clone(), config);
	assertThat(recovered).isEqualTo(PLAINTEXT);

	// Old password must no longer work with the new wrapped DEK
	final var oldPwdCopy = PASSWORD.clone();
	assertThatThrownBy(() -> EnvelopeCrypto.unseal(original.encryptedData(), newWrappedDek, oldPwdCopy, config))
		.isInstanceOf(Exception.class);
    }

    // -------------------------------------------------------------------------
    // Parameterized: all KDF types
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @EnumSource(CryptoConfig.KDF.class)
    @DisplayName("seal/unseal works with all KDF types")
    void allKdfsWork(final CryptoConfig.KDF kdf) throws Exception {
	config.setKeyDerivation(kdf);
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	final var plain = EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config);
	assertThat(plain).isEqualTo(PLAINTEXT);
    }

    // -------------------------------------------------------------------------
    // Parameterized: AES + ChaCha20
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("cipherConfigs")
    @DisplayName("seal/unseal works with all cipher algorithms")
    void allCiphersWork(final String keyAlgo, final String cipherAlgo) throws Exception {
	config.setKeyALGO(keyAlgo);
	config.setCipherALGO(cipherAlgo);
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	final var plain = EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config);
	assertThat(plain).isEqualTo(PLAINTEXT);
    }

    static Stream<Arguments> cipherConfigs() {
	return Stream.of(Arguments.of("AES", "AES/GCM/NoPadding"), Arguments.of("CHACHA20", "CHACHA20-POLY1305"));
    }

    @Test
    @DisplayName("pre-1.x AES spelling normalizes and still round-trips (old vaults open)")
    void legacyAesSpellingNormalizes() throws Exception {
	config.setKeyALGO("AES");
	config.setCipherALGO("AES_256/GCM/NOPADDING");
	assertThat(config.getCipherALGO()).isEqualTo("AES/GCM/NoPadding");
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	final var plain = EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config);
	assertThat(plain).isEqualTo(PLAINTEXT);
    }

    // -------------------------------------------------------------------------
    // Format v1: AAD-bound metadata
    // -------------------------------------------------------------------------

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("v1 seal/unseal round-trips with AAD for AES and ChaCha20")
    void v1SealUnsealRoundTrips() throws Exception {
	for (final var keyAlgo : new String[] { "AES", "CHACHA20" }) {
	    final var v1 = new CryptoConfig();
	    v1.setKeyALGO(keyAlgo);
	    v1.setCipherALGO("AES".equals(keyAlgo) ? "AES/GCM/NoPadding" : "CHACHA20-POLY1305");
	    v1.setVaultFormatVersion(1);
	    final var dek = EnvelopeCrypto.generateDek();
	    final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), v1);
	    assertThat(EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), v1))
		    .isEqualTo(PLAINTEXT);
	}
    }

    @Test
    @DisplayName("v1 cipher metadata tampering fails authentication (pure AAD proof)")
    void v1CipherMetadataTamperRejected() throws Exception {
	// Downgrade-style tamper: the cipher string affects neither key
	// derivation nor strategy selection (keyALGO drives both), so only the
	// AAD can reject it.
	config.setVaultFormatVersion(1);
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	config.setCipherALGO("AES/CBC/PKCS5Padding");
	assertThatThrownBy(
		() -> EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config))
		.isInstanceOf(BadPaddingException.class);
    }

    @Test
    @DisplayName("v1 blob rejected on the legacy path (version field is load-bearing)")
    void v1BlobRejectedWithoutAad() throws Exception {
	config.setVaultFormatVersion(1);
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(PLAINTEXT, dek, PASSWORD.clone(), config);
	config.setVaultFormatVersion(0);
	assertThatThrownBy(
		() -> EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config))
		.isInstanceOf(BadPaddingException.class);
    }

    // -------------------------------------------------------------------------
    // Parameterized: Argon2 variants
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @EnumSource(Argon2.class)
    @DisplayName("wrapDek/unwrapDek works with all Argon2 types")
    void allArgon2TypesWork(final Argon2 argon2Type) throws Exception {
	config.setKeyDerivation(CryptoConfig.KDF.Argon2);
	config.setArgon2Type(argon2Type);
	final var dek = EnvelopeCrypto.generateDek();
	final var wrapped = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	final var recovered = EnvelopeCrypto.unwrapDek(wrapped, PASSWORD.clone(), config);
	assertThat(recovered).isEqualTo(dek);
    }

    // -------------------------------------------------------------------------
    // Parameterized: PBKDF2 HMAC variants
    // -------------------------------------------------------------------------

    @ParameterizedTest
    @EnumSource(Hmac.class)
    @DisplayName("wrapDek/unwrapDek works with all PBKDF2 HMAC algorithms")
    void allPbkdf2HmacVariantsWork(final Hmac hmac) throws Exception {
	config.setKeyDerivation(CryptoConfig.KDF.PBKDF2);
	config.setHmac(hmac);
	final var dek = EnvelopeCrypto.generateDek();
	final var wrapped = EnvelopeCrypto.wrapDek(dek.clone(), PASSWORD.clone(), config);
	final var recovered = EnvelopeCrypto.unwrapDek(wrapped, PASSWORD.clone(), config);
	assertThat(recovered).isEqualTo(dek);
    }

    // -------------------------------------------------------------------------
    // Large data
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("seal/unseal handles 1 MB of vault data")
    void largePlaintextRoundTrips() throws Exception {
	final var largeData = new byte[1024 * 1024];
	Crypto.getSecureRandom().nextBytes(largeData);
	final var dek = EnvelopeCrypto.generateDek();
	final var result = EnvelopeCrypto.seal(largeData, dek, PASSWORD.clone(), config);
	final var plain = EnvelopeCrypto.unseal(result.encryptedData(), result.wrappedDek(), PASSWORD.clone(), config);
	assertThat(plain).isEqualTo(largeData);
    }

    // -------------------------------------------------------------------------
    // Self-test
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Crypto.selfTest passes after envelope crypto changes")
    void selfTestPassesAfterEnvelopeChanges() {
	assertDoesNotThrow(() -> Crypto.selfTest(config));
    }
}
