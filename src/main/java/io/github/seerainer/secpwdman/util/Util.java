/*
 * Secure Password Manager
 * Copyright (C) 2025  Philipp Seerainer
 * philipp@seerainer.com
 * https://www.seerainer.com/
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

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import io.github.seerainer.secpwdman.io.CharArrayString;

/**
 * The class Util.
 */
public class Util {

	/**
	 * Clear byte array.
	 *
	 * @param bytes the byte array
	 */
	public static void clear(final byte[] bytes) {
		if (bytes != null) {
			Arrays.fill(bytes, (byte) 0);
		}
	}

	/**
	 * Clear char array.
	 *
	 * @param chars the char array
	 */
	public static void clear(final char[] chars) {
		if (chars != null) {
			Arrays.fill(chars, Character.MIN_VALUE);
		}
	}

	/**
	 * Get the base64 char array.
	 *
	 * @param cas the char array string
	 * @return base64Char
	 */
	public static char[] getBase64Char(final CharArrayString cas) {
		try {
			return CharsetUtil.toChars(getBase64Decode(CharsetUtil.toBytes(cas.toCharArray())));
		} catch (final IllegalArgumentException e) {
			return cas.toCharArray();
		} finally {
			cas.clear();
		}
	}

	/**
	 * Get the base64 decoded bytes.
	 *
	 * @param data the byte array data
	 * @return base64Bytes
	 */
	public static byte[] getBase64Decode(final byte[] data) {
		if (data == null) {
			return null;
		}
		return Base64.getDecoder().decode(data);
	}

	/**
	 * Get the base64 encoded bytes.
	 *
	 * @param data the byte array data
	 * @return base64Bytes
	 */
	public static byte[] getBase64Encode(final byte[] data) {
		if (data == null) {
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
		return UUID.randomUUID().toString().toUpperCase();
	}

	/**
	 * Checks if string is blank.
	 *
	 * @param s the string
	 * @return true, if string is blank
	 */
	public static boolean isBlank(final String s) {
		return s == null || s.isBlank();
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

	private Util() {
	}
}
