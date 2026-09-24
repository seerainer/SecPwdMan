/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
 * philipp@seerainer.com
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

import static io.github.seerainer.secpwdman.config.StringConstants.empty;
import static io.github.seerainer.secpwdman.config.StringConstants.newLine;
import static io.github.seerainer.secpwdman.config.StringConstants.passFair;
import static io.github.seerainer.secpwdman.config.StringConstants.passSecu;
import static io.github.seerainer.secpwdman.config.StringConstants.passShor;
import static io.github.seerainer.secpwdman.config.StringConstants.passStro;
import static io.github.seerainer.secpwdman.config.StringConstants.passWeak;

import java.nio.CharBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.nulabinc.zxcvbn.Zxcvbn;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The class PasswordStrength.
 */
public class PasswordStrength {

    private static final Zxcvbn zxcvbn = new Zxcvbn();

    private PasswordStrength() {
    }

    /**
     * Maps a zxcvbn score (0-4) to its display text. Extracted so the mapping is
     * unit-testable without an SWT display.
     *
     * @param score the zxcvbn score
     * @return the display text
     */
    static String strengthText(final int score) {
	return switch (score) {
	case 2 -> passFair;
	case 3 -> passStro;
	case 4 -> passSecu;
	default -> passWeak;
	};
    }

    /**
     * Evaluate the password strength.
     *
     * @param cData the cdata
     * @param label the label
     * @param pwd   the text
     */
    public static void evalPasswordStrength(final ConfigData cData, final Label label, final char[] pwd) {
	final var display = label.getDisplay();
	if (pwd.length < cData.getPasswordMinLength()) {
	    label.setForeground(display.getSystemColor(SWT.COLOR_RED));
	    label.setText(passShor);
	    label.setToolTipText(empty);
	    return;
	}
	final var charBuffer = CharBuffer.wrap(pwd);
	try {
	    final var strength = zxcvbn.measure(charBuffer);
	    final var text = strengthText(strength.getScore());
	    if (text.equals(passWeak) || text.equals(passFair)) {
		label.setForeground(display.getSystemColor(SWT.COLOR_RED));
	    } else if (SWTUtil.DARK) {
		label.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
	    } else {
		label.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
	    }
	    label.setText(text);
	    final var feedback = strength.getFeedback();
	    final var sb = new StringBuilder();
	    feedback.getSuggestions().forEach((final var s) -> sb.append(s).append(newLine));
	    label.setToolTipText(sb.toString() + feedback.getWarning());
	} finally {
	    Util.clear(pwd);
	    CharsetUtil.clearCharBuffer(charBuffer);
	}
    }
}
