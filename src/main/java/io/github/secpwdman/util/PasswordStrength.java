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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.nulabinc.zxcvbn.Zxcvbn;

import io.github.secpwdman.config.ConfData;

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
		final var minLength = cData.getPasswordMinLength();

		if (pwd.length < minLength) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passShor + minLength);
			label.setToolTipText(null);
			return;
		}

		var pwdChar = Util.toCharSequence(pwd);
		final var strength = new Zxcvbn().measure(pwdChar);
		pwdChar = null;

		final var crackTimes = strength.getCrackTimesDisplay();
		final var offlineSlow = crackTimes.getOfflineSlowHashing1e4perSecond();
		final var onlineFast = crackTimes.getOnlineNoThrottling10perSecond();
		final var onlineSlow = crackTimes.getOnlineThrottling100perHour();
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
		default:
			break;
		}

		if (text == cData.passWeak || text == cData.passFair)
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		else if (text == cData.passGood)
			label.setForeground(label.getParent().getForeground());
		else if (Util.DARK)
			label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		else
			label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));

		label.setText(text);
		label.setToolTipText(cData.passOfsl + offlineSlow + cData.passOnfa + onlineFast + cData.passOnsl + onlineSlow);
	}
}
