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

import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgYesNo;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The record TextDialog.
 */
record TextDialog(Action action) implements Icons, StringConstants {

	void open() {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var table = action.getTable();

		if (table.getItemCount() > 0 && !cData.isCustomHeader() && !msgYesNo(cData, shell, warnPass)) {
			return;
		}

		SearchDialog.close();

		final var image = getImage(shell.getDisplay(), APP_ICON);
		final var isWriteable = !cData.isReadOnly();
		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE, image,
				getLayout(), isWriteable ? textView + textWarn : textView);

		final var tableData = new String(action.extractData());
		final var text = text(dialog, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(isWriteable);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setText(tableData);

		dialog.addShellListener(shellClosedAdapter(e -> {
			final var textData = text.getText().replaceAll(System.lineSeparator(), newLine);

			if (isWriteable && !isBlank(textData) && !tableData.equals(textData)) {
				action.fillTable(true, textData.getBytes());
				cData.setModified(true);

				action.colorURL();
				action.fillGroupList();
				action.resizeColumns();
				action.updateUI();
			}
		}));

		setCenter(dialog);
		image.dispose();
		dialog.open();
	}
}
