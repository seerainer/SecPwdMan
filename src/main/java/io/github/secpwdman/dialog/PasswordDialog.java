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

import static io.github.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.secpwdman.util.Util.isEmptyString;
import static io.github.secpwdman.util.Util.setCenter;
import static io.github.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.secpwdman.action.FileAction;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.io.IO;

/**
 * The Class PasswordDialog.
 */
public class PasswordDialog {

	/**
	 * Tests the password strength.
	 *
	 * @param e     the ModifyEvent e
	 * @param cData the cData
	 */
	private static void testPassword(final ModifyEvent e, final ConfData cData, final Text text2) {
		final var text1 = (Text) e.widget;
		final var label = (Label) text1.getParent().getChildren()[7];
		final var text = text1.getText();

		if (text.equals(text2.getText()))
			evalPasswordStrength(cData, label, text);
		else {
			label.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passNoMa);
			label.setToolTipText(null);
		}
	}

	private final FileAction action;

	/**
	 * Instantiates a new password dialog.
	 *
	 * @param action the action
	 */
	public PasswordDialog(final FileAction action) {
		this.action = action;
	}

	/**
	 * Confirm password.
	 *
	 * @param dialog the dialog
	 */
	private void confirmPassword(final Shell dialog) {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var io = new IO(action);
		final var pwd = ((Text) dialog.getChildren()[1]);
		final var pwdStr = pwd.getText();
		pwd.selectAll();

		if (dialog.getBounds().height == 150) {
			final var minPwdLength = cData.getPasswordMinLength();
			final var pwdConfirm = ((Text) dialog.getChildren()[4]);
			final var shortMsg = String.format(cData.errorLen, Integer.valueOf(minPwdLength));
			pwdConfirm.selectAll();

			if ((!isEmptyString(pwdStr) && pwdStr.equals(pwdConfirm.getText())))
				if (pwd.getText().length() < minPwdLength)
					msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, shortMsg);
				else if (io.saveFile(pwdStr, cData.getFile())) {
					cData.setModified(false);
					dialog.close();

					if (cData.isExitAfterSave())
						shell.close();
					else if (cData.isClearAfterSave())
						action.clearData();
				}
		} else if (!isEmptyString(pwdStr) && io.openFile(pwdStr)) {
			cData.setLocked(false);
			cData.setModified(false);
			cData.setReadOnly(true);
			dialog.close();
		}

		if (!dialog.isDisposed())
			pwd.setFocus();

		if (shell != null && !shell.isDisposed()) {
			action.enableItems();
			action.setText();
		}
	}

	/**
	 * Open.
	 *
	 * @param confirm the confirm
	 */
	public void open(final boolean confirm) {
		final var cData = action.getCData();
		final var layout = new GridLayout(4, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;

		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.SYSTEM_MODAL, layout, cData.passTitl);

		newLabel(dialog, SWT.HORIZONTAL, cData.passWord);
		final var pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
		pwd.setFocus();
		pwd.setTextLimit(64);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> confirmPassword(dialog)), cData.entrOkay));

		if (confirm) {
			newLabel(dialog, SWT.HORIZONTAL, cData.passConf);
			final var pwdConfirm = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
			pwd.addModifyListener(e -> testPassword(e, cData, pwdConfirm));
			pwdConfirm.addModifyListener(e -> testPassword(e, cData, pwd));
			pwdConfirm.setTextLimit(64);

			emptyLabel(dialog);
			emptyLabel(dialog);

			final var label = newLabel(dialog, SWT.HORIZONTAL, cData.passShor + cData.getPasswordMinLength());
			label.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			dialog.setSize(500, 150);
		} else
			dialog.setSize(500, 100);

		setCenter(dialog);

		dialog.open();
	}
}
