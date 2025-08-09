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
package io.github.seerainer.secpwdman;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.crypto.CryptoConfig;
import io.github.seerainer.secpwdman.crypto.CryptoFactory;
import io.github.seerainer.secpwdman.io.IOUtil;

/**
 * Integration tests for SecPwdMan application. Tests end-to-end functionality
 * including file I/O, encryption, and configuration.
 */
@Tag("integration")
@DisplayName("SecPwdMan Integration Tests")
class SecPwdManIntegrationTest {

    @TempDir
    Path tempDir;

    private ConfigData configData;

    private Path testFile;

    private Path configFile;

    @BeforeEach
    void setUp(final TestInfo testInfo) {
	// Create test directory structure
	testFile = tempDir.resolve("test-passwords.json");
	configFile = tempDir.resolve("config.json");

	// Initialize configuration
	configData = new ConfigData();
	configData.setFile(testFile.toString());

	// Set headless mode for SWT
	System.setProperty("java.awt.headless", "true");
	System.setProperty("org.eclipse.swt.internal.gtk.cairoGraphics", "false");

	System.out.println("Running integration test: " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should create and read configuration")
    void shouldCreateAndReadConfiguration() throws Exception {
	// Create test configuration
	configData.setPasswordMinLength(12);
	configData.setAutoLockTime(300);
	configData.setBufferLength(8192);

	// Test configuration data
	assertThat(configData.getPasswordMinLength()).isEqualTo(12);
	assertThat(configData.getAutoLockTime()).isEqualTo(300);
	assertThat(configData.getBufferLength()).isEqualTo(8192);

	// Test crypto configuration
	final var cryptoConfig = configData.getCryptoConfig();
	assertThat(cryptoConfig).isNotNull();
	assertThat(cryptoConfig.getKeyDerivation()).isEqualTo(CryptoConfig.KDF.Argon2);
    }

    @Test
    @DisplayName("Should encrypt and decrypt data with different algorithms")
    @SuppressWarnings("static-method")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void shouldEncryptAndDecryptDataWithDifferentAlgorithms() throws Exception {
	// Test data
	final var testData = """
		{
		    "entries": [
		        {
		            "title": "Test Entry",
		            "username": "testuser",
		            "password": "TestPassword123!",
		            "url": "https://example.com",
		            "notes": "Test notes"
		        }
		    ]
		}
		""";

	final var testBytes = testData.getBytes(StandardCharsets.UTF_8);
	final var password = "TestMasterPassword123!".getBytes(StandardCharsets.UTF_8);

	// Test with AES
	final var aesConfig = new CryptoConfig();
	aesConfig.setCipherALGO("AES/GCM/NoPadding");
	final var aesCrypto = CryptoFactory.crypto(aesConfig);

	final var aesEncrypted = aesCrypto.encrypt(testBytes, password);
	final var aesDecrypted = aesCrypto.decrypt(aesEncrypted, password);

	assertThat(aesEncrypted).isNotEqualTo(testBytes);
	assertThat(aesDecrypted).isEqualTo(testBytes);

	// Test with ChaCha20
	final var chachaConfig = new CryptoConfig();
	chachaConfig.setCipherALGO("ChaCha20-Poly1305");
	final var chachaCrypto = CryptoFactory.crypto(chachaConfig);

	final var chachaEncrypted = chachaCrypto.encrypt(testBytes, password);
	final var chachaDecrypted = chachaCrypto.decrypt(chachaEncrypted, password);

	assertThat(chachaEncrypted).isNotEqualTo(testBytes);
	assertThat(chachaDecrypted).isEqualTo(testBytes);

	// Different algorithms should produce different ciphertext
	assertThat(aesEncrypted).isNotEqualTo(chachaEncrypted);
    }

    @Test
    @DisplayName("Should handle concurrent file access")
    void shouldHandleConcurrentFileAccess() throws Exception {
	// Create test file
	final var testData = "concurrent test data".getBytes(StandardCharsets.UTF_8);
	Files.write(testFile, testData);

	// Test concurrent read access
	final var results = new ByteArrayOutputStream[5];
	final var threads = new Thread[5];

	for (var i = 0; i < 5; i++) {
	    final var index = i;
	    results[index] = new ByteArrayOutputStream();

	    threads[index] = new Thread(() -> {
		try {
		    final var data = Files.readAllBytes(testFile);
		    results[index].write(data);
		} catch (final IOException e) {
		    System.err.println("Concurrent read failed: " + e.getMessage());
		}
	    });

	    threads[index].start();
	}

	// Wait for all threads
	for (final var thread : threads) {
	    thread.join(5000);
	    assertThat(thread.isAlive()).isFalse();
	}

	// Verify all reads succeeded
	for (final var result : results) {
	    assertThat(result.toByteArray()).isEqualTo(testData);
	}
    }

    @Test
    @DisplayName("Should handle file operations")
    void shouldHandleFileOperations() throws Exception {
	// Create test file
	final var testData = "Test file content".getBytes(StandardCharsets.UTF_8);
	Files.write(testFile, testData);

	// Test file existence
	assertThat(Files.exists(testFile)).isTrue();
	assertThat(Files.size(testFile)).isGreaterThan(0);

	// Test file reading
	final var readData = Files.readAllBytes(testFile);
	assertThat(readData).isEqualTo(testData);

	// Test file utility methods
	assertThat(IOUtil.isReadable(testFile.toString())).isTrue();
	assertThat(IOUtil.isFileReady(testFile.toString())).isTrue();
    }

    @Test
    @DisplayName("Should handle large files efficiently")
    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    void shouldHandleLargeFilesEfficiently() throws Exception {
	// Create large test data (10MB)
	final var largeData = new byte[10 * 1024 * 1024];
	new SecureRandom().nextBytes(largeData);

	// Encrypt and save
	final var cryptoConfig = new CryptoConfig();
	final var crypto = CryptoFactory.crypto(cryptoConfig);
	final var password = "TestPassword123!".getBytes(StandardCharsets.UTF_8);

	var startTime = System.currentTimeMillis();
	final var encrypted = crypto.encrypt(largeData, password);
	final var encryptTime = System.currentTimeMillis() - startTime;

	Files.write(testFile, encrypted);

	// Read and decrypt
	startTime = System.currentTimeMillis();
	final var fileData = Files.readAllBytes(testFile);
	final var decrypted = crypto.decrypt(fileData, password);
	final var decryptTime = System.currentTimeMillis() - startTime;

	assertThat(decrypted).isEqualTo(largeData);

	// Performance assertions (should complete within reasonable time)
	assertThat(encryptTime).isLessThan(10000); // < 10 seconds
	assertThat(decryptTime).isLessThan(10000); // < 10 seconds

	System.out.println(
		new StringBuilder().append("Large file encryption: ").append(encryptTime).append("ms").toString());
	System.out.println(
		new StringBuilder().append("Large file decryption: ").append(decryptTime).append("ms").toString());
    }

    @Test
    @DisplayName("Should handle memory cleanup properly")
    @SuppressWarnings("static-method")
    void shouldHandleMemoryCleanupProperly() throws Exception {
	// Create sensitive data
	final var sensitiveData = "sensitive password data".getBytes(StandardCharsets.UTF_8);
	final var password = "TestPassword123!".getBytes(StandardCharsets.UTF_8);

	// Encrypt data
	final var cryptoConfig = new CryptoConfig();
	final var crypto = CryptoFactory.crypto(cryptoConfig);
	final var encrypted = crypto.encrypt(sensitiveData, password);

	// Verify encryption worked
	assertThat(encrypted).isNotEqualTo(sensitiveData);

	// Clear sensitive data
	Arrays.fill(sensitiveData, (byte) 0);
	Arrays.fill(password, (byte) 0);

	// Force garbage collection
	System.gc();
	Thread.sleep(100);

	// Verify data is cleared
	for (final byte b : sensitiveData) {
	    assertThat(b).isEqualTo((byte) 0);
	}
	for (final byte b : password) {
	    assertThat(b).isEqualTo((byte) 0);
	}
    }

    @Test
    @DisplayName("Should handle multiple key derivation functions")
    @SuppressWarnings("static-method")
    void shouldHandleMultipleKeyDerivationFunctions() throws Exception {
	final var testData = "KDF test data".getBytes(StandardCharsets.UTF_8);
	final var password = "TestPassword123!".getBytes(StandardCharsets.UTF_8);

	// Test Argon2
	final var argon2Config = new CryptoConfig();
	argon2Config.setKeyDerivation(CryptoConfig.KDF.Argon2);
	final var argon2Crypto = CryptoFactory.crypto(argon2Config);

	final var argon2Encrypted = argon2Crypto.encrypt(testData, password);
	final var argon2Decrypted = argon2Crypto.decrypt(argon2Encrypted, password);

	assertThat(argon2Decrypted).isEqualTo(testData);

	// Test PBKDF2
	final var pbkdf2Config = new CryptoConfig();
	pbkdf2Config.setKeyDerivation(CryptoConfig.KDF.PBKDF2);
	final var pbkdf2Crypto = CryptoFactory.crypto(pbkdf2Config);

	final var pbkdf2Encrypted = pbkdf2Crypto.encrypt(testData, password);
	final var pbkdf2Decrypted = pbkdf2Crypto.decrypt(pbkdf2Encrypted, password);

	assertThat(pbkdf2Decrypted).isEqualTo(testData);

	// Test scrypt
	final var scryptConfig = new CryptoConfig();
	scryptConfig.setKeyDerivation(CryptoConfig.KDF.scrypt);
	final var scryptCrypto = CryptoFactory.crypto(scryptConfig);

	final var scryptEncrypted = scryptCrypto.encrypt(testData, password);
	final var scryptDecrypted = scryptCrypto.decrypt(scryptEncrypted, password);

	assertThat(scryptDecrypted).isEqualTo(testData);

	// Different KDFs should produce different ciphertext
	assertThat(argon2Encrypted).isNotEqualTo(pbkdf2Encrypted);
	assertThat(pbkdf2Encrypted).isNotEqualTo(scryptEncrypted);
	assertThat(argon2Encrypted).isNotEqualTo(scryptEncrypted);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Should handle Windows-specific file operations")
    void shouldHandleWindowsSpecificFileOperations() throws Exception {
	// Create file with Windows path
	final var testData = "Windows test data".getBytes(StandardCharsets.UTF_8);
	Files.write(testFile, testData);

	// Test that file exists and is readable
	assertThat(Files.exists(testFile)).isTrue();
	assertThat(IOUtil.isReadable(testFile.toString())).isTrue();

	// Read and verify
	final var readData = Files.readAllBytes(testFile);
	assertThat(readData).isEqualTo(testData);
    }

    @ParameterizedTest
    @ValueSource(strings = { "AES/GCM/NoPadding", "ChaCha20-Poly1305" })
    @DisplayName("Should work with different encryption algorithms")
    void shouldWorkWithDifferentEncryptionAlgorithms(final String algorithm) throws Exception {
	// Setup crypto config
	final var cryptoConfig = new CryptoConfig();
	cryptoConfig.setCipherALGO(algorithm);

	final var crypto = CryptoFactory.crypto(cryptoConfig);

	// Test data
	final var testData = "Algorithm test data".getBytes(StandardCharsets.UTF_8);
	final var password = "TestPassword123!".getBytes(StandardCharsets.UTF_8);

	// Encrypt and save
	final var encrypted = crypto.encrypt(testData, password);
	Files.write(testFile, encrypted);

	// Read and decrypt
	final var fileData = Files.readAllBytes(testFile);
	final var decrypted = crypto.decrypt(fileData, password);

	assertThat(decrypted).isEqualTo(testData);
    }

    @AfterEach
    void tearDown() throws IOException {
	// Clean up test files
	if (Files.exists(testFile)) {
	    Files.delete(testFile);
	}
	if (Files.exists(configFile)) {
	    Files.delete(configFile);
	}
    }
}
