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
import static io.github.seerainer.secpwdman.ui.Widgets.label;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.ui.Widgets.text;
import static io.github.seerainer.secpwdman.util.CharsetUtil.toBytes;
import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.io.IO;
import io.github.seerainer.secpwdman.util.AutoLockManager;
import io.github.seerainer.secpwdman.util.Win32Affinity;

/**
 * The record PasswordDialog.
 */
record PasswordDialog(FileAction action) implements PrimitiveConstants, StringConstants {

    private static void closeDialog(final ConfigData cData, final Shell dialog) {
	cData.setLocked(false);
	cData.setModified(false);
	dialog.close();
	System.gc();
    }

    private static void testPassword(final ModifyEvent e, final ConfigData cData, final Text text2) {
	final var text1 = (Text) e.widget;
	final var label = (Label) text1.getParent().getChildren()[5];
	final var pwd1 = text1.getTextChars();
	final var pwd2 = text2.getTextChars();
	if (isEqual(pwd1, pwd2)) {
	    if (pwd1.length >= PWD_MIN_LENGTH) {
		evalPasswordStrength(cData, label, pwd1);
	    } else {
		label.setText(errorLen.formatted(Integer.valueOf(cData.getPasswordMinLength())));
	    }
	} else {
	    label.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
	    label.setText(passNoMa);
	    label.setToolTipText(empty);
	}
	clear(pwd1);
	clear(pwd2);
    }

    private static void startAutoLock(final Display display, final FileAction action) {
	AutoLockManager.getInstance(display, action);
    }

    private void confirmPassword(final Shell dialog) {
	final var cData = action.getCData();
	final var shell = action.getShell();
	final var display = shell.getDisplay();
	final var file = cData.getFile();
	final var io = new IO(action);
	final var pwd = ((Text) dialog.getChildren()[1]);
	final var pwdCharsA = pwd.getTextChars();
	final var length = pwdCharsA.length;
	pwd.selectAll();
	if (dialog.getBounds().height == PWD_CONFIRM_HEIGHT) {
	    final var pwdMinLength = cData.getPasswordMinLength();
	    final var pwdConfirm = ((Text) dialog.getChildren()[3]);
	    final var pwdCharsB = pwdConfirm.getTextChars();
	    pwdConfirm.selectAll();
	    if ((length > 0 && isEqual(pwdCharsA, pwdCharsB))) {
		if (length < pwdMinLength) {
		    msg(dialog, SWT.ICON_ERROR | SWT.OK, titleErr, errorLen.formatted(Integer.valueOf(pwdMinLength)));
		} else {
		    action.resetGroupList();
		    action.fillTable(true, action.extractData(true));
		    cData.setImport(true);
		    if (io.saveFile(toBytes(pwdCharsB), file)) {
			closeDialog(cData, dialog);
			action.postSave();
			startAutoLock(display, action);
		    }
		}
	    }
	    clear(pwdCharsA);
	} else if (length > 0) {
	    dialog.setVisible(false);
	    if (io.openFile(toBytes(pwdCharsA), file)) {
		cData.setReadOnly(action.getTable().getItemCount() > 0);
		closeDialog(cData, dialog);
		startAutoLock(display, action);
	    } else {
		dialog.setVisible(true);
	    }
	}
	if (!dialog.isDisposed()) {
	    pwd.setFocus();
	}
	if (Objects.isNull(shell) || shell.isDisposed()) {
	    return;
	}
	action.fillGroupList();
	action.updateUI();
    }

    Shell open(final boolean confirm) {
	DialogFactory.closeSearchDialog();

	final var cData = action.getCData();
	final var shell = action.getShell();
	final var display = shell.getDisplay();
	final var layout = getLayout(3, 5, 10, 10, 8, 8, 10);
	final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.SYSTEM_MODAL, layout, passTitl);

	label(dialog, SWT.HORIZONTAL, passWord);
	final var pwd = text(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
	pwd.setFocus();
	pwd.setTextLimit(PWD_MAX_LENGTH);

	if (confirm) {
	    label(dialog, SWT.HORIZONTAL, passConf);
	    final var pwdConfirm = text(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
	    pwd.addModifyListener(e -> testPassword(e, cData, pwdConfirm));
	    pwdConfirm.addModifyListener(e -> testPassword(e, cData, pwd));
	    pwdConfirm.setTextLimit(PWD_MAX_LENGTH);

	    emptyLabel(dialog, 1);

	    final var label = label(dialog, SWT.HORIZONTAL,
		    errorLen.formatted(Integer.valueOf(cData.getPasswordMinLength())));
	    label.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
	    label.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0, 2, 1));
	    dialog.setSize(480, PWD_CONFIRM_HEIGHT);
	} else {
	    dialog.setSize(480, 150);
	}

	emptyLabel(dialog, 3);

	final var okBtn = button(dialog, SWT.PUSH, dialOkay, widgetSelectedAdapter(_ -> confirmPassword(dialog)));
	var gridData = getGridData(SWT.END, SWT.TOP, 1, 0, 2, 1);
	gridData.widthHint = BUTTON_WIDTH;
	okBtn.setLayoutData(gridData);
	dialog.setDefaultButton(okBtn);

	final var clBtn = button(dialog, SWT.PUSH, diaCancl, widgetSelectedAdapter(_ -> dialog.close()));
	gridData = getGridData(SWT.LEAD, SWT.TOP, 1, 0);
	gridData.widthHint = BUTTON_WIDTH;
	clBtn.setLayoutData(gridData);

	setCenter(dialog);
	dialog.open();
	display.asyncExec(() -> Win32Affinity.setWindowDisplayAffinity(dialog));
	return dialog;
    }
}
