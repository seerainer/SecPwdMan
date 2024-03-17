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
package io.github.secpwdman.action;

import static io.github.secpwdman.util.Util.isEmptyString;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.dialog.RandomPassword;

/**
 * The Class EditAction.
 */
public class EditAction extends Action {

	/**
	 * Instantiates a new edit action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	public EditAction(final ConfData cData, final Shell shell, final Table table) {
		super(cData, shell, table);
	}

	/**
	 * Copy to clipboard.
	 *
	 * @param index the index
	 */
	public void copyToClipboard(final int index) {
		var text = table.getItem(table.getSelectionIndex()).getText(index);

		if (isEmptyString(text))
			text = cData.nullStr;

		final var display = shell.getDisplay();
		final var cb = new Clipboard(display);
		cb.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
		cb.dispose();

		if (index == 5)
			display.timerExec(cData.getClearPasswd() * 1000, this::clearClipboard);
	}

	/**
	 * Delete selected line(s).
	 */
	public void deleteLine() {
		cData.setModified(true);
		table.setRedraw(false);
		table.remove(table.getSelectionIndices());
		table.setRedraw(true);

		enableItems();
		setText();
	}

	/**
	 * Edits the entry.
	 *
	 * @param newEntry  true if new entry
	 * @param dialog    the dialog
	 * @param tableItem the table item
	 */
	public void editEntry(final boolean newEntry, final Shell dialog, final TableItem tableItem) {
		final var child = dialog.getChildren();
		final var uuid = ((Text) child[0]).getText();
		final var group = ((Text) child[1]).getText();
		final var title = ((Text) child[3]).getText();
		final var url = ((Text) child[5]).getText();
		final var user = ((Text) child[7]).getText();
		final var pass = ((Text) child[9]).getText();
		final var notes = ((Text) child[11]).getText();
		final var textFields = new String[] { uuid, group, title, url, user, pass, notes };

		if (!newEntry && tableItem != null) {
			final var items = new String[textFields.length];
			for (var i = 0; i < items.length; i++)
				items[i] = tableItem.getText(i);

			if (Arrays.equals(items, textFields)) {
				dialog.close();
				return;
			}
		}

		if (!isEmptyString(textFields[2]) || !isEmptyString(textFields[4])) {
			if (isEmptyString(textFields[0]))
				textFields[0] = UUID.randomUUID().toString().toUpperCase();

			final var groupChildren = ((Group) child[13]).getChildren();
			final boolean[] selection = { false, false, false, false };

			for (var j = 0; j < selection.length; j++)
				selection[j] = !((Button) groupChildren[j]).getSelection();

			if (selection[0] && selection[1] && selection[2] && selection[3])
				for (var k = 0; k < selection.length; k++)
					((Button) groupChildren[k]).setSelection(true);

			if (isEmptyString(textFields[5]))
				textFields[5] = new RandomPassword(this).generate(groupChildren);

			if (!isEmptyString(textFields[6]))
				textFields[6] = notes.replaceAll(System.lineSeparator(), cData.newLine);

			if (newEntry)
				new TableItem(table, SWT.NONE).setText(textFields);
			else
				table.getItem(table.getSelectionIndex()).setText(textFields);

			dialog.close();
			cData.setModified(true);
			colorURL();
			enableItems();
			resizeColumns();
			setText();
			table.redraw();
		} else
			((Text) child[3]).setFocus();
	}
}
