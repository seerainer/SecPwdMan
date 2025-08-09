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
package io.github.seerainer.secpwdman.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;

/**
 * Utility class for test configuration and data management. Provides common
 * functionality for all test classes.
 */
public class TestUtils {

    private static final Properties TEST_PROPERTIES = new Properties();

    static {
	try (var is = TestUtils.class.getResourceAsStream("/test.properties")) {
	    if (is != null) {
		TEST_PROPERTIES.load(is);
	    }
	} catch (final IOException e) {
	    System.err.println("Failed to load test properties: " + e.getMessage());
	}
    }

    private TestUtils() {
    }

    /**
     * Clear sensitive data from byte array.
     *
     * @param data the byte array to clear
     */
    public static void clearSensitiveData(final byte[] data) {
	if (data != null) {
	    Arrays.fill(data, (byte) 0);
	}
    }

    /**
     * Clear sensitive data from char array.
     *
     * @param data the char array to clear
     */
    public static void clearSensitiveData(final char[] data) {
	if (data != null) {
	    Arrays.fill(data, '\0');
	}
    }

    /**
     * Generate random test data.
     *
     * @param size the size in bytes
     * @return random byte array
     */
    public static byte[] generateRandomTestData(final int size) {
	final var data = new byte[size];
	new SecureRandom().nextBytes(data);
	return data;
    }

    /**
     * Generate test file name with prefix.
     *
     * @param suffix the file suffix
     * @return test file name
     */
    public static String generateTestFileName(final String suffix) {
	final var prefix = getTestProperty("test.file.temp.prefix", "test-");
	return new StringBuilder().append(prefix).append(System.currentTimeMillis()).append(".").append(suffix)
		.toString();
    }

    /**
     * Get crypto test parameter.
     *
     * @param parameter the parameter name
     * @return parameter value as integer
     */
    public static int getCryptoTestParameter(final String parameter) {
	return getTestPropertyAsInt("test.crypto." + parameter, 1000);
    }

    /**
     * Get performance threshold for a specific operation.
     *
     * @param operation the operation name (encryption, decryption, kdf)
     * @return threshold in milliseconds
     */
    public static long getPerformanceThreshold(final String operation) {
	return getTestPropertyAsLong(
		new StringBuilder().append("test.performance.").append(operation).append(".threshold").toString(),
		5000);
    }

    /**
     * Get security test parameter.
     *
     * @param parameter the parameter name
     * @return parameter value as double
     */
    public static double getSecurityTestParameter(final String parameter) {
	return getTestPropertyAsDouble("test.security." + parameter, 1.0);
    }

    /**
     * Get test data sizes from configuration.
     *
     * @return array of test data sizes
     */
    public static int[] getTestDataSizes() {
	final var sizes = getTestProperty("test.data.sizes", "1024,8192,65536");
	final var parts = sizes.split(",");
	final var result = new int[parts.length];
	for (var i = 0; i < parts.length; i++) {
	    result[i] = Integer.parseInt(parts[i].trim());
	}
	return result;
    }

    /**
     * Get test password by strength level.
     *
     * @param strength the strength level (weak, medium, strong)
     * @return test password
     */
    public static String getTestPassword(final String strength) {
	return getTestProperty("test.password." + strength, "DefaultTestPassword123!");
    }

    /**
     * Get test password as byte array.
     *
     * @param strength the strength level (weak, medium, strong)
     * @return test password as byte array
     */
    public static byte[] getTestPasswordBytes(final String strength) {
	return getTestPassword(strength).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Get a test property value.
     *
     * @param key the property key
     * @return the property value or null if not found
     */
    public static String getTestProperty(final String key) {
	return TEST_PROPERTIES.getProperty(key);
    }

    /**
     * Get a test property value with default.
     *
     * @param key          the property key
     * @param defaultValue the default value if property is not found
     * @return the property value or default value
     */
    public static String getTestProperty(final String key, final String defaultValue) {
	return TEST_PROPERTIES.getProperty(key, defaultValue);
    }

    /**
     * Get a test property as double.
     *
     * @param key          the property key
     * @param defaultValue the default value if property is not found
     * @return the property value as double or default value
     */
    public static double getTestPropertyAsDouble(final String key, final double defaultValue) {
	final var value = TEST_PROPERTIES.getProperty(key);
	if (value == null) {
	    return defaultValue;
	}
	try {
	    return Double.parseDouble(value);
	} catch (final NumberFormatException e) {
	    return defaultValue;
	}
    }

    /**
     * Get a test property as integer.
     *
     * @param key          the property key
     * @param defaultValue the default value if property is not found
     * @return the property value as integer or default value
     */
    public static int getTestPropertyAsInt(final String key, final int defaultValue) {
	final var value = TEST_PROPERTIES.getProperty(key);
	if (value == null) {
	    return defaultValue;
	}
	try {
	    return Integer.parseInt(value);
	} catch (final NumberFormatException e) {
	    return defaultValue;
	}
    }

    /**
     * Get a test property as long.
     *
     * @param key          the property key
     * @param defaultValue the default value if property is not found
     * @return the property value as long or default value
     */
    public static long getTestPropertyAsLong(final String key, final long defaultValue) {
	final var value = TEST_PROPERTIES.getProperty(key);
	if (value == null) {
	    return defaultValue;
	}
	try {
	    return Long.parseLong(value);
	} catch (final NumberFormatException e) {
	    return defaultValue;
	}
    }

    /**
     * Get timeout multiplier for CI environments.
     *
     * @return timeout multiplier
     */
    public static int getTimeoutMultiplier() {
	return isRunningInCI() ? 3 : 1;
    }

    /**
     * Check if we're running in CI environment.
     *
     * @return true if running in CI
     */
    public static boolean isRunningInCI() {
	return System.getenv("CI") != null || System.getenv("GITHUB_ACTIONS") != null
		|| System.getenv("JENKINS_URL") != null;
    }

    /**
     * Load test data from JSON file.
     *
     * @return test data as string
     */
    public static String loadTestData() {
	try (var is = TestUtils.class.getResourceAsStream("/test-data.json")) {
	    if (is != null) {
		return new String(is.readAllBytes(), StandardCharsets.UTF_8);
	    }
	} catch (final IOException e) {
	    System.err.println("Failed to load test data: " + e.getMessage());
	}
	return "{}";
    }

    /**
     * Print performance information to console.
     *
     * @param operation the operation name
     * @param duration  the duration in milliseconds
     * @param dataSize  the data size in bytes
     */
    public static void printPerformanceInfo(final String operation, final long duration, final int dataSize) {
	final var throughput = (double) dataSize / duration * 1000.0 / (1024 * 1024); // MB/s
	System.out.println(new StringBuilder().append("[PERF] ").append(operation).append(": ").append(duration)
		.append("ms, ").append("%.2f".formatted(Double.valueOf(throughput))).append(" MB/s (data size: ")
		.append(dataSize).append(" bytes)").toString());
    }

    /**
     * Print security test result to console.
     *
     * @param testName  the test name
     * @param result    the test result
     * @param threshold the threshold value
     */
    public static void printSecurityTestResult(final String testName, final double result, final double threshold) {
	final var status = result <= threshold ? "PASS" : "FAIL";
	System.out.println(new StringBuilder().append("[SEC] ").append(testName).append(": ")
		.append("%.3f".formatted(Double.valueOf(result))).append(" (threshold: ")
		.append("%.3f".formatted(Double.valueOf(threshold))).append(") - ").append(status).toString());
    }

    /**
     * Print test information to console.
     *
     * @param testName the test name
     * @param info     additional information
     */
    public static void printTestInfo(final String testName, final String info) {
	System.out.println(new StringBuilder().append("[TEST] ").append(testName).append(": ").append(info).toString());
    }
}
