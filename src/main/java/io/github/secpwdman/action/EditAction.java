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

import static io.github.secpwdman.util.Util.isEmpty;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.secpwdman.config.ConfData;

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
	 * @param index the selected index number
	 */
	public void copyToClipboard(final int index) {
		var text = table.getItem(table.getSelectionIndex()).getText(index);

		if (isEmpty(text))
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
}
