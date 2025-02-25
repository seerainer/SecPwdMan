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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;

/**
 * The class CharsetUtil.
 */
public class CharsetUtil implements PrimitiveConstants {

	private static void clearBuffer(final ByteBuffer buffer) {
		while (buffer.hasRemaining()) {
			buffer.put((byte) 0);
		}
		buffer.clear();
	}

	private static void clearBuffer(final CharBuffer buffer) {
		while (buffer.hasRemaining()) {
			buffer.put(Character.MIN_VALUE);
		}
		buffer.clear();
	}

	/**
	 * Creates a new array with all occurrences of a target sequence replaced.
	 *
	 * @param array       The original char array
	 * @param target      The target character sequence
	 * @param replacement The replacement character sequence
	 * @return A new char array with replacements
	 */
	public static char[] replaceSequence(final char[] array, final char[] target, final char[] replacement) {
		if (array == null || target == null || target.length == 0) {
			return array;
		}

		// First, count occurrences to determine new array size
		var count = 0;
		for (var i = 0; i <= array.length - target.length; i++) {
			var found = true;
			for (var j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					found = false;
					break;
				}
			}
			if (found) {
				count++;
				i += target.length - 1;
			}
		}

		// Calculate new array size
		final var newLength = array.length + count * (replacement.length - target.length);
		final var result = new char[newLength];

		// Perform replacements
		var writeIndex = 0;
		var readIndex = 0;
		while (readIndex < array.length) {
			var found = false;
			if (readIndex <= array.length - target.length) {
				found = true;
				for (var j = 0; j < target.length; j++) {
					if (array[readIndex + j] != target[j]) {
						found = false;
						break;
					}
				}
			}

			if (found) {
				// Copy replacement
				System.arraycopy(replacement, 0, result, writeIndex, replacement.length);
				writeIndex += replacement.length;
				readIndex += target.length;
			} else {
				// Copy original character
				result[writeIndex++] = array[readIndex++];
			}
		}

		return result;
	}

	/**
	 * Converts a char array to a byte array using UTF-8 encoding.
	 *
	 * @param chars the char array
	 * @return the byte array
	 */
	public static byte[] toBytes(final char[] chars) {
		final var charBuffer = CharBuffer.wrap(chars);
		final var bytes = toBytes(charBuffer);
		clearBuffer(charBuffer);
		Util.clear(chars);
		return bytes;
	}

	private static byte[] toBytes(final CharBuffer charBuffer) {
		final var byteBuffer = ByteBuffer.allocateDirect(charBuffer.remaining() * UTF8_BYTES);
		StandardCharsets.UTF_8.newEncoder().encode(charBuffer, byteBuffer, true);
		byteBuffer.flip();
		final var bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		clearBuffer(byteBuffer);
		return bytes;
	}

	/**
	 * Converts a StringBuilder to a byte array using UTF-8 encoding.
	 *
	 * @param sb the StringBuilder
	 * @return the byte array
	 */
	public static byte[] toBytes(final StringBuilder sb) {
		final var charBuffer = CharBuffer.wrap(sb);
		final var bytes = toBytes(charBuffer);
		clearBuffer(charBuffer);
		sb.setLength(0);
		return bytes;
	}

	/**
	 * Converts a byte array to a char array using UTF-8 encoding.
	 *
	 * @param bytes the byte array
	 * @return the char array
	 */
	public static char[] toChars(final byte[] bytes) {
		final var byteBuffer = ByteBuffer.allocateDirect(bytes.length * UTF8_BYTES);
		byteBuffer.put(bytes);
		byteBuffer.flip();
		final var charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
		final var chars = new char[charBuffer.remaining()];
		charBuffer.get(chars);
		clearBuffer(byteBuffer);
		clearBuffer(charBuffer);
		Util.clear(bytes);
		return chars;
	}

	private CharsetUtil() {
	}
}
