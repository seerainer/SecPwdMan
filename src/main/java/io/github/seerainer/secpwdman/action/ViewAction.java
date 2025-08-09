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
package io.github.seerainer.secpwdman.action;

import java.util.Objects;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The class ViewAction.
 */
public class ViewAction extends Action {

    /**
     * Instantiates a new view action.
     *
     * @param cData the cdata
     * @param shell the shell
     * @param table the table
     */
    public ViewAction(final ConfigData cData, final Shell shell, final Table table) {
	super(cData, shell, table);
    }

    private static FontData[] getFontData(final Control control) {
	return control.getFont().getFontData();
    }

    /**
     * Changes the font of the table or the dialogs.
     *
     * @param if true change the font of the shell otherwise the table
     */
    public void changeFont(final boolean isShell) {
	final var fontDialog = new FontDialog(shell);
	fontDialog.setFontList(isShell ? getFontData(shell) : getFontData(table));
	final var fontData = fontDialog.open();
	if (Objects.isNull(fontData)) {
	    return;
	}
	final var font = new Font(shell.getDisplay(), fontData);
	if (isShell) {
	    shell.setFont(font);
	} else {
	    table.setFont(font);
	    getList().setFont(font);
	}
    }

    /**
     * Opens the group list.
     */
    public void openGroupList() {
	final var form = (SashForm) shell.getChildren()[1];
	final var list = getList();
	if (list.isVisible()) {
	    resetGroupList();
	    list.setVisible(false);
	} else {
	    list.setVisible(true);
	    fillGroupList();
	}
	form.requestLayout();
    }

    /**
     * Read only switch.
     */
    public void readOnlySwitch() {
	cData.setReadOnly(!cData.isReadOnly());
	updateUI();
    }

    /**
     * Shows or hides the password column.
     *
     * @param e the SelectionEvent
     */
    public void showPasswordColumn(final SelectionEvent e) {
	final var viewMenu = getMenu().getItem(3).getMenu();
	if (viewMenu.getItem(7).getSelection()) {
	    hidePasswordColumn();
	} else if (((MenuItem) e.widget).getSelection()) {
	    final var map = cData.getColumnMap();
	    final var passwordColumn = table.getColumn(map.get(csvHeader[5]).intValue());
	    passwordColumn.setResizable(true);
	    resizeColumns();
	    table.getColumn(map.get(csvHeader[2]).intValue()).setText(headerOp);
	    table.redraw();
	}
    }
}
