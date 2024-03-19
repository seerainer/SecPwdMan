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

import static io.github.secpwdman.util.Util.msgShowPasswords;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newText;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import io.github.secpwdman.action.FileAction;
import io.github.secpwdman.images.IMG;
import io.github.secpwdman.io.IO;

/**
 * The Class TextDialog.
 */
public class TextDialog {
	private final FileAction action;

	/**
	 * Instantiates a new text dialog.
	 *
	 * @param action the action
	 */
	public TextDialog(final FileAction action) {
		this.action = action;
		open();
	}

	/**
	 * Open.
	 */
	private void open() {
		final var cData = action.getCData();

		if (!msgShowPasswords(cData, action.getShell()))
			return;

		final var tableData = new IO(action).extractData(cData).toString();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		final var image = IMG.getImage(dialog.getDisplay(), IMG.APP_ICON);
		final var isWriteable = !cData.isReadOnly();
		final var layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		dialog.setImage(image);
		dialog.setLayout(layout);
		dialog.setText(cData.textView);
		image.dispose();

		if (isWriteable)
			dialog.setText(cData.textView + cData.textWarn);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		final var text = newText(dialog, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(isWriteable);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		text.setText(tableData);

		dialog.addShellListener(shellClosedAdapter(e -> {
			final var textData = text.getText().replaceAll(System.lineSeparator(), cData.newLine);
			if (isWriteable && !tableData.equals(textData))
				try {
					cData.setModified(true);
					new IO(action).fillTable(textData);
				} catch (final IOException ex) {
					msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
				}
		}));

		dialog.open();
	}
}
