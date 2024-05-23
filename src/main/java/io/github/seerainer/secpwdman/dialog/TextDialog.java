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
package io.github.seerainer.secpwdman.dialog;

import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgShowPasswords;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.util.Util.isEmpty;
import static io.github.seerainer.secpwdman.widgets.Widgets.newText;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.images.IMG;

/**
 * The Class TextDialog.
 */
public class TextDialog {

	private final Action action;

	/**
	 * Instantiates a new text dialog.
	 *
	 * @param action the action
	 */
	public TextDialog(final Action action) {
		this.action = action;
		open();
	}

	/**
	 * Open.
	 */
	private void open() {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var table = action.getTable();

		if (table.getItemCount() > 0 && !cData.isCustomHeader() && !msgShowPasswords(cData, shell))
			return;

		SearchDialog.close();

		final var image = getImage(shell.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE, image, layout, cData.textView);
		final var isWriteable = !cData.isReadOnly();
		final var tableData = new String(action.extractData());
		final var text = newText(dialog, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(isWriteable);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setText(tableData);

		dialog.addShellListener(shellClosedAdapter(e -> {
			final var textData = text.getText().replaceAll(System.lineSeparator(), cData.newLine);

			if (isWriteable && !isEmpty(textData) && !tableData.equals(textData)) {
				action.fillTable(true, textData.getBytes());
				cData.setModified(true);

				action.colorURL();
				action.fillGroupList();
				action.resizeColumns();
				action.enableItems();
				action.setText();
			}
		}));

		setCenter(dialog);

		if (isWriteable)
			dialog.setText(cData.textView + cData.textWarn);

		image.dispose();
		dialog.open();
	}
}
