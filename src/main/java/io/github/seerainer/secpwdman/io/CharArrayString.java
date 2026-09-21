/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
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
package io.github.seerainer.secpwdman.io;

import java.util.Arrays;

import io.github.seerainer.secpwdman.util.Util;

/**
 * The class CharArrayString. Wipeable char sequence that never relies on
 * reflection: the backing buffer is owned by this class and zeroed on clear.
 */
public class CharArrayString {

    private final char[] buf;

    private int count;

    /**
     * Instantiates a new CharArrayString.
     *
     * @param str the String
     */
    public CharArrayString(final String str) {
	this.buf = new char[Math.max(16, str.length())];
	str.getChars(0, str.length(), buf, 0);
	this.count = str.length();
    }

    /**
     * Instantiates a new CharArrayString from a char array without pinning an
     * intermediate String.
     *
     * @param chars the char array (copied)
     */
    public CharArrayString(final char[] chars) {
	this.buf = new char[Math.max(16, chars.length)];
	System.arraycopy(chars, 0, buf, 0, chars.length);
	this.count = chars.length;
    }

    /**
     * Zero the entire backing buffer and reset the length.
     */
    public void clear() {
	Util.clear(buf);
	count = 0;
    }

    /**
     * @return a copy of the live characters
     */
    public char[] toCharArray() {
	return Arrays.copyOf(buf, count);
    }
}
