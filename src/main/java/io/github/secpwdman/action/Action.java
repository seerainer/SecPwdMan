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

import static io.github.secpwdman.util.Util.arrayToString;
import static io.github.secpwdman.util.Util.clear;
import static io.github.secpwdman.util.Util.getCollator;
import static io.github.secpwdman.util.Util.getFilePath;
import static io.github.secpwdman.util.Util.getHashMap;
import static io.github.secpwdman.util.Util.getHashSet;
import static io.github.secpwdman.util.Util.getSecureRandom;
import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.isFileOpen;
import static io.github.secpwdman.util.Util.isUrl;
import static io.github.secpwdman.widgets.Widgets.msg;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;

import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.crypto.Crypto;
import io.github.secpwdman.io.IO;

/**
 * The abstract class Action.
 */
public abstract class Action {
	final ConfData cData;
	final Shell shell;
	final Table table;

	/**
	 * Instantiates a new action.
	 *
	 * @param cData the cdata
	 * @param shell the shell
	 * @param table the table
	 */
	Action(final ConfData cData, final Shell shell, final Table table) {
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
		if (cData.isCustomHeader())
			return;

		final var index = cData.getColumnMap().get(cData.csvHeader[3]).intValue();

		for (final var item : table.getItems()) {
			item.setForeground(index, cData.getTextColor());

			if (isUrl(item.getText(index)))
				item.setForeground(index, cData.getLinkColor());
		}
	}

	/**
	 * Creates the columns.
	 *
	 * @param header the header
	 */
	private void createColumns(final String[] header) {
		table.removeAll();

		while (table.getColumnCount() > 0)
			table.getColumns()[0].dispose();

		for (final var head : header) {
			final var col = new TableColumn(table, SWT.NONE);
			col.addSelectionListener(widgetSelectedAdapter(this::sortTable));
			col.setMoveable(true);
			col.setText(head);
			col.setWidth(cData.getColumnWidth());
		}
	}

	/**
	 * Creates the header.
	 *
	 * @param header the header
	 */
	public void createHeader(final String[] header) {
		final var csvHeader = cData.csvHeader;
		final var map = getHashMap();

		if (header == null) {
			cData.setHeader(arrayToString(cData, csvHeader));

			for (var i = 0; i < csvHeader.length; i++)
				map.put(csvHeader[i], Integer.valueOf(i));

			cData.setColumnMap(map);
			cData.setCustomHeader(false);
			createColumns(cData.tableHeader);
			hideColumns();
			return;
		}

		final var length = header.length;
		final var newHeader = new String[length];
		cData.setHeader(arrayToString(cData, header));

		for (var j = 0; j < header.length; j++)
			if (header[j].equalsIgnoreCase(csvHeader[0])) {
				map.put(csvHeader[0], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[0];
			} else if (header[j].equalsIgnoreCase(csvHeader[1])) {
				map.put(csvHeader[1], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[1];
			} else if (header[j].equalsIgnoreCase(csvHeader[2])) {
				map.put(csvHeader[2], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[2];
			} else if (header[j].equalsIgnoreCase(csvHeader[3])) {
				map.put(csvHeader[3], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[3];
			} else if (header[j].equalsIgnoreCase(csvHeader[4])) {
				map.put(csvHeader[4], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[4];
			} else if (header[j].equalsIgnoreCase(csvHeader[5])) {
				map.put(csvHeader[5], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[5];
			} else if (header[j].equalsIgnoreCase(csvHeader[6])) {
				map.put(csvHeader[6], Integer.valueOf(j));
				newHeader[j] = cData.tableHeader[6];
			}

		cData.setColumnMap(map);

		if (map.containsKey(csvHeader[0]) && map.containsKey(csvHeader[1]) && map.containsKey(csvHeader[2]) && map.containsKey(csvHeader[3])
				&& map.containsKey(csvHeader[4]) && map.containsKey(csvHeader[5]) && map.containsKey(csvHeader[6])) {
			cData.setCustomHeader(false);
			createColumns(newHeader);
			hideColumns();
		} else {
			cData.setCustomHeader(true);
			createColumns(header);
		}
	}

	/**
	 * Decrypt/Encrypt data for the group list.
	 *
	 * @param data    the data
	 * @param encrypt true if encrypt
	 * @return byte[]
	 */
	public byte[] cryptData(final byte[] data, final boolean encrypt) {
		if (data == null)
			return null;

		final var oldArgo = cData.isArgon2id();
		final var oldIter = cData.getPBKDFIter();
		cData.setArgon2id(false);
		cData.setPBKDFIter(Double.SIZE * Double.SIZE);

		byte[] b = null;

		try {
			if (encrypt) {
				final var random = getSecureRandom();
				final var pwd = new byte[random.nextInt(Integer.SIZE) + Integer.SIZE];
				random.nextBytes(pwd);
				b = new Crypto(cData).encrypt(data, pwd);
				cData.setKey(pwd);
				clear(data);
			} else
				b = new Crypto(cData).decrypt(data, cData.getKey());
		} catch (final Exception e) {
			msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		}

		cData.setArgon2id(oldArgo);
		cData.setPBKDFIter(oldIter);

		return b;
	}

	/**
	 * Enable menu items.
	 */
	public void enableItems() {
		final var menu = shell.getMenuBar();
		final var file = menu.getItem(0).getMenu();
		final var edit = menu.getItem(1).getMenu();
		final var find = menu.getItem(2).getMenu();
		final var view = menu.getItem(3).getMenu();
		final var isFileOpen = isFileOpen(cData.getFile());
		final var isModified = cData.isModified();
		final var isDefaultHeader = !cData.isCustomHeader();
		final var isUnlocked = !cData.isLocked();
		final var isWriteable = !cData.isReadOnly();
		final var itemCount = table.getItemCount();
		final var selectionCount = table.getSelectionCount();
		file.getItem(1).setEnabled(!isFileOpen);
		file.getItem(2).setEnabled(itemCount > 0 && isModified);
		file.getItem(4).setEnabled(isFileOpen && !isModified && isDefaultHeader);
		file.getItem(6).setEnabled(!isFileOpen);
		file.getItem(7).setEnabled(itemCount > 0);
		edit.getItem(0).setEnabled(isUnlocked && isWriteable && isDefaultHeader);
		edit.getItem(1).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(3).setEnabled(itemCount > 0 && isWriteable);
		edit.getItem(4).setEnabled(selectionCount > 0 && isWriteable);
		edit.getItem(6).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(7).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(8).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(9).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(11).setEnabled(selectionCount == 1 && isDefaultHeader && isUrl(cData, table));
		find.getItem(0).setEnabled(itemCount > 1);
		view.getItem(0).setEnabled(isFileOpen && isUnlocked && !isModified && isDefaultHeader);
		view.getItem(0).setSelection(cData.isReadOnly());
		view.getItem(2).setEnabled(isDefaultHeader);
		view.getItem(6).setEnabled(view.getItem(7).getSelection() && isDefaultHeader);
		view.getItem(7).setEnabled(view.getItem(6).getSelection() && isDefaultHeader);
		view.getItem(11).setEnabled(isUnlocked);

		final var toolBar = getToolBar();
		toolBar.getItem(0).setEnabled(file.getItem(1).getEnabled());
		toolBar.getItem(1).setEnabled(file.getItem(2).getEnabled());
		toolBar.getItem(3).setEnabled(file.getItem(4).getEnabled());
		toolBar.getItem(5).setEnabled(edit.getItem(0).getEnabled());
		toolBar.getItem(6).setEnabled(edit.getItem(1).getEnabled());
		toolBar.getItem(8).setEnabled(find.getItem(0).getEnabled());
		toolBar.getItem(10).setEnabled(edit.getItem(6).getEnabled());
		toolBar.getItem(11).setEnabled(edit.getItem(7).getEnabled());
		toolBar.getItem(12).setEnabled(edit.getItem(8).getEnabled());
		toolBar.getItem(13).setEnabled(edit.getItem(9).getEnabled());
		toolBar.getItem(15).setEnabled(edit.getItem(11).getEnabled());
	}

	/**
	 * Fill group list.
	 */
	public void fillGroupList() {
		final var list = getList();

		if (list.isVisible() && !cData.isCustomHeader()) {
			final var set = getHashSet();
			final var index = cData.getColumnMap().get(cData.csvHeader[1]).intValue();

			for (final var item : table.getItems())
				set.add(item.getText(index));

			list.setRedraw(false);
			list.removeAll();
			list.add(cData.listFirs);

			for (final var text : set)
				if (!isEmpty(text))
					list.add(text);

			list.setRedraw(true);
		}
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
	public List getList() {
		return (List) ((SashForm) shell.getChildren()[1]).getChildren()[0];
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
	 * Gets the table.
	 *
	 * @return the table
	 */
	ToolBar getToolBar() {
		return (ToolBar) shell.getChildren()[0];
	}

	/**
	 * Hide uuid, group and password column.
	 */
	private void hideColumns() {
		final var map = cData.getColumnMap();
		final var uuid = table.getColumn(map.get(cData.csvHeader[0]).intValue());
		final var group = table.getColumn(map.get(cData.csvHeader[1]).intValue());
		uuid.setResizable(false);
		uuid.setWidth(0);
		group.setResizable(false);
		group.setWidth(0);

		hidePasswordColumn();
	}

	/**
	 * Hide password column.
	 */
	void hidePasswordColumn() {
		final var map = cData.getColumnMap();
		final var pwdIndex = map.get(cData.csvHeader[5]).intValue();
		final var pwdCol = table.getColumn(pwdIndex);

		if (pwdCol.getResizable()) {
			pwdCol.setWidth(0);
			pwdCol.setResizable(false);
			final var viewMenu = shell.getMenuBar().getItem(3).getMenu();
			viewMenu.getItem(6).setSelection(false);
			viewMenu.getItem(7).setSelection(true);
			final var titleIndex = map.get(cData.csvHeader[2]).intValue();
			table.getColumn(titleIndex).setText(cData.tableHeader[2]);
		}
	}

	/**
	 * Reset group list.
	 */
	public void resetGroupList() {
		final var list = getList();

		if (list.isVisible() && list.getSelectionIndex() > 0) {
			list.setSelection(0);
			setGroupSelection();
		}
	}

	/**
	 * Resize columns.
	 */
	public void resizeColumns() {
		for (final var col : table.getColumns())
			if (col.getResizable())
				if (shell.getMenuBar().getItem(3).getMenu().getItem(4).getSelection())
					col.pack();
				else
					col.setWidth(cData.getColumnWidth());
	}

	/**
	 * Fill table with selected group.
	 */
	public void setGroupSelection() {
		final var index = getList().getSelectionIndex();

		if (index < 0)
			return;

		var data = cryptData(cData.getData(), false);

		if (data == null)
			data = IO.extractData(cData, table);

		try {
			new IO(this).fillTable(false, data);
		} catch (final IOException ex) {
			msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
		} finally {
			data = null;
		}
	}

	/**
	 * Sets the menu, shell and toolbar text.
	 */
	public void setText() {
		final var file = cData.getFile();

		if (isFileOpen(file)) {
			final var filePath = getFilePath(file);

			if (cData.isModified())
				shell.setText(ConfData.APP_NAME + cData.titleMD + filePath);
			else
				shell.setText(ConfData.APP_NAME + cData.titlePH + filePath);
		} else
			shell.setText(ConfData.APP_NAME);

		final var menu = shell.getMenuBar();
		final var tool = getToolBar();
		final var lockMenu = menu.getItem(0).getMenu().getItem(4);
		final var lockTool = tool.getItem(3);

		if (cData.isLocked()) {
			lockMenu.setText(cData.menuUnlo);
			lockTool.setToolTipText(cData.menuUnlo);
		} else {
			lockMenu.setText(cData.menuLock);
			lockTool.setToolTipText(cData.menuLock);
		}

		final var readCont = table.getMenu().getItem(8);
		final var readMenu = menu.getItem(1).getMenu().getItem(1);
		final var readTool = tool.getItem(6);

		if (cData.isReadOnly()) {
			readCont.setText(cData.menuVent);
			readMenu.setText(cData.menuVent);
			readTool.setToolTipText(cData.entrView);
		} else {
			readCont.setText(cData.menuEent);
			readMenu.setText(cData.menuEent);
			readTool.setToolTipText(cData.entrEdit);
		}
	}

	/**
	 * Sort table.
	 *
	 * @param e the SelectionEvent
	 */
	private void sortTable(final SelectionEvent e) {
		if (table.getItemCount() < 2)
			return;

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

		for (; index < count; index++)
			if (selectedColumn.equals(table.getColumn(index)))
				break;

		final var collator = getCollator();
		var items = table.getItems();

		for (var i = 1; i < items.length; i++)
			for (var j = 0; j < i; j++) {
				final var value1 = items[i].getText(index);
				final var value2 = items[j].getText(index);
				final var up = collator.compare(value1, value2) < 0 && dir == SWT.UP;
				final var down = collator.compare(value1, value2) > 0 && dir == SWT.DOWN;

				if (up || down) {
					final var values = new String[count];

					for (var k = 0; k < count; k++)
						values[k] = items[i].getText(k);

					items[i].dispose();
					new TableItem(table, SWT.NONE, j).setText(values);
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
