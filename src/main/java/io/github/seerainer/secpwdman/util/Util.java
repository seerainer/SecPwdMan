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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * The class Util.
 */
public final class Util {

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
	 * Get the absolute pathname.
	 *
	 * @param file the string file
	 * @return absolutePath
	 */
	public static String getFilePath(final String file) {
		return new File(file).getAbsolutePath();
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

	/**
	 * Checks if a file is not empty and readable.
	 *
	 * @param filePath the file path
	 * @return true if the file is ready, false otherwise
	 */
	public static boolean isFileReady(final String filePath) {
		return !isBlank(filePath) && isReadable(filePath);
	}

	/**
	 * Checks if a file is readable.
	 *
	 * @param filePath the file path
	 * @return true if the file is readable, false otherwise
	 */
	public static boolean isReadable(final String filePath) {
		final var file = new File(filePath);
		return (file.exists() && file.canRead() && file.canWrite() && file.isFile());
	}

	/**
	 * Converts a char array to a byte array using UTF-8 encoding.
	 *
	 * @param chars the char array
	 * @return the byte array
	 */
	public static byte[] toBytes(final char[] chars) {
		final var charBuffer = CharBuffer.wrap(chars);
		final var byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		final var bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		clear(charBuffer.array());
		clear(byteBuffer.array());
		return bytes;
	}

	/**
	 * Converts a byte array to a char array using UTF-8 encoding.
	 *
	 * @param bytes the byte array
	 * @return the char array
	 */
	public static char[] toChars(final byte[] bytes) {
		final var byteBuffer = ByteBuffer.wrap(bytes);
		final var charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
		final var chars = new char[byteBuffer.remaining()];
		charBuffer.get(chars);
		clear(charBuffer.array());
		clear(byteBuffer.array());
		return chars;
	}

	private Util() {
	}
}
