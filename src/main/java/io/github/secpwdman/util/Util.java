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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.UUID;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.secpwdman.config.ConfData;

/**
 * The Class Util.
 */
public class Util {
	public static final boolean DARK = Display.isSystemDarkTheme();
	public static final boolean WIN32 = System.getProperty("os.name").startsWith("Win"); //$NON-NLS-1$ //$NON-NLS-2$

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
	 * Clear byte[].
	 *
	 * @param b the byte[]
	 */
	public static void clear(final byte[] b) {
		Arrays.fill(b, (byte) 0);
	}

	/**
	 * Get a new array list.
	 *
	 * @param initialCapacity the initial capacity
	 * @return ArrayList
	 */
	public static ArrayList<String> getArrayList(final int initialCapacity) {
		return new ArrayList<>(initialCapacity);
	}

	/**
	 * Get a new collator instance.
	 *
	 * @return collator
	 */
	public static Collator getCollator() {
		return Collator.getInstance();
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
	 * Get a new hash set.
	 *
	 * @return HashSet
	 */
	public static HashSet<String> getHashSet() {
		return new HashSet<>();
	}

	/**
	 * Gets the image.
	 *
	 * @param display the display
	 * @param image   the image
	 * @return the image
	 */
	public static Image getImage(final Display display, final String image) {
		final var img = new Image(display, new ByteArrayInputStream(Base64.getMimeDecoder().decode(image)));
		img.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		return img;
	}

	/**
	 * Get a new secure random instance strong.
	 *
	 * @return InstanceStrong
	 * @throws NoSuchAlgorithmException
	 */
	public static SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
		return SecureRandom.getInstanceStrong();
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
	 * @param a the first boolean[] a
	 * @param b the second boolean[] b
	 * @return true, if equal
	 */
	public static boolean isEqual(final boolean[] a, final boolean[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Checks if array is equal.
	 *
	 * @param a the first byte[] a
	 * @param b the second byte[] b
	 * @return true, if equal
	 */
	public static boolean isEqual(final byte[] a, final byte[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Checks if array is equal.
	 *
	 * @param a the first char[] a
	 * @param b the second char[] b
	 * @return true, if equal
	 */
	public static boolean isEqual(final char[] a, final char[] b) {
		return Arrays.equals(a, b);
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
	 * Checks if is file open.
	 *
	 * @param f the string f
	 * @return true, if is file open
	 */
	public static boolean isFileOpen(final String f) {
		return !isEmpty(f) && isReadable(f);
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

	/**
	 * Center the shell.
	 *
	 * @param shell the shell
	 */
	public static void setCenter(final Shell shell) {
		final var r = shell.getDisplay().getBounds();
		final var s = shell.getBounds();
		shell.setLocation(new Point((r.width - s.width) / 2, ((r.height - s.height) * 2) / 5));
	}

	/**
	 * Character array to byte array.
	 *
	 * @param c the char[]
	 * @return the byte[]
	 */
	public static byte[] toBytes(final char[] c) {
		final var charBuffer = CharBuffer.wrap(c);
		final var byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		final var bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000');
		clear(byteBuffer.array());
		return bytes;
	}

	/**
	 * Character array to CharSequence.
	 *
	 * @param c the char[]
	 * @return the CharSequence
	 */
	public static CharSequence toCharSequence(final char[] c) {
		return CharBuffer.wrap(c);
	}

	private Util() {
	}
}
