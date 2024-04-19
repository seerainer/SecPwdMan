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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;

/**
 * The Class RandomPassword.
 */
public class RandomPassword {

	/**
	 * Generate a random password.
	 *
	 * @param children the children
	 * @return the string
	 */
	public static String generate(final Action action, final Control[] children) {
		final var cData = action.getCData();
		final var sign = new StringBuilder();
		final boolean[] select = { false, false, false, false, false, false };

		for (var i = 0; i < select.length; i++) {
			select[i] = ((Button) children[i]).getSelection();
			final var text = ((Button) children[i]).getText();

			if (select[i])
				if (i == 5)
					sign.append(cData.space);
				else if (i == 3)
					sign.append(text.substring(0, text.length() - 1));
				else
					sign.append(text);
		}

		if (Util.isEmpty(sign.toString()))
			return cData.nullStr;

		final var randomPwd = new StringBuilder();
		final var random = Util.getSecureRandom();
		final var spinner = ((Spinner) children[11]).getSelection();

		do {
			randomPwd.setLength(0);

			for (var count = spinner; count > 0; count--) {
				final var next = random.nextInt(sign.length());
				final var c = sign.charAt(next % sign.length());
				randomPwd.append(c);
			}
		} while (isWeakPassword(cData, select, randomPwd.toString().toCharArray()));

		return randomPwd.toString();
	}

	/**
	 * Checks if it is a weak password.
	 *
	 * @param cData the cdata
	 * @param pwd   the pwd
	 * @return true, if it is a weak password
	 */
	private static boolean isWeakPassword(final ConfData cData, final boolean[] selection, final char[] pwd) {
		final boolean[] b = { false, false, false, false, false, false };

		for (final char c : pwd)
			if (Character.isLowerCase(c))
				b[0] = true;
			else if (Character.isUpperCase(c))
				b[1] = true;
			else if (Character.isDigit(c))
				b[2] = true;
			else if ((cData.rSpecia1).contains(Character.toString(c)))
				b[3] = true;
			else if ((cData.rSpecia2).contains(Character.toString(c)))
				b[4] = true;
			else if (Character.isSpaceChar(c))
				b[5] = true;

		if (Util.isEqual(selection, b))
			return false;

		return true;
	}
}
