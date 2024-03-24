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

import static io.github.secpwdman.util.Util.isEmptyString;
import static io.github.secpwdman.widgets.Widgets.msg;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.nulabinc.zxcvbn.Zxcvbn;

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

		var pwd = cData.nullStr;

		if (isEmptyString(sign.toString()))
			return pwd;

		try {
			final var random = SecureRandom.getInstanceStrong();
			final var spinner = ((Spinner) children[11]).getSelection();

			do {
				final var randomPwd = new StringBuilder();
				for (var count = spinner; 0 < count; count--) {
					final var next = random.nextInt(sign.length());
					final var c = sign.charAt(next % sign.length());
					randomPwd.append(c);
				}
				pwd = randomPwd.toString();
			} while (isWeakPassword(cData, select, pwd.toCharArray()));
		} catch (final NoSuchAlgorithmException e) {
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		}

		return pwd;
	}

	/**
	 * Test the password strength.
	 *
	 * @param cData the cdata
	 * @param label the label
	 * @param pwd   the text
	 */
	void getPasswordStrength(final ConfData cData, final Label label, final String pwd) {
		final var display = label.getDisplay();

		if (pwd.length() < cData.getPwdMinLength()) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passShor);
			return;
		}

		final var strength = new Zxcvbn().measure(pwd).getScore();
		var text = cData.passWeak;

		if (strength == 1)
			text = cData.passFair;
		if (strength == 2)
			text = cData.passGood;
		if (strength == 3)
			text = cData.passStro;
		if (strength == 4)
			text = cData.passSecu;

		if (text == cData.passWeak || text == cData.passFair)
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		if (text == cData.passGood)
			label.setForeground(label.getParent().getForeground());
		else if (cData.isDarkTheme())
			label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		else
			label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

		label.setText(text);
	}

	/**
	 * Checks if it is a weak password.
	 *
	 * @param cData the cdata
	 * @param pwd   the pwd
	 * @return true, if it is a weak password
	 */
	private boolean isWeakPassword(final ConfData cData, final boolean[] selection, final char[] pwd) {
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

		if (Arrays.equals(selection, b))
			return false;

		return true;
	}
}
