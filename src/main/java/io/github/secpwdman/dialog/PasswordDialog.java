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

import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import io.github.secpwdman.action.FileAction;

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
		dialog.setLayout(layout);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		newLabel(dialog, SWT.HORIZONTAL, cData.passWord);
		var pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
		pwd.setFocus();
		pwd.setTextLimit(64);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> action.confirmPassword(dialog)), cData.entrOkay));

		if (confirm) {
			newLabel(dialog, SWT.HORIZONTAL, cData.passConf);
			pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
			pwd.setTextLimit(64);
			dialog.setSize(500, 130);
		} else
			dialog.setSize(500, 100);

		final var r = dialog.getDisplay().getBounds();
		final var s = dialog.getBounds();
		dialog.setLocation((r.width - s.width) / 2, ((r.height - s.height) * 2) / 5);
		dialog.setText(cData.passTitl);
		dialog.open();
	}
}
