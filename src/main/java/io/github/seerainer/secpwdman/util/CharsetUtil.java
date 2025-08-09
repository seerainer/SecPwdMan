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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class CharsetUtil - provides secure charset conversion operations. All
 * sensitive data operations use SecureMemory for proper security.
 */
public class CharsetUtil implements PrimitiveConstants, StringConstants {

    private CharsetUtil() {
    }

    private static void clearByteBuffer(final ByteBuffer buffer) {
	if (buffer.hasArray()) {
	    // For heap buffers, clear the backing array
	    final var array = buffer.array();
	    Util.clear(array);
	} else {
	    // For direct buffers, overwrite with zeros
	    buffer.clear();
	    while (buffer.hasRemaining()) {
		buffer.put((byte) 0);
	    }
	}
	buffer.clear();
    }

    private static void clearCharBuffer(final CharBuffer buffer) {
	if (buffer.hasArray()) {
	    // For heap buffers, clear the backing array
	    final var array = buffer.array();
	    Util.clear(array);
	} else {
	    // For direct buffers, overwrite with zeros
	    buffer.clear();
	    while (buffer.hasRemaining()) {
		buffer.put(Character.MIN_VALUE);
	    }
	}
	buffer.clear();
    }

    private static byte[] convertCharsToBytes(final char[] chars) {
	final var charBuffer = CharBuffer.wrap(chars);
	final var encoder = UTF_8.newEncoder();

	try {
	    final var byteBuffer = encoder.encode(charBuffer);
	    final var bytes = new byte[byteBuffer.remaining()];
	    byteBuffer.get(bytes);

	    clearCharBuffer(charBuffer);
	    clearByteBuffer(byteBuffer);

	    return bytes;
	} catch (final Exception e) {
	    clearCharBuffer(charBuffer);
	    throw new RuntimeException(SECURE_CHARSET_CONVERSION_FAILED, e);
	}
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
	if (isNull(array) || isNull(target) || target.length == 0) {
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
     * Securely converts a char array to a byte array using UTF-8 encoding. Uses
     * SecureMemory for intermediate operations to prevent sensitive data leakage.
     *
     * @param chars the char array
     * @return the byte array
     */
    public static byte[] toBytes(final char[] chars) {
	if (chars == null || chars.length == 0) {
	    return new byte[0];
	}

	final var tempBytes = convertCharsToBytes(chars);

	try {
	    return SecureMemory.withSecretMemory(tempBytes, segment -> {
		final var result = SecureMemory.readFromNative(segment);
		Util.clear(chars);
		return result;
	    });
	} finally {
	    Util.clear(tempBytes);
	}
    }

    /**
     * Securely converts a StringBuilder to a byte array using UTF-8 encoding. Uses
     * SecureMemory for intermediate operations.
     *
     * @param sb the StringBuilder
     * @return the byte array
     */
    public static byte[] toBytes(final StringBuilder sb) {
	if (sb == null || sb.length() == 0) {
	    return new byte[0];
	}
	final var chars = new char[sb.length()];
	sb.getChars(0, sb.length(), chars, 0);
	sb.setLength(0);
	return toBytes(chars);
    }

    /**
     * Securely converts a byte array to a char array using UTF-8 encoding. Uses
     * SecureMemory for intermediate operations to prevent sensitive data leakage.
     *
     * @param bytes the byte array
     * @return the char array
     */
    public static char[] toChars(final byte[] bytes) {
	if (bytes == null || bytes.length == 0) {
	    return new char[0];
	}

	return SecureMemory.withSecretMemory(bytes, segment -> {
	    final var sourceBytes = SecureMemory.readFromNative(segment);
	    try {
		final var charBuffer = UTF_8.decode(ByteBuffer.wrap(sourceBytes));
		final var chars = new char[charBuffer.remaining()];
		charBuffer.get(chars);

		clearCharBuffer(charBuffer);
		Util.clear(bytes);

		return chars;
	    } finally {
		Util.clear(sourceBytes);
	    }
	});
    }
}
