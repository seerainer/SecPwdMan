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

import static io.github.secpwdman.util.Util.passWarning;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.secpwdman.config.ConfData;

/**
 * The Class ViewAction.
 */
public class ViewAction extends Action {

	/**
	 * Instantiates a new view action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	public ViewAction(final ConfData cData, final Shell shell, final Table table) {
		super(cData, shell, table);
	}

	/**
	 * Read only switch.
	 */
	public void readOnlySwitch() {
		if (cData.isReadOnly())
			cData.setReadOnly(false);
		else
			cData.setReadOnly(true);

		enableItems();
		setText();
	}

	/**
	 * Show password column.
	 *
	 * @param e the SelectionEvent
	 */
	public void showPwdColumn(final SelectionEvent e) {
		final var viewMenu = shell.getMenuBar().getItem(2).getMenu();

		if (viewMenu.getItem(5).getSelection())
			hidePasswordColumn();
		else if (((MenuItem) e.widget).getSelection())
			if (passWarning(cData, shell)) {
				table.getColumn(5).setResizable(true);
				resizeColumns();
				table.getColumn(2).setText(cData.headerOp);
				table.redraw();
			} else {
				viewMenu.getItem(4).setSelection(false);
				viewMenu.getItem(5).setSelection(true);
			}
	}
}
