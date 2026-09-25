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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;

import javax.crypto.BadPaddingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

/**
 * Unit tests for security-hardening fixes: KDF parameter clamping, envelope
 * fail-closed behavior and ciphertext length checks.
 */
@Tag("unit")
@DisplayName("Security Hardening Unit Tests")
class SecurityFixesTest {

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Weak scrypt N is raised to OWASP minimum")
    void weakScryptNIsClamped() {
	final var config = new CryptoConfig();
	config.setScryptN(8);
	assertThat(config.getScryptN()).isEqualTo(128);
	config.setScryptN(16);
	assertThat(config.getScryptN()).isEqualTo(128);
	config.setScryptN(64);
	assertThat(config.getScryptN()).isEqualTo(128);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Valid scrypt N values are preserved")
    void validScryptNPreserved() {
	final var config = new CryptoConfig();
	config.setScryptN(128);
	assertThat(config.getScryptN()).isEqualTo(128);
	config.setScryptN(256);
	assertThat(config.getScryptN()).isEqualTo(256);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("scrypt R is clamped to the supported range")
    void scryptRIsClamped() {
	final var config = new CryptoConfig();
	config.setScryptR(Integer.MIN_VALUE);
	assertThat(config.getScryptR()).isEqualTo(CryptoConstants.SCRYPT_R_MIN);
	config.setScryptR(CryptoConstants.SCRYPT_R_MAX);
	assertThat(config.getScryptR()).isEqualTo(CryptoConstants.SCRYPT_R_MAX);
	config.setScryptR(Integer.MAX_VALUE);
	assertThat(config.getScryptR()).isEqualTo(CryptoConstants.SCRYPT_R_MAX);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Weak PBKDF2 iterations are raised to OWASP minimum")
    void weakPbkdf2IterIsClamped() {
	final var config = new CryptoConfig();
	config.setHmac(Hmac.SHA256);
	config.setPBKDF2Iter(1000);
	assertThat(config.getPBKDF2Iter()).isEqualTo(600000);
	config.setHmac(Hmac.SHA512);
	config.setPBKDF2Iter(1000);
	assertThat(config.getPBKDF2Iter()).isEqualTo(210000);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Argon2 parameters are clamped to valid ranges")
    void argon2ParamsClamped() {
	final var config = new CryptoConfig();
	config.setArgon2Memo(1);
	assertThat(config.getArgon2Memo()).isEqualTo(19);
	config.setArgon2Memo(100000);
	assertThat(config.getArgon2Memo()).isEqualTo(512);
	config.setArgon2Iter(0);
	assertThat(config.getArgon2Iter()).isEqualTo(2);
	config.setArgon2Iter(100000);
	assertThat(config.getArgon2Iter()).isEqualTo(256);
	config.setArgon2Para(0);
	assertThat(config.getArgon2Para()).isEqualTo(1);
	config.setArgon2Para(100);
	assertThat(config.getArgon2Para()).isEqualTo(8);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Null Argon2 type and HMAC throw and keep the previous value")
    void nullEnumsRejected() {
	final var config = new CryptoConfig();
	config.setArgon2Type(Argon2.D);
	assertThatThrownBy(() -> config.setArgon2Type(null)).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getArgon2Type()).isEqualTo(Argon2.D);
	config.setHmac(Hmac.SHA512);
	assertThatThrownBy(() -> config.setHmac(null)).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getHmac()).isEqualTo(Hmac.SHA512);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Null cipher, key and KDF throw and keep the previous value")
    void nullStringsRejected() {
	final var config = new CryptoConfig();
	final var cipher = config.getCipherALGO();
	assertThatThrownBy(() -> config.setCipherALGO(null)).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getCipherALGO()).isEqualTo(cipher);
	assertThatThrownBy(() -> config.setCipherALGO("   ")).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getCipherALGO()).isEqualTo(cipher);
	final var key = config.getKeyALGO();
	assertThatThrownBy(() -> config.setKeyALGO(null)).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getKeyALGO()).isEqualTo(key);
	final var kdf = config.getKeyDerivation();
	assertThatThrownBy(() -> config.setKeyDerivation(null)).isInstanceOf(IllegalArgumentException.class);
	assertThat(config.getKeyDerivation()).isEqualTo(kdf);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Truncated wrapped DEK is rejected before KDF")
    void truncatedWrappedDekRejected() {
	final var config = new CryptoConfig();
	final var password = "password".getBytes(StandardCharsets.UTF_8);
	assertThatThrownBy(() -> EnvelopeCrypto.unwrapDek(new byte[10], password, config))
		.isInstanceOf(IllegalArgumentException.class);
	assertThatThrownBy(() -> EnvelopeCrypto.unwrapDek(new byte[43], password, config))
		.isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Unknown key algorithm fails closed instead of defaulting")
    void unknownKeyAlgoFailsClosed() {
	final var config = new CryptoConfig();
	config.setKeyALGO("DES");
	config.setCipherALGO("DES/ECB/PKCS5Padding");
	final var dek = EnvelopeCrypto.generateDek();
	assertThatThrownBy(() -> EnvelopeCrypto.encryptWithDek("data".getBytes(StandardCharsets.UTF_8), dek, config))
		.isInstanceOf(Exception.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Cipher/key algorithm mismatch throws instead of silently falling back to AES")
    void cipherKeyMismatchFailsClosed() {
	final var config = new CryptoConfig();
	config.setKeyALGO("AES");
	config.setCipherALGO("CHACHA20-POLY1305");
	assertThatThrownBy(() -> CryptoFactory.crypto(config)).isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("DEK-layer auth failure surfaces as typed BadPaddingException")
    void dekLayerAuthFailureIsTyped() throws Exception {
	final var config = new CryptoConfig();
	final var dek = EnvelopeCrypto.generateDek();
	final var wrongDek = EnvelopeCrypto.generateDek();
	final var ciphertext = EnvelopeCrypto.encryptWithDek("data".getBytes(StandardCharsets.UTF_8), dek, config);
	assertThatThrownBy(() -> EnvelopeCrypto.decryptWithDek(ciphertext, wrongDek, config))
		.isInstanceOf(BadPaddingException.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Truncated DEK-layer ciphertext is rejected")
    void truncatedCiphertextRejected() {
	final var config = new CryptoConfig();
	final var dek = EnvelopeCrypto.generateDek();
	assertThatThrownBy(() -> EnvelopeCrypto.decryptWithDek(new byte[5], dek, config)).isInstanceOf(Exception.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("SealedObject rejects non-AEAD transformations")
    void sealedObjectRejectsWeakTransformation() {
	final var data = "secret".getBytes(StandardCharsets.UTF_8);
	final var key = new byte[16];
	assertThatThrownBy(() -> Crypto.generateSealedObject(data, key, "DES/ECB/PKCS5Padding", "DES"))
		.isInstanceOf(Exception.class);
    }
}
