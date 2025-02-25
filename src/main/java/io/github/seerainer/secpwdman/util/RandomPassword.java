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

import java.util.Arrays;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.Crypto;

/**
 * The class RandomPassword.
 */
public class RandomPassword implements PrimitiveConstants, StringConstants {

	/**
	 * Generate a random password.
	 *
	 * @param action   the action
	 * @param children the children
	 * @param pwdField the password text field
	 * @return the string
	 */
	public static char[] generate(final Action action, final Control[] children) {
		final var sb = new StringBuilder();
		final var select = new boolean[6];
		for (var i = 0; i < select.length; i++) {
			select[i] = ((Button) children[i]).getSelection();
			final var text = ((Button) children[i]).getText();
			if (select[i]) {
				sb.append(i == 5 ? space : (i == 3 ? text.substring(0, text.length() - 1) : text));
			}
		}
		if (Util.isBlank(sb.toString())) {
			return new char[0];
		}
		final var spinner = ((Spinner) children[8]).getSelection();
		var randomPwd = new char[spinner];
		do {
			randomPwd = generate(spinner, sb);
		} while (isWeakPassword(select, randomPwd));

		return randomPwd;
	}

	private static char[] generate(final int length, final StringBuilder sb) {
		final var random = Crypto.getSecureRandom();
		final var chars = new char[length];
		for (var i = 0; i < length; i++) {
			chars[i] = sb.charAt(random.nextInt(sb.length()));
		}
		return chars;
	}

	/**
	 * Generate a random password for the key store.
	 *
	 * @return the char array
	 */
	public static char[] generateKeyStorePassword() {
		final var sb = new StringBuilder();
		sb.append(rTextLoC + rTextUpC + rNumbers + rSpecia1 + rSpecia2 + space);
		return generate(DEFAULT_PWD_LENGTH, sb);
	}

	private static boolean isWeakPassword(final boolean[] selection, final char[] pwd) {
		final var b = new boolean[6];
		for (final char c : pwd) {
			if (Character.isLowerCase(c)) {
				b[0] = true;
			} else if (Character.isUpperCase(c)) {
				b[1] = true;
			} else if (Character.isDigit(c)) {
				b[2] = true;
			} else if (rSpecia1.indexOf(c) >= 0) {
				b[3] = true;
			} else if (rSpecia2.indexOf(c) >= 0) {
				b[4] = true;
			} else if (Character.isSpaceChar(c)) {
				b[5] = true;
			}
		}

		return !Arrays.equals(selection, b);
	}

	private RandomPassword() {
	}
}
