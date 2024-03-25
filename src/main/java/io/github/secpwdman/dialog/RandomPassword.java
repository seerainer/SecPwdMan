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
	 * Evaluate the password strength.
	 *
	 * @param cData the cdata
	 * @param label the label
	 * @param pwd   the text
	 */
	void evalPasswordStrength(final ConfData cData, final Label label, final CharSequence pwd) {
		final var display = label.getDisplay();
		final var minLength = cData.getPasswordMinLength();

		if (pwd.length() < minLength) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passShor + minLength);
			label.setToolTipText(null);
			return;
		}

		final var strength = new Zxcvbn().measure(pwd);
		final var crackTimes = strength.getCrackTimesDisplay();
		final var off = crackTimes.getOfflineFastHashing1e10PerSecond();
		final var ofs = crackTimes.getOfflineSlowHashing1e4perSecond();
		final var onf = crackTimes.getOnlineNoThrottling10perSecond();
		final var ons = crackTimes.getOnlineThrottling100perHour();
		var text = cData.passWeak;

		switch (strength.getScore()) {
		case 0:
			break;
		case 1:
			text = cData.passFair;
			break;
		case 2:
			text = cData.passGood;
			break;
		case 3:
			text = cData.passStro;
			break;
		case 4:
			text = cData.passSecu;
			break;
		}

		if (text == cData.passWeak || text == cData.passFair)
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		else if (text == cData.passGood)
			label.setForeground(label.getParent().getForeground());
		else if (cData.isDarkTheme())
			label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		else
			label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

		label.setText(text);
		label.setToolTipText(cData.passOffa + off + cData.passOfsl + ofs + cData.passOnfa + onf + cData.passOnsl + ons);
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

		if (isEmptyString(sign.toString()))
			return cData.nullStr;

		final var randomPwd = new StringBuilder();

		try {
			final var random = SecureRandom.getInstanceStrong();
			final var spinner = ((Spinner) children[11]).getSelection();

			do {
				randomPwd.setLength(0);
				for (var count = spinner; 0 < count; count--) {
					final var next = random.nextInt(sign.length());
					final var c = sign.charAt(next % sign.length());
					randomPwd.append(c);
				}
			} while (isWeakPassword(cData, select, randomPwd.toString().toCharArray()));
		} catch (final NoSuchAlgorithmException e) {
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		}

		return randomPwd.toString();
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
