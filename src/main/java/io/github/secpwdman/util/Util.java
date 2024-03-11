/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
 * philipp@seerainer.com
 * http://www.seerainer.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
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
package io.github.secpwdman.util;

import java.io.File;
import java.net.URI;

import org.eclipse.swt.widgets.Table;

/**
 * The Class Util.
 */
public class Util {

	/**
	 * Checks if is empty string.
	 *
	 * @param s the string s
	 * @return true, if is empty string
	 */
	public static boolean isEmptyString(final String s) {
		return s == null || s.isBlank();
	}

	/**
	 * Checks if is file open.
	 *
	 * @param f the string f
	 * @return true, if is file open
	 */
	public static boolean isFileOpen(final String f) {
		return !isEmptyString(f) && isReadable(f);
	}

	/**
	 * Checks if is readable.
	 *
	 * @param f the string f
	 * @return true, if is readable
	 */
	public static boolean isReadable(final String f) {
		final var file = new File(f);
		return (file.exists() && file.canRead() && file.canWrite() && file.isFile());
	}

	/**
	 * Checks if is url.
	 *
	 * @param count the count
	 * @param table the table
	 * @return true, if is url
	 */
	public static boolean isUrl(final boolean count, final Table table) {
		if (count)
			return isUrl(table.getSelection()[0].getText(3));
		return false;
	}

	/**
	 * Checks if is url.
	 *
	 * @param url the url
	 * @return true, if is url
	 */
	public static boolean isUrl(final String url) {
		try {
			URI.create(url).toURL();
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	private Util() {
	}
}
