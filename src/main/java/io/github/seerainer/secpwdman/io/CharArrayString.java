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
package io.github.seerainer.secpwdman.io;

import java.io.CharArrayWriter;

import io.github.seerainer.secpwdman.util.Util;

/**
 * The class CharArrayString.
 */
public class CharArrayString {

	private final CharArrayWriter caw;

	/**
	 * Instantiates a new CharArrayString.
	 *
	 * @param str the str
	 */
	public CharArrayString(final String str) {
		this.caw = new CharArrayWriter();
		this.caw.write(str, 0, str.length());
	}

	/**
	 * Clear.
	 */
	public void clear() {
		Util.clear(toCharArray());
		caw.reset();
	}

	/**
	 * @return the length
	 */
	public int length() {
		return caw.size();
	}

	/**
	 * @return the char array
	 */
	public char[] toCharArray() {
		return caw.toCharArray();
	}
}
