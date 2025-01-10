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
package io.github.seerainer.secpwdman.dialog;

import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static io.github.seerainer.secpwdman.util.Util.toBytes;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
import static io.github.seerainer.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.label;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static java.lang.Integer.valueOf;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.io.IO;

/**
 * The class PasswordDialog.
 */
final class PasswordDialog implements PrimitiveConstants, StringConstants {

	private static void testPassword(final ModifyEvent e, final ConfigData cData, final Text text2) {
		final var text1 = (Text) e.widget;
		final var label = (Label) text1.getParent().getChildren()[5];
		final var pwd1 = text1.getTextChars();
		final var pwd2 = text2.getTextChars();
		if (isEqual(pwd1, pwd2)) {
			if (pwd1.length >= PASSWORD_ABSOLUTE_MIN_LENGTH) {
				evalPasswordStrength(cData, label, pwd1);
			} else {
				label.setText(errorLen.formatted(valueOf(cData.getPasswordMinLength())));
			}
		} else {
			label.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
			label.setText(passNoMa);
			label.setToolTipText(empty);
		}
		clear(pwd1);
		clear(pwd2);
	}

	private final FileAction action;

	/**
	 * Instantiates a new password dialog.
	 *
	 * @param action the action
	 */
	PasswordDialog(final FileAction action) {
		this.action = action;
	}

	private void confirmPassword(final Shell dialog) {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var file = cData.getFile();
		final var io = new IO(action);
		final var pwd = ((Text) dialog.getChildren()[1]);
		final var pwdCharsA = pwd.getTextChars();
		final var length = pwdCharsA.length;
		pwd.selectAll();
		if (dialog.getBounds().height == PASSWORD_CONFIRM_HEIGHT) {
			final var pwdMinLength = cData.getPasswordMinLength();
			final var pwdConfirm = ((Text) dialog.getChildren()[3]);
			final var pwdCharsB = pwdConfirm.getTextChars();
			pwdConfirm.selectAll();
			if ((length > 0 && isEqual(pwdCharsA, pwdCharsB))) {
				if (length < pwdMinLength) {
					msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorLen.formatted(valueOf(pwdMinLength)));
				} else if (io.saveFile(toBytes(pwdCharsB), file)) {
					cData.setModified(false);
					cData.setReadOnly(true);
					dialog.close();
					action.postSave();
				}
			}
			clear(pwdCharsA);
		} else if (length > 0 && io.openFile(toBytes(pwdCharsA), file)) {
			cData.setLocked(false);
			cData.setModified(false);
			cData.setReadOnly(true);
			dialog.close();
		}
		if (!dialog.isDisposed()) {
			pwd.setFocus();
		}
		if (shell == null || shell.isDisposed()) {
			return;
		}
		action.fillGroupList();
		action.updateUI();
	}

	/**
	 * Opens the dialog.
	 *
	 * @param confirm if true open confirm dialog
	 */
	void open(final boolean confirm) {
		SearchDialog.close();

		final var cData = action.getCData();
		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.SYSTEM_MODAL,
				getLayout(3, 5, 10, 10, 8, 8, 10), passTitl);

		label(dialog, SWT.HORIZONTAL, passWord);
		final var pwd = text(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
		pwd.setFocus();
		pwd.setTextLimit(64);

		if (confirm) {
			label(dialog, SWT.HORIZONTAL, passConf);
			final var pwdConfirm = text(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
			pwd.addModifyListener(e -> testPassword(e, cData, pwdConfirm));
			pwdConfirm.addModifyListener(e -> testPassword(e, cData, pwd));
			pwdConfirm.setTextLimit(64);

			emptyLabel(dialog, 1);

			final var label = label(dialog, SWT.HORIZONTAL, errorLen.formatted(valueOf(cData.getPasswordMinLength())));
			label.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			dialog.setSize(480, PASSWORD_CONFIRM_HEIGHT);
		} else {
			dialog.setSize(480, 150);
		}

		emptyLabel(dialog, 3);

		final var okBtn = button(dialog, SWT.PUSH, entrOkay, widgetSelectedAdapter(e -> confirmPassword(dialog)));
		var gridData = new GridData(SWT.END, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = 80;
		okBtn.setLayoutData(gridData);
		dialog.setDefaultButton(okBtn);

		final var clBtn = button(dialog, SWT.PUSH, entrCanc, widgetSelectedAdapter(e -> dialog.close()));
		gridData = new GridData(SWT.LEAD, SWT.TOP, true, false);
		gridData.widthHint = 80;
		clBtn.setLayoutData(gridData);

		setCenter(dialog);
		dialog.open();
	}
}
