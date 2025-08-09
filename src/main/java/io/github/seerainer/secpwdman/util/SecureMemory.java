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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Function;

import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * Utility class for secure native memory operations.
 *
 * <p>
 * This class provides methods to store sensitive data in native (off-heap)
 * memory and ensures proper zeroing before release to prevent sensitive data
 * from remaining in memory longer than necessary.
 * </p>
 *
 * <p>
 * Key security features:
 * <ul>
 * <li>Native memory allocation (off-heap)</li>
 * <li>Automatic zeroing on close</li>
 * <li>Scoped lifetime management</li>
 * <li>No heap intermediaries for sensitive operations</li>
 * </ul>
 * </p>
 */
public class SecureMemory implements StringConstants {

    private static final Logger LOG = LogFactory.getLog();

    private SecureMemory() {
    }

    /**
     * Reads data from native memory into a new byte array.
     *
     * @param segment the memory segment to read from
     * @return a new byte array containing the data
     */
    public static byte[] readFromNative(final MemorySegment segment) {
	final var length = (int) segment.byteSize();
	final var result = new byte[length];
	MemorySegment.copy(segment, ValueLayout.JAVA_BYTE, 0, result, 0, length);
	return result;
    }

    /**
     * Executes an operation with a secret stored in native memory using Arena. The
     * memory is automatically zeroed and released when the operation completes.
     *
     * @param <T>        the return type of the operation
     * @param secretData the sensitive data to store in native memory
     * @param operation  the operation to perform with the native memory segment
     * @return the result of the operation
     * @throws RuntimeException if the operation fails
     */
    public static <T> T withSecretMemory(final byte[] secretData, final Function<MemorySegment, T> operation) {
	if (secretData == null || secretData.length == 0) {
	    throw new IllegalArgumentException(ERR_SECRET_NULL_OR_EMPTY);
	}
	try (var arena = Arena.ofConfined()) {
	    final var segment = arena.allocate(secretData.length, 1);
	    try {
		MemorySegment.copy(secretData, 0, segment, ValueLayout.JAVA_BYTE, 0, secretData.length);
		return operation.apply(segment);
	    } finally {
		zeroMemory(segment);
	    }
	} catch (final Exception e) {
	    LOG.error(ERR_SECURE_MEMORY_OP, e);
	    throw new RuntimeException(ERR_SECURE_MEMORY_FAILED, e);
	} finally {
	    Util.clear(secretData);
	}
    }

    private static void zeroMemory(final MemorySegment segment) {
	try {
	    segment.fill((byte) 0);
	} catch (final Exception e) {
	    LOG.warn(WARN_ZERO_NATIVE_MEMORY, e);
	}
    }
}
