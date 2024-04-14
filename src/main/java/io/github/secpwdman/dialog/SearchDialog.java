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

import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.secpwdman.action.FileAction;

/**
 * The Class SearchDialog.
 */
public class SearchDialog {
	private static Shell dialog;

	public static final Shell getDialog() {
		return dialog;
	}

	private final FileAction action;

	/**
	 * Instantiates a new search dialog.
	 *
	 * @param action the action
	 */
	public SearchDialog(final FileAction action) {
		this.action = action;

		if (dialog != null && !dialog.isDisposed())
			dialog.close();
	}

	/**
	 * Open.
	 */
	public void open() {
		final var cData = action.getCData();
		final var layout = new GridLayout(4, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;

		dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.TOOL, layout, cData.searTitl);
		dialog.addShellListener(shellClosedAdapter(e -> ((Shell) e.widget).dispose()));

		newLabel(dialog, SWT.HORIZONTAL, cData.searText);
		final var text = newText(dialog, SWT.BORDER | SWT.SINGLE);
		text.selectAll();
		text.setFocus();
		text.setTextLimit(50);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> search()), cData.entrOkay));
		dialog.setSize(500, 100);
		dialog.open();
	}

	/**
	 * Search the table.
	 */
	private void search() {
		final var text = ((Text) dialog.getChildren()[1]);
		text.selectAll();

		final var value = text.getText();

		if (isEmpty(value))
			return;

		final var table = action.getTable();
		final var itemCount = table.getItemCount();
		final var columnCount = table.getColumnCount();
		final var selectionCount = table.getSelectionCount();
		var selectionIndex = table.getSelectionIndex();

		if (selectionCount == 1)
			selectionIndex += 1;
		else
			selectionIndex = 0;

		final var shell = action.getShell();

		for (var i = selectionIndex; i < itemCount; i++)
			for (var j = 0; j < columnCount; j++)
				if (table.getItem(i).getText(j).contains(value)) {
					table.setSelection(i);
					shell.forceActive();
					table.setFocus();
					return;
				}

		final var cData = action.getCData();
		msg(shell, SWT.ICON_INFORMATION | SWT.OK, cData.titleInf, cData.doubleQ + value + cData.doubleQ + cData.searMess);

		text.setFocus();
		table.setSelection(-1);
	}
}
