/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * The Class Util.
 */
public class Util {

//	public static final boolean WIN32 = System.getProperty("os.name").startsWith("Win"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Clear byte[].
	 *
	 * @param b the byte[]
	 */
	public static void clear(final byte[] b) {
		Arrays.fill(b, (byte) 0);
	}

	/**
	 * Clear char[].
	 *
	 * @param b the byte[]
	 */
	public static void clear(final char[] c) {
		Arrays.fill(c, Character.MIN_VALUE);
	}

	/**
	 * Get the absolute pathname.
	 *
	 * @param f the string f
	 * @return absolutePath
	 */
	public static String getFilePath(final String f) {
		return new File(f).getAbsolutePath();
	}

	/**
	 * Get a new secure random instance strong.
	 *
	 * @return InstanceStrong
	 * @throws NoSuchAlgorithmException
	 */
	public static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstanceStrong();
		} catch (final NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}

	/**
	 * Checks if is empty string.
	 *
	 * @param s the string s
	 * @return true, if is empty string
	 */
	public static boolean isEmpty(final String s) {
		return s == null || s.isBlank();
	}

	/**
	 * Checks if array is equal.
	 *
	 * @param a the first object a
	 * @param b the second object b
	 * @return true, if equal
	 */
	public static boolean isEqual(final Object[] a, final Object[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Checks if file is open.
	 *
	 * @param f the string f
	 * @return true, if is file open
	 */
	public static boolean isFileOpen(final String f) {
		return !isEmpty(f) && isReadable(f);
	}

	/**
	 * Checks if file is readable.
	 *
	 * @param f the string f
	 * @return true, if is readable
	 */
	public static boolean isReadable(final String f) {
		final var file = new File(f);
		return (file.exists() && file.canRead() && file.canWrite() && file.isFile());
	}

	/**
	 * Get Integer value of int.
	 *
	 * @param i the int i
	 * @return Integer
	 */
	public static Integer valueOf(final int i) {
		return Integer.valueOf(i);
	}

	private Util() {
	}
}
