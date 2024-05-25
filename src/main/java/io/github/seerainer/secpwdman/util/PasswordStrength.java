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
package io.github.seerainer.secpwdman.util;

import java.nio.CharBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.nulabinc.zxcvbn.Zxcvbn;

import io.github.seerainer.secpwdman.config.ConfData;

/**
 * The Class PasswordStrength.
 */
public class PasswordStrength {

	/**
	 * Evaluate the password strength.
	 *
	 * @param cData the cdata
	 * @param label the label
	 * @param pwd   the text
	 */
	public static void evalPasswordStrength(final ConfData cData, final Label label, final char[] pwd) {
		final var display = label.getDisplay();

		if (pwd.length < ConfData.PASSWORD_MIN_LENGTH) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passShor);
			label.setToolTipText(cData.empty);
			return;
		}

		final var charBuffer = CharBuffer.wrap(pwd);
		final var strength = new Zxcvbn().measure(charBuffer);
		Util.clear(pwd);
		Util.clear(charBuffer.array());

		var text = cData.passWeak;

		switch (strength.getScore()) {
		case 2:
			text = cData.passFair;
			break;
		case 3:
			text = cData.passStro;
			break;
		case 4:
			text = cData.passSecu;
			break;
		default:
			break;
		}

		if (text == cData.passWeak || text == cData.passFair)
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		else if (SWTUtil.DARK)
			label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		else
			label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

		label.setText(text);

		final var feedback = strength.getFeedback();
		final var str = new StringBuilder();

		for (final var s : feedback.getSuggestions())
			str.append(s).append(cData.newLine);

		label.setToolTipText(str.toString() + feedback.getWarning());
	}
}
