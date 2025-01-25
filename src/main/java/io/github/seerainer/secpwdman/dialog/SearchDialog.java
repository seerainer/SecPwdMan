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

import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
import static io.github.seerainer.secpwdman.widgets.Widgets.label;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The record SearchDialog.
 */
public record SearchDialog(Action action) implements StringConstants {

	private static Shell dialog;

	/**
	 * Closes the dialog.
	 */
	public static void close() {
		if (dialog != null && !dialog.isDisposed()) {
			dialog.close();
		}
	}

	/**
	 * Opens the dialog.
	 */
	void open() {
		if (dialog != null && !dialog.isDisposed()) {
			dialog.forceActive();
			return;
		}

		dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.TOOL, getLayout(4, 5, 10, 10, 5, 5, 10), searTitl);

		label(dialog, SWT.HORIZONTAL, searText);
		final var text = text(dialog, SWT.BORDER | SWT.SINGLE);
		text.setFocus();
		text.setTextLimit(128);

		dialog.setDefaultButton(button(dialog, SWT.PUSH, entrOkay, widgetSelectedAdapter(e -> search())));
		dialog.setSize(480, 100);
		dialog.open();
	}

	private void search() {
		final var text = (Text) dialog.getChildren()[1];
		final var value = text.getText();
		if (isBlank(value)) {
			text.setFocus();
			return;
		}
		final var shell = action.getShell();
		final var table = action.getTable();
		final var length = value.length();
		final var itemCount = table.getItemCount();
		final var columnCount = table.getColumnCount();
		final var selectionIndex = table.getSelectionCount() == 1 ? table.getSelectionIndex() + 1 : 0;
		for (var i = selectionIndex; i < itemCount; i++) {
			for (var j = 0; j < columnCount; j++) {
				final var item = table.getItem(i).getText(j);
				for (var k = 0; k + length <= item.length(); k++) {
					if (!table.getColumn(j).getResizable()) {
						break;
					}
					if (item.substring(k, k + length).equalsIgnoreCase(value)) {
						table.setSelection(i);
						shell.forceActive();
						table.setFocus();
						return;
					}
				}
			}
		}
		msg(shell, SWT.ICON_INFORMATION | SWT.OK, titleInf, quote + value + quote + searMess);
		text.setFocus();
		table.setSelection(-1);
	}
}
