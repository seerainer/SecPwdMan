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

import static io.github.seerainer.secpwdman.util.Util.isEmpty;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.newButton;
import static io.github.seerainer.secpwdman.widgets.Widgets.newLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.newText;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.Action;

/**
 * The Class SearchDialog.
 */
public class SearchDialog {

	private static Shell dialog;

	public static final void close() {
		if (dialog != null && !dialog.isDisposed())
			dialog.close();
	}

	private final Action action;

	/**
	 * Instantiates a new search dialog.
	 *
	 * @param action the action
	 */
	public SearchDialog(final Action action) {
		this.action = action;
	}

	/**
	 * Open.
	 */
	public void open() {
		if (dialog != null && !dialog.isDisposed()) {
			dialog.forceActive();
			return;
		}

		final var cData = action.getCData();
		final var layout = new GridLayout(4, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;

		dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.TOOL, layout, cData.searTitl);

		newLabel(dialog, SWT.HORIZONTAL, cData.searText);
		final var text = newText(dialog, SWT.BORDER | SWT.SINGLE);
		text.setFocus();
		text.setTextLimit(128);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> search()), cData.entrOkay));
		dialog.setSize(480, 100);
		dialog.open();
	}

	/**
	 * Search the table.
	 */
	private void search() {
		final var text = ((Text) dialog.getChildren()[1]);
		final var value = text.getText();

		if (isEmpty(value)) {
			text.setFocus();
			return;
		}

		final var shell = action.getShell();
		final var table = action.getTable();
		final var length = value.length();
		final var itemCount = table.getItemCount();
		final var columnCount = table.getColumnCount();
		final var selectionCount = table.getSelectionCount();
		var selectionIndex = table.getSelectionIndex();

		if (selectionCount == 1)
			selectionIndex += 1;
		else
			selectionIndex = 0;

		for (var i = selectionIndex; i < itemCount; i++)
			for (var j = 0; j < columnCount; j++) {
				final var item = table.getItem(i).getText(j);

				for (var k = 0; k + length <= item.length(); k++) {
					if (!table.getColumn(j).getResizable())
						break;

					if (item.substring(k, k + length).equalsIgnoreCase(value)) {
						table.setSelection(i);
						shell.forceActive();
						table.setFocus();
						return;
					}
				}
			}

		final var cData = action.getCData();
		msg(shell, SWT.ICON_INFORMATION | SWT.OK, cData.titleInf, cData.doubleQ + value + cData.doubleQ + cData.searMess);

		text.setFocus();
		table.setSelection(-1);
	}
}
