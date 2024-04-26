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

import static io.github.secpwdman.util.Util.getImage;
import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.msgShowPasswords;
import static io.github.secpwdman.util.Util.setCenter;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.images.IMG;
import io.github.secpwdman.io.IO;

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

		final var image = getImage(shell.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE, image, layout, cData.textView);
		final var isWriteable = !cData.isReadOnly();
		final var tableData = new String(IO.extractData(cData, table));
		final var text = newText(dialog, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(isWriteable);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setText(tableData);

		dialog.addShellListener(shellClosedAdapter(e -> {
			final var textData = text.getText().replaceAll(System.lineSeparator(), cData.newLine);

			if (isWriteable && !isEmpty(textData) && !tableData.equals(textData))
				try {
					new IO(action).fillTable(true, textData.getBytes());
					cData.setModified(true);

					action.colorURL();
					action.fillGroupList();
					action.resizeColumns();
					action.enableItems();
					action.setText();
				} catch (final Exception ex) {
					msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
				}
		}));

		setCenter(dialog);

		if (isWriteable)
			dialog.setText(cData.textView + cData.textWarn);

		image.dispose();
		dialog.open();
	}
}
