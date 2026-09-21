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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.DELIMITER;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.MAX_FILE_SIZE;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.cipherAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.keyAES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.SealedObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.util.SerializationUtils;

/**
 * Unit tests for storage and memory-hardening fixes.
 */
@Tag("unit")
@DisplayName("Storage Hardening Unit Tests")
class StorageHardeningTest {

    @TempDir
    java.nio.file.Path tempDir;

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("CharArrayString.clear wipes the internal buffer")
    void charArrayStringClearWipesBuffer() throws Exception {
	final var cas = new CharArrayString("supersecret");
	assertThat(cas.toCharArray()).isEqualTo("supersecret".toCharArray());
	cas.clear();
	assertThat(cas.toCharArray()).isEmpty();
	final var bufField = CharArrayString.class.getDeclaredField("buf");
	bufField.setAccessible(true);
	final var buf = (char[]) bufField.get(cas);
	for (final char c : buf) {
	    assertThat(c).isEqualTo(Character.MIN_VALUE);
	}
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("SensitiveData setters zero the previous value")
    void sensitiveDataSettersZeroOld() {
	final var data = new ConfigData().getSensitiveData();
	final var oldDek = new byte[] { 1, 2, 3, 4 };
	data.setDek(oldDek);
	final var newDek = new byte[] { 5, 6, 7, 8 };
	data.setDek(newDek);
	assertThat(oldDek).containsExactly(0, 0, 0, 0);
	assertThat(data.getDek()).isSameAs(newDek);
	final var oldPwd = new char[] { 'a', 'b' };
	data.setKeyStorePassword(oldPwd);
	data.setKeyStorePassword(new char[] { 'x', 'y' });
	assertThat(oldPwd).containsExactly(Character.MIN_VALUE, Character.MIN_VALUE);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Serialization round-trip works and filter rejects gadgets")
    void serializationFilterWorks() throws Exception {
	final var container = new ByteContainer(new byte[] { 9, 8, 7 });
	final var bytes = SerializationUtils.serialize(container);
	final var back = (ByteContainer) SerializationUtils.deserialize(bytes);
	assertThat(back.getData()).isEqualTo(new byte[] { 9, 8, 7 });
	// Random bytes must not deserialize to a live object.
	assertThatThrownBy(() -> SerializationUtils.deserialize(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }))
		.isInstanceOf(RuntimeException.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("SealedObject(ByteContainer) round-trips through the allowlist")
    void sealedObjectRoundTrip() throws Exception {
	final var expected = "sealed-secret".getBytes(StandardCharsets.UTF_8);
	final var keyBytes = new byte[32];
	new SecureRandom().nextBytes(keyBytes);
	final var sealed = Crypto.generateSealedObject(expected.clone(), keyBytes, cipherAES, keyAES);
	final var bytes = SerializationUtils.serialize(sealed);
	final var back = (SealedObject) SerializationUtils.deserialize(bytes);
	final var container = (ByteContainer) back.getObject(Crypto.getSecretKey(keyBytes, keyAES));
	assertThat(container.getData()).isEqualTo(expected);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Deserialization rejects non-allowlisted types")
    void deserializeRejectsGadgets() throws Exception {
	final var map = new HashMap<String, String>();
	map.put("k", "v");
	final var bytes = SerializationUtils.serialize(map);
	assertThatThrownBy(() -> SerializationUtils.deserialize(bytes)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("IO.open rejects oversized files")
    void openRejectsOversized() throws Exception {
	final var big = tempDir.resolve("big.json");
	Files.write(big, new byte[MAX_FILE_SIZE + 1]);
	assertThatThrownBy(() -> IO.open(big.toString())).isInstanceOf(IOException.class);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("CappedStream fails past its limit")
    void cappedStreamEnforcesLimit() throws Exception {
	final var data = new byte[100];
	new SecureRandom().nextBytes(data);
	try (final var ok = new IO.CappedStream(new ByteArrayInputStream(data), 100)) {
	    assertThat(ok.readAllBytes()).isEqualTo(data);
	}
	try (final var capped = new IO.CappedStream(new ByteArrayInputStream(data), 10)) {
	    assertThatThrownBy(capped::readAllBytes).isInstanceOf(IOException.class);
	}
    }

    @Test
    @DisplayName("Atomic save writes readable content")
    void atomicSaveWritesContent() throws Exception {
	final var target = tempDir.resolve("vault.json");
	final var content = "{\"app\":\"test\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
	IO.save(target.toString(), content);
	assertThat(Files.readAllBytes(target)).isEqualTo(content);
	// Overwrite must also be atomic and preserve content.
	final var content2 = "{\"app\":\"test2\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
	IO.save(target.toString(), content2);
	assertThat(Files.readAllBytes(target)).isEqualTo(content2);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("ConfigData setters clamp to safe ranges")
    void configDataClamps() {
	final var cData = new ConfigData();
	cData.setAutoLockTime(-5);
	assertThat(cData.getAutoLockTime()).isEqualTo(1);
	cData.setAutoLockTime(1000);
	assertThat(cData.getAutoLockTime()).isEqualTo(60);
	cData.setClearPassword(0);
	assertThat(cData.getClearPassword()).isEqualTo(5);
	cData.setClearPassword(10000);
	assertThat(cData.getClearPassword()).isEqualTo(300);
	cData.setPasswordMinLength(1);
	assertThat(cData.getPasswordMinLength()).isEqualTo(8);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Vault format newer than known fails closed")
    void unsupportedVaultVersionRejected() throws Exception {
	final var json = """
		{"appName":"SecPwdMan","formatVersion":99,"encryptedData":"eA==","encryptedDEK":"eQ=="}
		""";
	final var cData = new ConfigData();
	try (final var is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
	    assertThatThrownBy(() -> JsonUtil.setJsonFile(cData, is)).isInstanceOf(IllegalArgumentException.class);
	}
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("ConfigData divider rejects CSV-breaking characters")
    void dividerValidation() {
	final var cData = new ConfigData();
	cData.setDivider(';');
	assertThat(cData.getDivider()).isEqualTo(';');
	cData.setDivider('|');
	assertThat(cData.getDivider()).isEqualTo('|');
	cData.setDivider('\t');
	assertThat(cData.getDivider()).isEqualTo('\t');
	cData.setDivider('"');
	assertThat(cData.getDivider()).isEqualTo(DELIMITER);
	cData.setDivider('\n');
	assertThat(cData.getDivider()).isEqualTo(DELIMITER);
	cData.setDivider('\r');
	assertThat(cData.getDivider()).isEqualTo(DELIMITER);
	cData.setDivider('\0');
	assertThat(cData.getDivider()).isEqualTo(DELIMITER);
    }
}
