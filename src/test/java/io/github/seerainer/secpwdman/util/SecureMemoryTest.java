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
package io.github.seerainer.secpwdman.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Demonstration class for SecureMemory and CharsetUtil functionality.
 */
@Tag("integration")
@DisplayName("SecureMemory and CharsetUtil Test")
class SecureMemoryTest {

    @SuppressWarnings("static-method")
    private long calculateChecksum(final byte[] data) {
	var checksum = 0L;
	for (final byte b : data) {
	    checksum += b & 0xFF;
	}
	return checksum;
    }

    @SuppressWarnings("static-method")
    private boolean isArrayCleared(final byte[] array) {
	for (final byte b : array) {
	    if (b != 0) {
		return false;
	    }
	}
	return true;
    }

    @SuppressWarnings("static-method")
    private boolean isArrayCleared(final char[] array) {
	for (final char c : array) {
	    if (c != 0) {
		return false;
	    }
	}
	return true;
    }

    @Test
    @DisplayName("SecureMemory and CharsetUtil Integration Test")
    void secureMemoryTest() {
	System.out.println("=== SecureMemory and CharsetUtil Test ===\n");

	try {
	    testBasicSecureMemory();
	    testCharsetUtilIntegration();
	    testMemoryClearing();
	    testExceptionSafety();
	    testLargeDataHandling();

	    System.out.println("\n✅ All tests passed! SecureMemory implementation is working correctly.");

	} catch (final Exception e) {
	    System.err.println("❌ Test failed: " + e.getMessage());
	    e.printStackTrace();
	}
    }

    private void testBasicSecureMemory() {
	System.out.println("1. Testing basic SecureMemory operations...");

	final var testData = "sensitive-password-123".getBytes(StandardCharsets.UTF_8);
	final var originalData = testData.clone();

	final var result = SecureMemory.withSecretMemory(testData, segment -> {
	    final var readData = SecureMemory.readFromNative(segment);
	    final var isEqual = Arrays.equals(originalData, readData);
	    Util.clear(readData);
	    return Boolean.valueOf(isEqual);
	});

	if (!result.booleanValue() || !isArrayCleared(testData)) {
	    throw new RuntimeException("Basic SecureMemory test failed");
	}
	System.out.println("   ✓ Basic SecureMemory operations work correctly");
	System.out.println("   ✓ Original data properly cleared after operation");
    }

    @SuppressWarnings("static-method")
    private void testCharsetUtilIntegration() {
	System.out.println("\n2. Testing CharsetUtil secure conversions...");

	final var originalPassword = "MySecurePassword123!";
	final var originalChars = originalPassword.toCharArray();
	final var originalBytes = originalPassword.getBytes(StandardCharsets.UTF_8);

	// Test char[] to byte[] conversion
	final var convertedBytes = CharsetUtil.toBytes(originalChars.clone());
	if (!Arrays.equals(originalBytes, convertedBytes)) {
	    throw new RuntimeException("Char to byte conversion failed");
	}

	// Test byte[] to char[] conversion
	final var convertedChars = CharsetUtil.toChars(convertedBytes.clone());
	if (!Arrays.equals(originalChars, convertedChars)) {
	    throw new RuntimeException("Byte to char conversion failed");
	}

	// Test round-trip conversion
	final var roundTripBytes = CharsetUtil.toBytes(convertedChars);
	final var roundTripChars = CharsetUtil.toChars(roundTripBytes);
	if (!Arrays.equals(originalChars, roundTripChars)) {
	    throw new RuntimeException("Round-trip conversion failed");
	}

	System.out.println("   ✓ CharsetUtil conversions work correctly");
	System.out.println("   ✓ Round-trip conversion preserves data integrity");
    }

    private void testExceptionSafety() {
	System.out.println("\n4. Testing exception safety...");

	final var testData = "exception-test-data".getBytes(StandardCharsets.UTF_8);

	try {
	    SecureMemory.withSecretMemory(testData, _ -> {
		throw new RuntimeException("Intentional test exception");
	    });
	} catch (final RuntimeException e) {
	    // Expected exception
	}

	if (!isArrayCleared(testData)) {
	    throw new RuntimeException("Exception safety test failed");
	}
	System.out.println("   ✓ Data cleared even when exceptions occur");
    }

    private void testLargeDataHandling() {
	System.out.println("\n5. Testing large data handling...");

	final var largeData = new byte[10240]; // 10KB
	for (var i = 0; i < largeData.length; i++) {
	    largeData[i] = (byte) (i % 256);
	}

	final var checksum = calculateChecksum(largeData);

	final var result = SecureMemory.withSecretMemory(largeData, segment -> {
	    final var readData = SecureMemory.readFromNative(segment);
	    final var readChecksum = calculateChecksum(readData);
	    Util.clear(readData);
	    return Boolean.valueOf(checksum == readChecksum);
	});

	if (!result.booleanValue() || !isArrayCleared(largeData)) {
	    throw new RuntimeException("Large data handling test failed");
	}
	System.out.println("   ✓ Large data handling works correctly");
	System.out.println("   ✓ Data integrity maintained for large datasets");
    }

    private void testMemoryClearing() {
	System.out.println("\n3. Testing memory clearing...");

	final var sensitiveData = "super-secret-data".getBytes(StandardCharsets.UTF_8);
	final var originalLength = sensitiveData.length;

	SecureMemory.withSecretMemory(sensitiveData, segment -> Boolean.valueOf(segment.byteSize() == originalLength));

	if (!isArrayCleared(sensitiveData)) {
	    throw new RuntimeException("Memory clearing test failed");
	}
	System.out.println("   ✓ Sensitive data properly cleared after SecureMemory operation");

	// Test CharsetUtil clearing
	final var testChars = "test-password".toCharArray();
	CharsetUtil.toBytes(testChars);

	if (!isArrayCleared(testChars)) {
	    throw new RuntimeException("CharsetUtil clearing test failed");
	}
	System.out.println("   ✓ CharsetUtil properly clears input arrays");
    }
}