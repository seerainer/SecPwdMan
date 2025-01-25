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

import java.nio.CharBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.nulabinc.zxcvbn.Zxcvbn;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class PasswordStrength.
 */
public final class PasswordStrength implements PrimitiveConstants, StringConstants {

	/**
	 * Evaluate the password strength.
	 *
	 * @param cData the cdata
	 * @param label the label
	 * @param pwd   the text
	 */
	public static void evalPasswordStrength(final ConfigData cData, final Label label, final char[] pwd) {
		final var display = label.getDisplay();
		if (pwd.length < PWD_MIN_LENGTH) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
			label.setText(passShor);
			label.setToolTipText(empty);
			return;
		}
		final var charBuffer = CharBuffer.wrap(pwd);
		final var strength = new Zxcvbn().measure(charBuffer);
		Util.clear(pwd);
		Util.clear(charBuffer.array());
		var text = passWeak;
		switch (strength.getScore()) {
		case 2 -> text = passFair;
		case 3 -> text = passStro;
		case 4 -> text = passSecu;
		default -> {
			// break;
		}
		}
		if (text.equals(passWeak) || text.equals(passFair)) {
			label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		} else if (SWTUtil.DARK) {
			label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		} else {
			label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
		}
		label.setText(text);
		final var feedback = strength.getFeedback();
		final var str = new StringBuilder();
		feedback.getSuggestions().forEach((final var s) -> str.append(s).append(newLine));
		label.setToolTipText(str.toString() + feedback.getWarning());
	}

	private PasswordStrength() {
	}
}
