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
package io.github.seerainer.secpwdman.action;

import static io.github.seerainer.secpwdman.util.Util.isEmpty;

import java.util.ArrayList;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.config.ConfData;

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

		if (index == cData.getColumnMap().get(cData.csvHeader[5]).intValue())
			display.timerExec(cData.getClearPassword() * ConfData.SECONDS, this::clearClipboard);
	}

	/**
	 * Delete selected line(s).
	 */
	public void deleteLine() {
		cData.setModified(true);
		table.setRedraw(false);

		if (getList().isVisible()) {
			var items = table.getSelection();
			final var arrayList = new ArrayList<String>(items.length);

			for (final var item : items)
				arrayList.add(item.getText(0));

			resetGroupList();

			for (final var item : table.getItems())
				for (final var text : arrayList)
					if (text.equals(item.getText(0))) {
						item.dispose();
						break;
					}

			items = null;
			arrayList.clear();
		} else
			table.remove(table.getSelectionIndices());

		table.setRedraw(true);
		cData.setData(cryptData(extractData(), true));

		enableItems();
		fillGroupList();
		setText();
	}
}
