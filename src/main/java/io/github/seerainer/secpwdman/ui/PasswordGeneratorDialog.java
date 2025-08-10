/*
 * SecPwdMan
 * Copyright (C) 2025  Philipp Seerainer
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
package io.github.seerainer.secpwdman.ui;

import static io.github.seerainer.secpwdman.ui.Widgets.button;
import static io.github.seerainer.secpwdman.ui.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.ui.Widgets.group;
import static io.github.seerainer.secpwdman.ui.Widgets.label;
import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.ui.Widgets.spinner;
import static io.github.seerainer.secpwdman.ui.Widgets.text;
import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static org.eclipse.swt.events.MouseListener.mouseDownAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.RandomPassword;

/**
 * The record PasswordGeneratorDialog.
 */
record PasswordGeneratorDialog(Action action) implements Icons, PrimitiveConstants, StringConstants {

    private void generate(final int count, final int pwdLength, final Group random, final Text text) {
	final var buffer = new char[pwdLength * count + count - 1];
	var bufferIndex = 0;

	for (var i = 0; i < count; i++) {
	    final var randPwd = RandomPassword.generate(action, random.getChildren());
	    if (randPwd.length == 0) {
		continue;
	    }
	    System.arraycopy(randPwd, 0, buffer, bufferIndex, randPwd.length);
	    bufferIndex += randPwd.length;
	    if (i < count - 1) {
		buffer[bufferIndex] = LF;
		bufferIndex++;
	    }
	}
	text.setTextChars(buffer);
	clear(buffer);
    }

    Shell open() {
	final var shell = action.getShell();
	final var display = shell.getDisplay();
	final var image = getImage(display, APP_ICON);
	final var layout = getLayout(2, 6, 6, 20, 5, 5, 8);
	final var dialog = shell(shell, SWT.SHELL_TRIM & ~SWT.MIN | SWT.APPLICATION_MODAL, image, layout, toolPGen);

	final var text = text(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	text.setEditable(false);

	final var pwdStrength = group(dialog, new GridLayout(), entrPInd);
	final var pwdStrengthLabel = label(pwdStrength, SWT.HORIZONTAL, passEmpt);
	pwdStrengthLabel.setForeground(display.getSystemColor(SWT.COLOR_RED));
	pwdStrengthLabel.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0));

	final var random = group(dialog, new GridLayout(4, false), entrRand);
	button(random, true, rTextLoC);
	button(random, true, rTextUpC);
	button(random, true, rNumbers);
	button(random, true, rSpecia1);
	button(random, false, rSpecia2);
	button(random, false, entrSpac);
	label(random, SWT.HORIZONTAL, entrCust);
	final var customValueTxt = text(random, SWT.BORDER | SWT.SINGLE);
	customValueTxt.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0, 3, 1));

	label(random, SWT.HORIZONTAL, entrLgth);
	final var length = spinner(random, PWD_DEFAULT_LENGTH, PWD_MIN_LENGTH, PWD_MAX_LENGTH, 0, 1, 4);
	final var genBtn = button(random, SWT.PUSH, entrGene, null);
	var gridData = getGridData(SWT.CENTER, SWT.CENTER, 1, 1, 2, 2);
	gridData.widthHint = BUTTON_WIDTH;
	genBtn.setLayoutData(gridData);

	label(random, SWT.HORIZONTAL, passCoun);
	final var count = spinner(random, RANDOM_PASSWORD_COUNT, 1, 35, 0, 1, 4);
	genBtn.addSelectionListener(widgetSelectedAdapter(_ -> {
	    resetLabel(display, pwdStrengthLabel);
	    generate(count.getSelection(), length.getSelection(), random, text);
	}));

	final var copyOnClick = button(dialog, true, passCopy);

	text.addMouseListener(mouseDownAdapter(_ -> {
	    selectCurrentLine(display, pwdStrengthLabel, text);
	    if (copyOnClick.getSelection()) {
		text.copy();
	    }
	}));

	emptyLabel(dialog, 2);

	final var closeBtn = button(dialog, SWT.PUSH, diaClose, widgetSelectedAdapter(_ -> dialog.close()));
	gridData = getGridData(SWT.CENTER, SWT.CENTER, 1, 0, 2, 1);
	gridData.widthHint = BUTTON_WIDTH;
	closeBtn.setLayoutData(gridData);

	final var point = getPrefSize(dialog);
	final var size = 50;
	dialog.setSize(point.x + size, point.y + size * 4);
	dialog.setDefaultButton(genBtn);
	genBtn.setFocus();
	image.dispose();
	dialog.open();
	action.setAffinity(dialog);
	return dialog;
    }

    private static void resetLabel(final Display display, final Label label) {
	label.setForeground(display.getSystemColor(SWT.COLOR_RED));
	label.setText(passEmpt);
    }

    private void selectCurrentLine(final Display display, final Label label, final Text text) {
	if (text.getCharCount() == 0) {
	    resetLabel(display, label);
	    return;
	}
	final var caretPosition = text.getCaretPosition();
	final var content = text.getTextChars();
	var lineStart = caretPosition;
	while (lineStart > 0 && content[lineStart - 1] != LF && content[lineStart - 1] != CR) {
	    lineStart--;
	}
	if (lineStart > 0 && content[lineStart - 1] == CR && lineStart < content.length && content[lineStart] == LF) {
	    lineStart++;
	}
	var lineEnd = caretPosition;
	while (lineEnd < content.length) {
	    final var c = content[lineEnd];
	    if (c == LF || c == CR) {
		break;
	    }
	    lineEnd++;
	}
	text.setSelection(lineStart, lineEnd);
	final var password = Arrays.copyOfRange(content, lineStart, lineEnd);
	evalPasswordStrength(action.getCData(), label, password);
	clear(content);
    }
}
