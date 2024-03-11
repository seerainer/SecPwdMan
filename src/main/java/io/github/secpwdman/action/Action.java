/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
 * philipp@seerainer.com
 * http://www.seerainer.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
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

import static io.github.secpwdman.util.Util.isFileOpen;
import static io.github.secpwdman.util.Util.isUrl;

import java.io.File;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;

import io.github.secpwdman.config.ConfData;

/**
 * The Class Action.
 */
public abstract class Action {
	protected final ConfData cData;
	protected final Shell shell;
	protected final Table table;

	/**
	 * Instantiates a new action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	public Action(final ConfData cData, final Shell shell, final Table table) {
		this.cData = cData;
		this.shell = shell;
		this.table = table;
	}

	/**
	 * Clear clipboard.
	 */
	public void clearClipboard() {
		final var cb = new Clipboard(shell.getDisplay());
		cb.setContents(new Object[] { cData.nullStr }, new Transfer[] { TextTransfer.getInstance() });
		cb.clearContents();
		cb.dispose();
	}

	/**
	 * Color URL.
	 */
	public void colorURL() {
		for (var i = 0; table.getItemCount() > i; i++) {
			final var item = table.getItem(i);
			item.setForeground(3, cData.getTextColor());

			if (isUrl(item.getText(3)))
				item.setForeground(3, cData.getLinkColor());
		}
	}

	/**
	 * Enable menu items.
	 */
	public void enableItems() {
		final var menu = shell.getMenuBar();
		final var file = menu.getItem(0).getMenu();
		final var edit = menu.getItem(1).getMenu();
		final var view = menu.getItem(2).getMenu();
		final var isFileOpen = isFileOpen(cData.getFile());
		final var isModified = cData.isModified();
		final var isNotCustHead = !cData.isCustomHeader();
		final var isNotLocked = !cData.isLocked();
		final var isNotReadOnly = !cData.isReadOnly();
		final var itemCount = table.getItemCount();
		final var selectionCount = table.getSelectionCount();
		file.getItem(1).setEnabled(!isFileOpen);
		file.getItem(2).setEnabled(itemCount > 0 && isModified);
		file.getItem(4).setEnabled(isFileOpen && !isModified);
		file.getItem(6).setEnabled(!isFileOpen);
		file.getItem(7).setEnabled(itemCount > 0);
		edit.getItem(0).setEnabled(isNotLocked && isNotReadOnly && isNotCustHead);
		edit.getItem(1).setEnabled(selectionCount == 1 && isNotCustHead);
		edit.getItem(3).setEnabled(itemCount > 0 && isNotReadOnly);
		edit.getItem(4).setEnabled(selectionCount > 0 && isNotReadOnly);
		edit.getItem(6).setEnabled(selectionCount == 1 && isNotCustHead);
		edit.getItem(7).setEnabled(selectionCount == 1 && isNotCustHead);
		edit.getItem(8).setEnabled(selectionCount == 1 && isNotCustHead);
		edit.getItem(9).setEnabled(selectionCount == 1 && isNotCustHead);
		edit.getItem(11).setEnabled(isUrl(selectionCount == 1, table) && isNotCustHead);
		view.getItem(0).setEnabled(isFileOpen && isNotLocked && !isModified && isNotCustHead);
		view.getItem(0).setSelection(cData.isReadOnly());
		view.getItem(4).setEnabled(view.getItem(5).getSelection() && isNotCustHead);
		view.getItem(5).setEnabled(view.getItem(4).getSelection() && isNotCustHead);

		final var toolBar = (ToolBar) shell.getChildren()[0];
		toolBar.getItem(0).setEnabled(file.getItem(1).getEnabled());
		toolBar.getItem(1).setEnabled(file.getItem(2).getEnabled());
		toolBar.getItem(3).setEnabled(file.getItem(4).getEnabled());
		toolBar.getItem(5).setEnabled(edit.getItem(0).getEnabled());
		toolBar.getItem(6).setEnabled(edit.getItem(1).getEnabled());
		toolBar.getItem(8).setEnabled(edit.getItem(6).getEnabled());
		toolBar.getItem(9).setEnabled(edit.getItem(7).getEnabled());
		toolBar.getItem(10).setEnabled(edit.getItem(8).getEnabled());
		toolBar.getItem(11).setEnabled(edit.getItem(9).getEnabled());
		toolBar.getItem(13).setEnabled(edit.getItem(11).getEnabled());
	}

	/**
	 * Gets the cdata.
	 *
	 * @return the cdata
	 */
	public ConfData getCData() {
		return cData;
	}

	/**
	 * Gets the shell.
	 *
	 * @return the shell
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * Hide password column.
	 */
	public void hidePasswordColumn() {
		if (cData.isCustomHeader())
			return;

		final var pwdCol = table.getColumn(5);

		if (pwdCol.getResizable()) {
			pwdCol.setWidth(0);
			pwdCol.setResizable(false);
			final var viewMenu = shell.getMenuBar().getItem(2).getMenu();
			viewMenu.getItem(4).setSelection(false);
			viewMenu.getItem(5).setSelection(true);
			table.getColumn(2).setText(cData.defaultHeader[2]);
		}
	}

	/**
	 * Resize columns.
	 */
	public void resizeColumns() {
		for (var i = 0; i < table.getColumnCount(); i++) {
			final var col = table.getColumn(i);

			if (col.getResizable())
				if (shell.getMenuBar().getItem(2).getMenu().getItem(2).getSelection())
					col.pack();
				else
					col.setWidth(cData.getColumnWidth());
		}
	}

	/**
	 * Sets the menu, shell and toolbar text.
	 */
	public void setText() {
		final var file = cData.getFile();

		if (isFileOpen(file)) {
			final var filePath = new File(file).getAbsolutePath();

			if (cData.isModified())
				shell.setText(ConfData.APP_NAME + cData.titleMD + filePath);
			else
				shell.setText(ConfData.APP_NAME + cData.titlePH + filePath);
		} else
			shell.setText(ConfData.APP_NAME);

		final var menu = shell.getMenuBar();
		final var tool = ((ToolBar) shell.getChildren()[0]);
		final var lockMenu = menu.getItem(0).getMenu().getItem(4);
		final var lockTool = tool.getItem(3);

		if (cData.isLocked()) {
			lockMenu.setText(cData.menuUnlo);
			lockTool.setToolTipText(cData.menuUnlo);
		} else {
			lockMenu.setText(cData.menuLock);
			lockTool.setToolTipText(cData.menuLock);
		}

		final var lognCont = table.getMenu().getItem(8);
		final var lognMenu = menu.getItem(1).getMenu().getItem(1);
		final var lognTool = tool.getItem(6);

		if (cData.isReadOnly()) {
			lognCont.setText(cData.menuVent);
			lognMenu.setText(cData.menuVent);
			lognTool.setToolTipText(cData.entrView);
		} else {
			lognCont.setText(cData.menuEent);
			lognMenu.setText(cData.menuEent);
			lognTool.setToolTipText(cData.entrEdit);
		}
	}
}
