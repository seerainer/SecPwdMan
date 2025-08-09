/*
 * SecPwdMan
 * Copyright (C) 2025  Philipp Seerainer
 * philipp@seerainer.com
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
package io.github.seerainer.secpwdman.ui;

import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
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

    Shell open() {
	final var layout = getLayout(4, 5, 10, 10, 5, 5, 10);
	final var dialog = Widgets.shell(action.getShell(), SWT.DIALOG_TRIM | SWT.TOOL, layout, searTitl);

	Widgets.label(dialog, SWT.HORIZONTAL, searText);
	final var text = Widgets.text(dialog, SWT.BORDER | SWT.SINGLE);
	text.setFocus();
	text.setTextLimit(128);

	dialog.setDefaultButton(Widgets.button(dialog, SWT.PUSH, dialOkay, widgetSelectedAdapter(_ -> search(dialog))));
	dialog.setSize(480, 100);
	dialog.open();
	return dialog;
    }

    private void search(final Shell dialog) {
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
	final var selectionCount = table.getSelectionCount();
	final var selectionIndex = selectionCount == 1 ? table.getSelectionIndex() + 1 : 0;
	for (var i = selectionIndex; i < itemCount; i++) {
	    for (var j = 0; j < columnCount; j++) {
		final var item = table.getItem(i).getText(j);
		for (var k = 0; k + length <= item.length(); k++) {
		    if (!table.getColumn(j).getResizable()) {
			break;
		    }
		    if (item.substring(k, k + length).equalsIgnoreCase(value)) {
			table.setFocus();
			table.setSelection(i);
			shell.forceActive();
			action.updateUI();
			return;
		    }
		}
	    }
	}
	final var sb = new StringBuilder();
	sb.append(quote).append(value).append(quote).append(searMess);
	Widgets.msg(shell, SWT.ICON_INFORMATION | SWT.OK, titleInf, sb.toString());
	table.setSelection(-1);
	action.updateUI();
	text.setFocus();
    }
}
