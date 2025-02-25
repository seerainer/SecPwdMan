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
package io.github.seerainer.secpwdman.action;

import static io.github.seerainer.secpwdman.util.Util.getBase64Char;

import java.util.ArrayList;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.io.CharArrayString;

/**
 * The class EditAction.
 */
public class EditAction extends Action {

	/**
	 * Instantiates a new edit action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	public EditAction(final ConfigData cData, final Shell shell, final Table table) {
		super(cData, shell, table);
	}

	/**
	 * Copies the selected text to the clipboard.
	 *
	 * @param index the selected index number
	 */
	public void copyToClipboard(final int index) {
		final var display = shell.getDisplay();
		final var cb = new Clipboard(display);
		final var sb = new StringBuilder();
		final var cas = new CharArrayString(table.getItem(table.getSelectionIndex()).getText(index));
		if (index == cData.getColumnMap().get(csvHeader[5]).intValue()) {
			sb.append(getBase64Char(cas));
			display.timerExec(cData.getClearPassword() * SECONDS, this::clearClipboard);
		} else {
			sb.append(cas.length() > 0 ? cas.toCharArray() : nullStr.toCharArray());
			cas.clear();
		}
		cb.setContents(new Object[] { sb.toString() }, new Transfer[] { TextTransfer.getInstance() }, DND.CLIPBOARD);
		cb.dispose();
		sb.setLength(0);
	}

	/**
	 * Deletes selected line(s).
	 */
	public void deleteLine() {
		cData.setModified(true);
		table.setRedraw(false);
		if (getList().isVisible()) {
			final var items = table.getSelection();
			final var arrayList = new ArrayList<String>(items.length);
			for (final var item : items) {
				arrayList.add(item.getText(0));
			}
			resetGroupList();
			for (final var item : table.getItems()) {
				if (arrayList.contains(item.getText(0))) {
					item.dispose();
				}
			}
		} else {
			table.remove(table.getSelectionIndices());
		}
		table.setRedraw(true);
		cData.getSensitiveData().setSealedData(cryptData(extractData()));
		fillGroupList();
		updateUI();
	}
}
