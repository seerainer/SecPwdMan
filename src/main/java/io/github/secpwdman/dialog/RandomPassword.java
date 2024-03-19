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
package io.github.secpwdman.dialog;

import static io.github.secpwdman.util.Util.getCompressedDataLength;
import static io.github.secpwdman.util.Util.isEmptyString;
import static io.github.secpwdman.widgets.Widgets.msg;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;

/**
 * The Class RandomPassword.
 */
public class RandomPassword {
	private final Action action;

	/**
	 * Instantiates a new random password.
	 *
	 * @param action the action
	 */
	public RandomPassword(final Action action) {
		this.action = action;
	}

	/**
	 * Generate a random password.
	 *
	 * @param children the children
	 * @return the string
	 */
	public String generate(final Control[] children) {
		final var cData = action.getCData();
		var pwd = cData.nullStr;
		final var randomPwd = new StringBuilder();
		final var sign = new StringBuilder();
		final var count = ((Spinner) children[11]).getSelection();
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

		if (!isEmptyString(sign.toString())) {
			try {
				final var random = SecureRandom.getInstanceStrong();
				final var strongPwd = select[0] && select[1] && select[2] && (select[3] || select[4]);

				do {
					randomPwd.setLength(0);
					for (var j = 0; j < count; j++) {
						final var next = random.nextInt(sign.length());
						final var c = sign.charAt(next % sign.length());
						randomPwd.append(c);
					}
				} while (strongPwd && isWeakPwd(cData, randomPwd.toString()));
			} catch (final NoSuchAlgorithmException e) {
				msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
			}
			pwd = randomPwd.toString();
		}

		return pwd;
	}

	/**
	 * Checks if it is a weak password.
	 *
	 * @param cData the cdata
	 * @param pwd   the pwd
	 * @return true, if it is a weak password
	 */
	boolean isWeakPwd(final ConfData cData, final String pwd) {
		final boolean[] b = { false, false, false, false, false, false };

		for (final char c : pwd.toCharArray())
			if (Character.isLowerCase(c))
				b[0] = true;
			else if (Character.isUpperCase(c))
				b[1] = true;
			else if (Character.isDigit(c))
				b[2] = true;
			else if (cData.rSpecia1.contains(Character.toString(c)))
				b[3] = true;
			else if (cData.rSpecia2.contains(Character.toString(c)))
				b[4] = true;
			else
				b[5] = true;

		if (b[0] && b[1] && b[2] && (b[3] || b[4] || b[5]))
			return false;

		return true;
	}

	/**
	 * Test the password strength.
	 *
	 * @param cData the cdata
	 * @param label the label
	 * @param text  the text
	 */
	void testPasswordStrength(final ConfData cData, final Label label, final String text) {
		final var display = label.getDisplay();

		if (text.length() < 8) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passShor);
		} else {
			final var compressedLength = getCompressedDataLength(text.getBytes());

			if (cData.isDarkTheme())
				label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
			else
				label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

			if (isWeakPwd(cData, text)) {
				if (compressedLength > 19) {
					label.setText(cData.passGood);

					if (compressedLength > 27)
						label.setText(cData.passStro);
				} else {
					label.setForeground(display.getSystemColor(SWT.COLOR_RED));
					label.setText(cData.passWeak);
				}
			} else {
				label.setText(cData.passGood);

				if (compressedLength > 19)
					label.setText(cData.passStro);
				if (compressedLength > 27)
					label.setText(cData.passSecu);
			}
		}
	}
}
