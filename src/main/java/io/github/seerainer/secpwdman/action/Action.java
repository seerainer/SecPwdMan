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

import static io.github.seerainer.secpwdman.util.URLUtil.isUrl;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.getFilePath;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static io.github.seerainer.secpwdman.util.Util.isFileReady;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static java.lang.Integer.valueOf;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

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
import org.slf4j.Logger;

import com.github.skjolber.stcsv.CsvReader;
import com.github.skjolber.stcsv.sa.StringArrayCsvReader;
import com.password4j.SecureString;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.crypto.CryptoUtil;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The abstract class Action.
 */
public abstract class Action implements CryptoConstants, PrimitiveConstants, StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	final ConfigData cData;
	final Shell shell;
	final Table table;

	Action(final ConfigData cData, final Shell shell, final Table table) {
		this.cData = cData;
		this.shell = shell;
		this.table = table;
	}

	/**
	 * Clears the clipboard.
	 */
	public void clearClipboard() {
		final var cb = new Clipboard(shell.getDisplay());
		cb.setContents(new Object[] { nullStr }, new Transfer[] { TextTransfer.getInstance() });
		cb.clearContents();
		cb.dispose();
	}

	/**
	 * Colors the URL.
	 */
	public void colorURL() {
		if (cData.isCustomHeader()) {
			return;
		}
		final var index = cData.getColumnMap().get(csvHeader[3]).intValue();

		for (final var item : table.getItems()) {
			item.setForeground(index, isUrl(item.getText(index)) ? cData.getLinkColor() : cData.getTextColor());
		}
	}

	private void createColumns(final String[] header) {
		table.removeAll();

		while (table.getColumnCount() > 0) {
			table.getColumns()[0].dispose();
		}
		for (final var head : header) {
			final var col = new TableColumn(table, SWT.NONE);
			col.addSelectionListener(widgetSelectedAdapter(this::sortTable));
			col.setMoveable(true);
			col.setText(head);
			col.setWidth(cData.getColumnWidth());
		}
	}

	/**
	 * Internal encryption for the group list.
	 *
	 * @param tableData the data
	 * @return SealedObject
	 */
	public SealedObject cryptData(final byte[] tableData) {
		if (tableData == null) {
			LOG.error("Data is null");
			return null;
		}
		try {
			final var sensitiveData = cData.getSensitiveData();
			sensitiveData.setDataKey(CryptoUtil.generateSecretKey(keyAES).getEncoded());
			return CryptoUtil.generateSealedObject(tableData, sensitiveData.getDataKey(), cipherAES, keyAES);
		} catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException
				| IOException | ClassNotFoundException e) {
			LOG.error("Error occurred", e);
			msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
			return null;
		}
	}

	private void customHeader(final String[] header) {
		final var newHeader = Arrays.copyOf(header, header.length);
		final var map = new HashMap<String, Integer>(header.length);
		for (var i = 0; i < header.length; i++) {
			for (var j = 0; j < csvHeader.length; j++) {
				if (header[i].equalsIgnoreCase(csvHeader[j])) {
					map.put(csvHeader[j], valueOf(i));
					newHeader[i] = tableHeader[j];
					break;
				}
			}
		}
		cData.setColumnMap(map);
		cData.setHeader(headerArrayToString(header));

		for (final var key : csvHeader) {
			if (!map.containsKey(key)) {
				cData.setCustomHeader(true);
				createColumns(header);
				LOG.warn("Custom header created");
				return;
			}
		}
		cData.setCustomHeader(false);
		createColumns(newHeader);
		hideColumns();
	}

	/**
	 * Creates the default header.
	 */
	public void defaultHeader() {
		final var map = new HashMap<String, Integer>(csvHeader.length);
		for (var i = 0; i < csvHeader.length; i++) {
			map.put(csvHeader[i], valueOf(i));
		}
		cData.setColumnMap(map);
		cData.setCustomHeader(false);
		cData.setHeader(headerArrayToString(csvHeader));
		createColumns(tableHeader);
		hideColumns();
	}

	/**
	 * Disables menu and toolbar items.
	 */
	public void enableItems() {
		final var menu = shell.getMenuBar();
		final var file = menu.getItem(0).getMenu();
		final var edit = menu.getItem(1).getMenu();
		final var find = menu.getItem(2).getMenu();
		final var view = menu.getItem(3).getMenu();
		final var isDefaultHeader = !cData.isCustomHeader();
		final var isFileOpen = isFileReady(cData.getFile());
		final var isModified = cData.isModified();
		final var isShowPass = cData.isShowPassword();
		final var isUnlocked = !cData.isLocked();
		final var isWriteable = !cData.isReadOnly();
		final var itemCount = table.getItemCount();
		final var selectionCount = table.getSelectionCount();

		file.getItem(1).setEnabled(!isFileOpen);
		file.getItem(2).setEnabled(itemCount > 0 && isWriteable);
		file.getItem(4).setEnabled(isFileOpen && !isModified && isUnlocked && isWriteable);
		file.getItem(6).setEnabled(isFileOpen && !isModified && isDefaultHeader);
		file.getItem(8).setEnabled(!isFileOpen);
		file.getItem(9).setEnabled(itemCount > 0 && isShowPass);

		edit.getItem(0).setEnabled(isUnlocked && isWriteable && isDefaultHeader);
		edit.getItem(1).setEnabled(selectionCount == 1 && isDefaultHeader);
		edit.getItem(3).setEnabled(itemCount > 0);
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
		view.getItem(6).setEnabled(view.getItem(7).getSelection() && isDefaultHeader && isShowPass);
		view.getItem(7).setEnabled(view.getItem(6).getSelection() && isDefaultHeader);
		view.getItem(11).setEnabled(isUnlocked && isShowPass);

		final var toolBar = getToolBar();
		toolBar.getItem(0).setEnabled(file.getItem(1).getEnabled());
		toolBar.getItem(1).setEnabled(file.getItem(2).getEnabled());
		toolBar.getItem(3).setEnabled(file.getItem(6).getEnabled());
		toolBar.getItem(5).setEnabled(edit.getItem(0).getEnabled());
		toolBar.getItem(6).setEnabled(edit.getItem(1).getEnabled());
		toolBar.getItem(8).setEnabled(find.getItem(0).getEnabled());
		toolBar.getItem(10).setEnabled(edit.getItem(6).getEnabled());
		toolBar.getItem(11).setEnabled(edit.getItem(7).getEnabled());
		toolBar.getItem(12).setEnabled(edit.getItem(8).getEnabled());
		toolBar.getItem(13).setEnabled(edit.getItem(9).getEnabled());
		toolBar.getItem(15).setEnabled(edit.getItem(11).getEnabled());
	}

	private SecureString escapeSpecialChar(final String s) {
		if (s == null) {
			LOG.warn("String is null");
			return new SecureString(new char[0]);
		}

		//@formatter:off
		final var hasSpecialChar = s.codePoints().anyMatch(c -> c > asciiLength)
								|| s.contains(quote)
								|| s.contains(comma)
								|| s.contains(newLine)
								|| s.contains(space)
								|| s.contains(String.valueOf(cData.getDivider()));
		//@formatter:on
		return new SecureString(hasSpecialChar ? (quote + s.replace(quote, quote + quote) + quote).toCharArray()
				: s.replaceAll(lineBrk, space).toCharArray());
	}

	/**
	 * Extracts all data from the table.
	 *
	 * @return the byte[]
	 */
	public byte[] extractData() {
		final var csvDivider = String.valueOf(cData.getDivider());
		final var sb = new StringBuilder(table.getItemCount() * OUT_LENGTH); // preallocate size
		sb.append(cData.getHeader()).append(newLine);

		for (final var item : table.getItems()) {
			final var itemText = new SecureString[table.getColumnCount()];
			for (var i = 0; i < itemText.length; i++) {
				itemText[i] = escapeSpecialChar(item.getText(i));
			}

			final var line = new StringBuilder();
			for (var j = 0; j < itemText.length; j++) {
				if (j > 0) {
					line.append(csvDivider);
				}
				line.append(itemText[j]);
			}
			sb.append(line).append(newLine);
		}

		return sb.toString().getBytes();
	}

	/**
	 * Fills the group list.
	 */
	public void fillGroupList() {
		final var list = getList();
		if (!list.isVisible() || cData.isCustomHeader()) {
			return;
		}
		final var set = new HashSet<String>(table.getItemCount());
		final var index = cData.getColumnMap().get(csvHeader[1]).intValue();

		for (final var item : table.getItems()) {
			set.add(item.getText(index));
		}
		list.setRedraw(false);
		list.removeAll();
		list.add(listFirs);
		set.stream().filter((final var text) -> !isBlank(text)).forEach(list::add);
		list.setSelection(0);
		list.setRedraw(true);
	}

	/**
	 * Fills the table.
	 *
	 * @param newHeader true if new header
	 * @param tableData the data
	 */
	public void fillTable(final boolean newHeader, final byte[] tableData) {
		final var reader = new InputStreamReader(new ByteArrayInputStream(tableData));
		final var length = cData.getBufferLength() * MEM_SIZE;
		final var csvDivider = cData.getDivider();
		final var builder = StringArrayCsvReader.builder().bufferLength(length);
		if (!comma.equals(String.valueOf(csvDivider))) {
			LOG.warn("Divider is not a comma");
			// TODO: Add support for escape character and quote character
			// Identical escape and quote character not supported
			builder.divider(csvDivider).escapeCharacter(newLine.charAt(0)).quoteCharacter(quote.charAt(0));
		}
		table.setRedraw(false);
		table.setSortColumn(null);
		table.removeAll();
		try (final var iterator = builder.build(reader)) {
			final var header = iterator.next();
			if (newHeader) {
				if (isEqual(header, csvHeader)) {
					defaultHeader();
				} else {
					customHeader(header);
				}
				fillTable(iterator, null);
				cData.getSensitiveData().setSealedData(cryptData(tableData));
			} else {
				final var list = getList();
				final var listSelection = list.getItem(list.getSelectionIndex());
				fillTable(iterator, listSelection.equals(listFirs) ? null : listSelection);
			}
		} catch (final Exception e) {
			LOG.error("Error occurred", e);
			msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
		}
		clear(tableData);
		colorURL();
		table.setRedraw(true);
		resizeColumns();
		table.redraw();
	}

	private void fillTable(final CsvReader<String[]> iterator, final String selection) throws Exception {
		while (true) {
			final var txt = iterator.next();
			if (txt == null) {
				break;
			}
			if (selection == null || selection.equals(txt[1])) {
				final var ti = new TableItem(table, SWT.NONE);
				ti.setText(txt);
			}
		}
	}

	/**
	 * Gets the cdata.
	 *
	 * @return the cdata
	 */
	public ConfigData getCData() {
		return cData;
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
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

	ToolBar getToolBar() {
		return (ToolBar) shell.getChildren()[0];
	}

	private String headerArrayToString(final String[] s) {
		final var str = Arrays.toString(s).replace(comma + space, String.valueOf(cData.getDivider()));
		return str.substring(1, str.length() - 1);
	}

	private void hideColumns() {
		final var map = cData.getColumnMap();
		final var uuid = table.getColumn(map.get(csvHeader[0]).intValue());
		final var group = table.getColumn(map.get(csvHeader[1]).intValue());
		uuid.setResizable(false);
		uuid.setWidth(0);
		group.setResizable(false);
		group.setWidth(0);
		hidePasswordColumn();
	}

	/**
	 * Hides the password column.
	 */
	public void hidePasswordColumn() {
		if (cData.isCustomHeader()) {
			return;
		}
		final var map = cData.getColumnMap();
		final var passwordIndex = map.get(csvHeader[5]).intValue();
		final var passwordColumn = table.getColumn(passwordIndex);
		if (!passwordColumn.getResizable()) {
			return;
		}
		passwordColumn.setWidth(0);
		passwordColumn.setResizable(false);
		final var viewMenu = shell.getMenuBar().getItem(3).getMenu();
		viewMenu.getItem(6).setSelection(false);
		viewMenu.getItem(7).setSelection(true);
		table.getColumn(map.get(csvHeader[2]).intValue()).setText(tableHeader[2]);
	}

	/**
	 * Resets the group list.
	 */
	public void resetGroupList() {
		final var list = getList();
		if (!list.isVisible() || list.getSelectionIndex() < 1) {
			return;
		}
		list.setSelection(0);
		setGroupSelection();
	}

	/**
	 * Resizes the columns.
	 */
	public void resizeColumns() {
		final var resize = shell.getMenuBar().getItem(3).getMenu().getItem(4).getSelection();
		cData.setResizeCol(resize);

		table.setRedraw(false);
		for (final var column : table.getColumns()) {
			if (column.getResizable()) {
				if (resize) {
					column.pack();
				} else {
					column.setWidth(cData.getColumnWidth());
				}
			}
		}
		table.setRedraw(true);
	}

	/**
	 * Fills the table with the selected group.
	 */
	public void setGroupSelection() {
		final var index = getList().getSelectionIndex();
		if (index < 0) {
			return;
		}
		final var sensitiveData = cData.getSensitiveData();
		final var sealedData = sensitiveData.getSealedData();
		final var dataKey = sensitiveData.getDataKey();
		byte[] tableData = null;
		if (sealedData != null && dataKey != null) {
			try {
				tableData = ((String) sealedData.getObject(CryptoUtil.getSecretKey(dataKey, keyAES))).getBytes();
			} catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | IOException e) {
				LOG.error("Error occurred", e);
				msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
			}
		}
		fillTable(false, tableData == null ? extractData() : tableData);
		tableData = null;
	}

	private void setText() {
		final var file = cData.getFile();
		var shellTitle = APP_NAME;
		if (isFileReady(file)) {
			final var filePath = getFilePath(file);
			shellTitle = cData.isModified() ? APP_NAME + titleMD + filePath : APP_NAME + titlePH + filePath;
		}
		shell.setText(shellTitle);

		final var menu = shell.getMenuBar();
		final var tool = getToolBar();
		final var lockText = cData.isLocked() ? menuUnlo : menuLock;
		menu.getItem(0).getMenu().getItem(6).setText(lockText);
		tool.getItem(3).setToolTipText(lockText);

		final var readOnly = cData.isReadOnly();
		final var readOnlyText = readOnly ? menuVent : menuEent;
		table.getMenu().getItem(8).setText(readOnlyText);
		menu.getItem(1).getMenu().getItem(1).setText(readOnlyText);
		tool.getItem(6).setToolTipText(readOnly ? entrView : entrEdit);
	}

	private void sortTable(final SelectionEvent e) {
		if (table.getItemCount() < 2) {
			return;
		}
		final var startTime = System.currentTimeMillis();
		final var selectedColumn = (TableColumn) e.widget;
		var dir = table.getSortDirection();
		if (table.getSortColumn() == selectedColumn) {
			dir = (dir == SWT.UP) ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(selectedColumn);
			dir = SWT.UP;
		}
		final var finalDir = dir; // Make dir effectively final
		final var index = Arrays.asList(table.getColumns()).indexOf(selectedColumn);
		final var collator = Collator.getInstance();
		final var items = table.getItems();

		Arrays.sort(items, (item1, item2) -> {
			final var value1 = item1.getText(index);
			final var value2 = item2.getText(index);
			return finalDir == SWT.UP ? collator.compare(value1, value2) : collator.compare(value2, value1);
		});

		table.setRedraw(false);
		final var values = new String[table.getColumnCount()];
		for (var i = 0; i < items.length; i++) {
			final var item = items[i];
			for (var j = 0; j < values.length; j++) {
				values[j] = item.getText(j);
			}
			item.dispose();
			final var newItem = new TableItem(table, SWT.NONE, i);
			newItem.setText(values);
		}
		table.setRedraw(true);

		colorURL();
		table.setSortDirection(dir);
		LOG.info("Time to sort: {} ms", Long.valueOf(System.currentTimeMillis() - startTime));
	}

	/**
	 * Enables menu and toolbar items and sets the menu, shell and toolbar text.
	 */
	public void updateUI() {
		enableItems();
		setText();
	}
}
