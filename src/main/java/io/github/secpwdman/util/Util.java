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
package io.github.secpwdman.util;

import static io.github.secpwdman.widgets.Widgets.msg;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.Deflater;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.secpwdman.config.ConfData;

/**
 * The Class Util.
 */
public class Util {

	/**
	 * Convert string array to string.
	 *
	 * @param s the string s
	 * @return absolutePath
	 */
	public static String arrayToString(final String[] s) {
		return Arrays.toString(s);
	}

	/**
	 * Deflate input.
	 *
	 * @param input byte array
	 * @return compressedDataLength
	 */
	public static int getCompressedDataLength(final byte[] input) {
		final var output = new byte[input.length + Byte.SIZE];
		final var deflater = new Deflater();
		deflater.setInput(input);
		deflater.finish();
		final var compressedDataLength = deflater.deflate(output);
		deflater.end();

		return compressedDataLength;
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
	 * Get random UUID.
	 *
	 * @return randomUUID
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}

	/**
	 * Checks if array is equal.
	 *
	 * @param a the first string a
	 * @param b the second string b
	 * @return true, if equal
	 */
	public static boolean isArrayEqual(final String[] a, final String[] b) {
		return Arrays.equals(a, b);
	}

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
	 * @param url the string url
	 * @return true, if is url
	 */
	public static boolean isUrl(final String url) {
		return UrlValidator.getInstance().isValid(url);
	}

	/**
	 * Checks if is url.
	 *
	 * @param table the table
	 * @return true, if is url
	 */
	public static boolean isUrl(final Table table) {
		return isUrl(table.getSelection()[0].getText(3));
	}

	/**
	 * Asks to show passwords in cleartext.
	 *
	 * @param cData the cData
	 * @param shell the shell
	 * @return true, if is yes
	 */
	public static boolean msgShowPasswords(final ConfData cData, final Shell shell) {
		return msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO, cData.titleWar, cData.warnPass) == SWT.YES;
	}

	private Util() {
	}
}
