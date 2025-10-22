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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class Util.
 */
public class Util implements StringConstants {

    private Util() {
    }

    /**
     * Clear byte array.
     *
     * @param bytes the byte array
     */
    public static void clear(final byte[] bytes) {
	if (nonNull(bytes)) {
	    Arrays.fill(bytes, (byte) 0);
	}
    }

    /**
     * Clear char array.
     *
     * @param chars the char array
     */
    public static void clear(final char[] chars) {
	if (nonNull(chars)) {
	    Arrays.fill(chars, Character.MIN_VALUE);
	}
    }

    private static int compareVersions(final String v1, final String v2) {
	final var core1 = v1.split(minus)[0].split(replus)[0];
	final var core2 = v2.split(minus)[0].split(replus)[0];
	final var parts1 = core1.split(redot);
	final var parts2 = core2.split(redot);
	final var length = Math.max(parts1.length, parts2.length);

	for (var i = 0; i < length; i++) {
	    final var num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
	    final var num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
	    if (num1 != num2) {
		return Integer.compare(num1, num2);
	    }
	}
	return 0;
    }

    /**
     * Get the base64 decoded bytes.
     *
     * @param data the byte array data
     * @return base64Bytes
     */
    public static byte[] getBase64Decode(final byte[] data) {
	if (isNull(data)) {
	    return null;
	}
	try {
	    return Base64.getDecoder().decode(data);
	} catch (final IllegalArgumentException e) {
	    return null;
	}
    }

    /**
     * Get the base64 encoded bytes.
     *
     * @param data the byte array data
     * @return base64Bytes
     */
    public static byte[] getBase64Encode(final byte[] data) {
	if (isNull(data)) {
	    return null;
	}
	return Base64.getEncoder().encode(data);
    }

    /**
     * Get random UUID in upper case.
     *
     * @return randomUUID
     */
    public static String getUUID() {
	return UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
    }

    /**
     * Checks if string is blank.
     *
     * @param s the string
     * @return true, if string is blank
     */
    public static boolean isBlank(final String s) {
	return isNull(s) || s.isBlank();
    }

    /**
     * Checks if two char arrays are equal.
     *
     * @param a the first char array
     * @param b the second char array
     * @return true if the arrays are equal, false otherwise
     */
    public static boolean isEqual(final char[] a, final char[] b) {
	return Arrays.equals(a, b);
    }

    /**
     * Checks if two object arrays are equal.
     *
     * @param a the first object array
     * @param b the second object array
     * @return true if the arrays are equal, false otherwise
     */
    public static boolean isEqual(final Object[] a, final Object[] b) {
	return Arrays.equals(a, b);
    }

    /**
     * Returns true if the given version is less than or equal to 1.0.0.
     * Pre-releases (e.g. 1.0.0-rc.2) are considered lower than their core version.
     *
     * @param version the version string to check
     */
    public static boolean isOldVersion(final String version) {
	final var coreVersion = version.split(minus)[0].split(replus)[0];
	return compareVersions(coreVersion, MAJOR_VERSION) <= 0;
    }
}
