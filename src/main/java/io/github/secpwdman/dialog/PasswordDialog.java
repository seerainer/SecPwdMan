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

import static io.github.secpwdman.util.Util.setCenter;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
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

/**
 * The Class PasswordDialog.
 */
public class PasswordDialog {
	private final FileAction action;

	/**
	 * Instantiates a new password dialog.
	 *
	 * @param confirm the confirm
	 * @param action  the action
	 */
	public PasswordDialog(final boolean confirm, final FileAction action) {
		this.action = action;
		open(confirm);
	}

	/**
	 * Open.
	 *
	 * @param confirm the confirm
	 */
	private void open(final boolean confirm) {
		final var cData = action.getCData();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.SYSTEM_MODAL);
		final var layout = new GridLayout(4, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;
		dialog.setLayout(layout);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		newLabel(dialog, SWT.HORIZONTAL, cData.passWord);
		final var pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
		pwd.setFocus();
		pwd.setTextLimit(64);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> action.confirmPassword(dialog)), cData.entrOkay));

		if (confirm) {
			newLabel(dialog, SWT.HORIZONTAL, cData.passConf);
			final var pwdConfirm = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
			pwd.addModifyListener(e -> testPassword(e, cData, pwdConfirm));
			pwdConfirm.addModifyListener(e -> testPassword(e, cData, pwd));
			pwdConfirm.setTextLimit(64);

			new Label(dialog, SWT.NONE);
			new Label(dialog, SWT.NONE);

			final var label = newLabel(dialog, SWT.HORIZONTAL, cData.passShor + cData.getPasswordMinLength());
			label.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			dialog.setSize(500, 150);
		} else
			dialog.setSize(500, 100);

		dialog.setLocation(setCenter(dialog));
		dialog.setText(cData.passTitl);
		dialog.open();
	}

	/**
	 * Tests how strong the password is.
	 *
	 * @param e     the ModifyEvent e
	 * @param cData the cData
	 */
	private void testPassword(final ModifyEvent e, final ConfData cData, final Text text2) {
		final var random = new RandomPassword(action);
		final var text1 = (Text) e.widget;
		final var label = (Label) text1.getParent().getChildren()[7];
		final var text = text1.getText();

		if (text.equals(text2.getText()))
			random.evalPasswordStrength(cData, label, text);
		else {
			label.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passNoMa);
			label.setToolTipText(null);
		}
	}
}
