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

import static io.github.secpwdman.util.Util.WIN32;
import static io.github.secpwdman.util.Util.arrayToString;
import static io.github.secpwdman.util.Util.getFilePath;
import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.isEqual;
import static io.github.secpwdman.util.Util.isFileOpen;
import static io.github.secpwdman.util.Util.isReadable;
import static io.github.secpwdman.util.Util.msgShowPasswords;
import static io.github.secpwdman.widgets.Widgets.fileDialog;
import static io.github.secpwdman.widgets.Widgets.msg;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.text.Collator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.dialog.PasswordDialog;
import io.github.secpwdman.dialog.SearchDialog;
import io.github.secpwdman.io.IO;

/**
 * The Class FileAction.
 */
public class FileAction extends Action {

	/**
	 * Instantiates a new file action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	public FileAction(final ConfData cData, final Shell shell, final Table table) {
		super(cData, shell, table);
	}

	/**
	 * Clear data.
	 */
	public void clearData() {
		table.setRedraw(false);
		createColumns(cData.tableHeader);
		table.setRedraw(true);

		cData.setArgon2id(true);
		cData.setClearAfterSave(false);
		cData.setCustomHeader(false);
		cData.setExitAfterSave(false);
		cData.setFile(null);
		cData.setLocked(false);
		cData.setModified(false);
		cData.setReadOnly(false);

		clearClipboard();
		enableItems();
		fillGroupList();
		setText();
	}

	/**
	 * Creates the columns.
	 *
	 * @param isDefaultHeader true if default header
	 * @param header          the header
	 */
	public void createColumns(final boolean isDefaultHeader, final String[] header) {
		for (final var head : header) {
			final var col = new TableColumn(table, SWT.NONE);
			col.addSelectionListener(widgetSelectedAdapter(this::sortTable));
			col.setText(head);
			col.setWidth(cData.getColumnWidth());
		}

		if (isDefaultHeader) {
			final var uuid = table.getColumn(0);
			final var group = table.getColumn(1);
			uuid.setResizable(false);
			uuid.setWidth(0);
			group.setResizable(false);
			group.setWidth(0);

			hidePasswordColumn();
		}
	}

	/**
	 * Creates new table columns.
	 *
	 * @param header the header
	 */
	public void createColumns(final String[] header) {
		final var isDefaultHeader = isEqual(header, cData.tableHeader);

		if (isDefaultHeader) {
			cData.setCustomHeader(false);
			cData.setHeader(cData.csvHeader);
		} else {
			cData.setCustomHeader(true);
			final var strTrim = arrayToString(header).replace(cData.comma + cData.space, cData.comma);
			cData.setHeader(strTrim.substring(1, strTrim.length() - 1));
		}

		table.removeAll();

		while (table.getColumnCount() > 0)
			table.getColumns()[0].dispose();

		createColumns(isDefaultHeader, header);
	}

	/**
	 * Unminimize the app, ask to save before exit, dispose resources and exit.
	 *
	 * @return true, if successful
	 */
	public boolean exit() {
		if (shell.getMinimized())
			shell.setMinimized(false);

		if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0)
			switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, cData.titleWar, cData.warnExit)) {
			case SWT.YES:
				openSave(SWT.SAVE);
				cData.setExitAfterSave(true);
				return false;
			case SWT.NO:
				break;
			default:
				return false;
			}

		for (final var item : getToolBar().getItems()) {
			final var image = item.getImage();

			if (image != null)
				image.dispose();
		}

		cData.setData(null);
		table.getFont().dispose();
		shell.getFont().dispose();
		getList().getFont().dispose();
		clearClipboard();

		return true;
	}

	/**
	 * Import / export dialog.
	 *
	 * @param style the style
	 */
	public void importExport(final int style) {
		final var io = new IO(this);
		final var dialog = fileDialog(shell, style);
		dialog.setFilterNames(new String[] { cData.imexFile });
		dialog.setFilterExtensions(new String[] { cData.imexExte });

		if (style == SWT.OPEN) {
			final var f = dialog.open();

			if (isFileOpen(f) && io.openFile(null, f)) {
				cData.setModified(true);
				enableItems();
				fillGroupList();
				setText();
			}
		} else if (style == SWT.SAVE) {
			if (!cData.isCustomHeader() && !msgShowPasswords(cData, shell))
				return;

			final var f = dialog.open();
			if (!isEmpty(f))
				io.saveFile(null, f);
		}
	}

	/**
	 * Lock switch.
	 */
	public void lockSwitch() {
		if (cData.isLocked())
			new PasswordDialog(this).open(false);
		else
			setLocked();
	}

	/**
	 * Create a new database (clear data).
	 */
	public void newDatabase() {
		if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0)
			switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, cData.titleWar, cData.warnNewF)) {
			case SWT.YES:
				openSave(SWT.SAVE);
				cData.setClearAfterSave(true);
				break;
			case SWT.NO:
				clearData();
				break;
			}
		else
			clearData();
	}

	/**
	 * Open / save dialog.
	 *
	 * @param style the style
	 */
	public void openSave(final int style) {
		if (style == SWT.SAVE && isFileOpen(cData.getFile()))
			new PasswordDialog(this).open(true);
		else {
			final var dialog = fileDialog(shell, style);
			dialog.setFilterNames(new String[] { cData.passFile });
			dialog.setFilterExtensions(new String[] { cData.passExte });
			final var f = dialog.open();

			if (!isEmpty(f) && f.endsWith(cData.passExte.substring(1))) {
				cData.setFile(f);

				if (style == SWT.OPEN && isReadable(f)) {
					table.removeAll();
					cData.setLocked(true);
					cData.setModified(false);
					new PasswordDialog(this).open(false);
				} else if (style == SWT.SAVE)
					new PasswordDialog(this).open(true);
			}
		}

		enableItems();
		setText();
	}

	/**
	 * Sets the app locked.
	 */
	public void setLocked() {
		if (isFileOpen(cData.getFile()) && !cData.isModified()) {
			cData.setLocked(true);
			table.removeAll();

			final var search = SearchDialog.getDialog();

			if (search != null && !search.isDisposed())
				search.close();

			clearClipboard();
			hidePasswordColumn();
			enableItems();
			setText();

			final var tray = shell.getDisplay().getSystemTray();
			if (tray != null && WIN32) {
				shell.setMinimized(true);
				shell.setVisible(false);
				final var trayItem = tray.getItem(0);
				trayItem.setToolTipText(ConfData.APP_NAME + cData.titlePH + getFilePath(cData.getFile()));
				trayItem.setVisible(true);
			}
		}
	}

	/**
	 * Sort table.
	 *
	 * @param e the SelectionEvent
	 */
	private void sortTable(final SelectionEvent e) {
		final var list = getList();
		final var noGroupSelection = !list.isVisible() || (list.isVisible() && list.getSelectionIndex() < 1);

		if ((table.getItemCount() < 2) || (cData.isReadOnly() && noGroupSelection))
			return;

		if (noGroupSelection)
			cData.setModified(true);

		final var sortColumn = table.getSortColumn();
		final var selectedColumn = (TableColumn) e.widget;
		var dir = table.getSortDirection();

		if (sortColumn == selectedColumn)
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
		else {
			table.setSortColumn(selectedColumn);
			dir = SWT.UP;
		}

		final var count = table.getColumnCount();
		var index = 0;

		for (var i = 0; i < count; i++)
			if (selectedColumn.equals(table.getColumn(i))) {
				index = i;
				break;
			}

		final var collator = Collator.getInstance();
		var items = table.getItems();

		for (var j = 1; j < items.length; j++)
			for (var k = 0; k < j; k++) {
				final var value1 = items[j].getText(index);
				final var value2 = items[k].getText(index);
				final var up = collator.compare(value1, value2) < 0 && dir == SWT.UP;
				final var down = collator.compare(value1, value2) > 0 && dir == SWT.DOWN;
				if (up || down) {
					final var values = new String[count];
					for (var l = 0; l < count; l++)
						values[l] = items[j].getText(l);
					items[j].dispose();
					new TableItem(table, SWT.NONE, k).setText(values);
					items = table.getItems();
					break;
				}
			}

		items = null;
		colorURL();
		enableItems();
		setText();
		table.setSortDirection(dir);
	}
}
