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

import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgYesNo;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import org.eclipse.swt.SWT;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.CharsetUtil;

/**
 * The record TextDialog.
 */
record TextDialog(Action action) implements Icons, StringConstants {

	void open() {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var table = action.getTable();

		if (table.getItemCount() > 0 && !cData.isCustomHeader() && !msgYesNo(cData, shell, warnPass)) {
			return;
		}

		SearchDialog.close();

		final var image = getImage(shell.getDisplay(), APP_ICON);
		final var isWriteable = !cData.isReadOnly();
		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE, image,
				getLayout(), isWriteable ? textView + textWarn : textView);

		final var tableData = CharsetUtil.toChars(action.extractData());
		final var text = text(dialog, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(isWriteable);
		text.setLayoutData(getGridData(SWT.FILL, SWT.FILL, 1, 1));
		text.setTextChars(tableData);

		dialog.addShellListener(shellClosedAdapter(e -> {
			final var textData = CharsetUtil.replaceSequence(text.getTextChars(), System.lineSeparator().toCharArray(),
					newLine.toCharArray());
			if (isWriteable && textData.length > 0 && !isEqual(tableData, textData)) {
				cData.setModified(true);
				action.fillTable(true, CharsetUtil.toBytes(textData));
				action.fillGroupList();
				action.resizeColumns();
				action.updateUI();
			}
			clear(tableData);
			clear(textData);
		}));

		setCenter(dialog);
		image.dispose();
		dialog.open();
	}
}
