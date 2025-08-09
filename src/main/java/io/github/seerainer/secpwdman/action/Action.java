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

import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.getColor;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgYesNo;
import static io.github.seerainer.secpwdman.util.URLUtil.isUrl;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.getBase64Decode;
import static io.github.seerainer.secpwdman.util.Util.getBase64Encode;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.crypto.KeyStoreManager;
import io.github.seerainer.secpwdman.csv.CSVConfiguration;
import io.github.seerainer.secpwdman.csv.CSVParseException;
import io.github.seerainer.secpwdman.csv.CSVParser;
import io.github.seerainer.secpwdman.csv.CSVParsingOptions;
import io.github.seerainer.secpwdman.csv.CSVRecord;
import io.github.seerainer.secpwdman.io.ByteContainer;
import io.github.seerainer.secpwdman.io.IOUtil;
import io.github.seerainer.secpwdman.util.CharsetUtil;
import io.github.seerainer.secpwdman.util.LogFactory;
import io.github.seerainer.secpwdman.util.SerializationUtils;

/**
 * Abstract class for actions.
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
	final var data = new Object[] { nullStr };
	final var dataTypes = new Transfer[] { TextTransfer.getInstance() };
	cb.setContents(data, dataTypes, DND.CLIPBOARD);
	cb.clearContents();
	cb.dispose();
    }

    /**
     * Colors the table.
     */
    public void colorTable() {
	if (cData.isCustomHeader()) {
	    return;
	}
	final var index = cData.getColumnMap().get(csvHeader[3]).intValue();
	final var color = DARK ? TABL_BACK : DARK_FORE;
	final var bgColor = getColor(color, color, color);
	var count = 0;
	for (final var item : table.getItems()) {
	    item.setBackground(count++ % 2 == 0 ? bgColor : null);
	    item.setForeground(index, isUrl(item.getText(index)) ? cData.getLinkColor() : cData.getTextColor());
	}
    }

    private boolean containsSpecialChar(final char[] chars) {
	for (final char c : chars) {
	    if (c > ASCII_LENGTH || c == QUOTE_CHAR || Character.isWhitespace(c) || c == cData.getDivider()) {
		return true;
	    }
	}
	return false;
    }

    private String convertHeaderArrayToString(final String[] s) {
	return String.join(String.valueOf(cData.getDivider()), s);
    }

    private void createColumns(final String[] header) {
	resetTable();
	while (table.getColumnCount() > 0) {
	    table.getColumns()[0].dispose();
	}
	Arrays.stream(header).forEach(head -> {
	    final var col = new TableColumn(table, SWT.NONE);
	    col.addSelectionListener(widgetSelectedAdapter(this::sortTable));
	    col.setMoveable(true);
	    col.setText(head);
	    col.setWidth(cData.getColumnWidth());
	});
    }

    private void customHeader(final String[] header) {
	final var newHeader = Arrays.copyOf(header, header.length);
	final HashMap<String, Integer> map = HashMap.newHashMap(header.length);
	for (var i = 0; i < header.length; i++) {
	    for (var j = 0; j < csvHeader.length; j++) {
		if (header[i].equalsIgnoreCase(csvHeader[j])) {
		    map.put(csvHeader[j], Integer.valueOf(i));
		    newHeader[i] = tableHeader[j];
		    break;
		}
	    }
	}
	cData.setColumnMap(map);
	cData.setHeader(convertHeaderArrayToString(header));

	if (Arrays.stream(csvHeader).anyMatch(key -> !map.containsKey(key))) {
	    cData.setCustomHeader(true);
	    createColumns(header);
	    LOG.warn(CUSTOM_HEADER);
	    return;
	}
	cData.setCustomHeader(false);
	createColumns(newHeader);
	hideColumns();
    }

    /**
     * Decrypts the password.
     *
     * @param data the encrypted password
     * @return the decrypted password
     */
    public char[] decryptPassword(final char[] data) {
	if (!isKeyStoreReady()) {
	    return data;
	}
	final var password = CharsetUtil.toChars(getPassword());
	final var bytes = CharsetUtil.toBytes(data);
	final var keyData = getBase64Decode(bytes);
	if (isNull(keyData)) {
	    clear(password);
	    return CharsetUtil.toChars(bytes);
	}
	final var dec = KeyStoreManager.getPasswordFromKeyStore(password, keyData);
	if (isNull(dec)) {
	    clear(password);
	    return CharsetUtil.toChars(bytes);
	}
	final var decChar = CharsetUtil.toChars(dec);
	clear(password);
	clear(bytes);
	return decChar;
    }

    /**
     * Creates the default header.
     */
    public void defaultHeader() {
	final HashMap<String, Integer> map = HashMap.newHashMap(csvHeader.length);
	for (var i = 0; i < csvHeader.length; i++) {
	    map.put(csvHeader[i], Integer.valueOf(i));
	}
	cData.setColumnMap(map);
	cData.setCustomHeader(false);
	cData.setHeader(convertHeaderArrayToString(csvHeader));
	createColumns(tableHeader);
	hideColumns();
    }

    /**
     * Disables menu and toolbar items.
     */
    public void enableItems() {
	final var menu = getMenu();
	final var file = menu.getItem(0).getMenu();
	final var edit = menu.getItem(1).getMenu();
	final var find = menu.getItem(2).getMenu();
	final var view = menu.getItem(3).getMenu();
	final var isDefaultHeader = !cData.isCustomHeader();
	final var isFileOpen = IOUtil.isFileReady(cData.getFile());
	final var isKeyReady = isKeyStoreReady();
	final var isModified = cData.isModified();
	final var isUnlocked = !cData.isLocked();
	final var isWriteable = !cData.isReadOnly();
	final var itemCount = table.getItemCount();
	final var selectionCount = table.getSelectionCount();

	file.getItem(1).setEnabled(!isFileOpen);
	file.getItem(2).setEnabled(itemCount > 0 && isWriteable && isDefaultHeader);
	file.getItem(3).setEnabled(isFileOpen);
	file.getItem(5).setEnabled(isKeyReady && !isModified && isUnlocked && isWriteable);
	file.getItem(7).setEnabled(isFileOpen && !isModified && isDefaultHeader);
	file.getItem(9).setEnabled(itemCount == 0 && isUnlocked && isWriteable);
	file.getItem(10).setEnabled(itemCount > 0);

	edit.getItem(0).setEnabled(isKeyReady && isUnlocked && isWriteable && isDefaultHeader);
	edit.getItem(1).setEnabled(selectionCount == 1 && isDefaultHeader && isKeyReady);
	edit.getItem(3).setEnabled(itemCount > 0);
	edit.getItem(4).setEnabled(selectionCount > 0 && isWriteable);
	edit.getItem(6).setEnabled(selectionCount == 1 && isDefaultHeader);
	edit.getItem(7).setEnabled(selectionCount == 1 && isDefaultHeader);
	edit.getItem(8).setEnabled(selectionCount == 1 && isDefaultHeader);
	edit.getItem(9).setEnabled(selectionCount == 1 && isDefaultHeader);
	edit.getItem(11).setEnabled(selectionCount == 1 && isDefaultHeader && isUrl(cData, table));

	find.getItem(0).setEnabled(itemCount > 1);

	view.getItem(0).setEnabled(itemCount > 0 && isFileOpen && isUnlocked && !isModified && isDefaultHeader);
	view.getItem(0).setSelection(cData.isReadOnly());
	view.getItem(2).setEnabled(isDefaultHeader);
	view.getItem(6).setEnabled(view.getItem(7).getSelection() && isFileOpen && isDefaultHeader);
	view.getItem(7).setEnabled(view.getItem(6).getSelection() && isFileOpen && isDefaultHeader);
	view.getItem(11).setEnabled(isUnlocked);

	final var toolBar = getToolBar();
	toolBar.getItem(0).setEnabled(file.getItem(1).getEnabled());
	toolBar.getItem(1).setEnabled(file.getItem(2).getEnabled());
	toolBar.getItem(3).setEnabled(file.getItem(7).getEnabled());
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
     * Encrypts the password.
     *
     * @param password the password
     * @return the byte array
     */
    public char[] encryptPassword(final char[] password) {
	if (!isKeyStoreReady()) {
	    return password;
	}
	final var key = CharsetUtil.toChars(getPassword());
	final var bytes = CharsetUtil.toBytes(password);
	final var kst = KeyStoreManager.putPasswordInKeyStore(key, bytes);
	clear(key);
	clear(bytes);
	return CharsetUtil.toChars(getBase64Encode(kst));
    }

    private String escapeSpecialChar(final char[] chars) {
	if (!containsSpecialChar(chars)) {
	    return new String(CharsetUtil.replaceSequence(chars, lineBrk.toCharArray(), space.toCharArray()));
	}
	final var quoteChar1 = quote.toCharArray();
	final var quoteChar2 = (quote + quote).toCharArray();
	final var specialCha = CharsetUtil.replaceSequence(chars, quoteChar1, quoteChar2);
	final var result = new char[specialCha.length + quoteChar2.length];
	System.arraycopy(quoteChar1, 0, result, 0, quoteChar1.length);
	System.arraycopy(specialCha, 0, result, quoteChar1.length, specialCha.length);
	System.arraycopy(quoteChar1, 0, result, quoteChar1.length + specialCha.length, quoteChar1.length);
	return new String(result);
    }

    /**
     * Extracts all data from the table.
     *
     * @param decrypt true if the data should be decrypted
     * @return the byte array
     */
    public byte[] extractData(final boolean decrypt) {
	final var csvDivider = String.valueOf(cData.getDivider());
	final var isImport = cData.isImport();
	final var pwdIndex = cData.isCustomHeader() ? -1 : cData.getColumnMap().get(csvHeader[5]).intValue();
	final var sb = new StringBuilder(table.getItemCount() * BUFFER_MIN); // preallocate size
	sb.append(cData.getHeader()).append(newLine);

	for (final var item : table.getItems()) {
	    final var itemText = new String[table.getColumnCount()];
	    for (var i = 0; i < itemText.length; i++) {
		var text = item.getText(i).toCharArray();
		if (decrypt && i == pwdIndex) {
		    text = decryptPassword(text);
		} else if (!decrypt && isImport && i == pwdIndex) {
		    text = encryptPassword(text);
		}
		itemText[i] = escapeSpecialChar(text);
		clear(text);
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
	return CharsetUtil.toBytes(sb);
    }

    /**
     * Fills the group list.
     */
    public void fillGroupList() {
	final var list = getList();
	if (!list.isVisible() || cData.isCustomHeader()) {
	    return;
	}
	final HashSet<String> set = HashSet.newHashSet(table.getItemCount());
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
     * @param withHeader true if filled with header
     * @param tableData  the data
     */
    public void fillTable(final boolean withHeader, final byte[] tableData) {
	final var bufferLength = cData.getBufferLength();
	final var devider = cData.getDivider();
	final var config = CSVConfiguration.builder().initialBufferSize(bufferLength).delimiter(devider).build();
	final var options = CSVParsingOptions.builder().build();
	final var parser = new CSVParser(config, options);
	java.util.List<CSVRecord> record = null;

	table.setRedraw(false);
	resetTable();

	try {
	    record = parser.parseByteArray(tableData.clone());
	    fillTable(withHeader, record.iterator());
	    if (withHeader) {
		storeTableData(tableData);
	    }
	} catch (final CSVParseException e) {
	    LOG.error(ERROR, e);
	    msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
	} finally {
	    clear(tableData);
	    if (nonNull(record)) {
		record.clear();
	    }
	}

	colorTable();
	table.setRedraw(true);
	resizeColumns();
	table.redraw();
    }

    private void fillTable(final boolean withHeader, final Iterator<CSVRecord> iterator) {
	if (!iterator.hasNext()) {
	    return;
	}
	final var header = iterator.next().getFields();
	if (withHeader) {
	    if (isEqual(header, csvHeader)) {
		defaultHeader();
	    } else {
		customHeader(header);
	    }
	    fillTable(iterator, null);
	} else {
	    final var list = getList();
	    final var listSelection = list.getItem(list.getSelectionIndex());
	    fillTable(iterator, listSelection.equals(listFirs) ? null : listSelection);
	}
    }

    private void fillTable(final Iterator<CSVRecord> iterator, final String selection) {
	var count = 0;
	final var groupIndex = cData.isCustomHeader() ? -1 : cData.getColumnMap().get(csvHeader[1]).intValue();
	while (iterator.hasNext()) {
	    final var txt = iterator.next().getFields();
	    if (isNull(selection) || selection.equals(txt[groupIndex])) {
		if (count++ == MAX_TABLE_ENTRIES && !msgYesNo(cData, shell, warnMaxE)) {
		    LOG.warn(MAX_ENTRY);
		    break;
		}
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
	final var sf = (SashForm) shell.getChildren()[1];
	return (List) sf.getChildren()[0];
    }

    /**
     * Gets the menu
     *
     * @return the menu
     */
    public Menu getMenu() {
	return shell.getMenuBar();
    }

    byte[] getPassword() {
	final var sensitiveData = cData.getSensitiveData();
	final var keyStoreData = sensitiveData.getKeyStoreData();
	final var keyStorePassword = sensitiveData.getKeyStorePassword();
	return KeyStoreManager.getPasswordFromKeyStore(keyStorePassword, keyStoreData);
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
     * Gets the toolbar.
     *
     * @return the toolbar
     */
    public ToolBar getToolBar() {
	return (ToolBar) shell.getChildren()[0];
    }

    private void hideColumn(final int columnIndex) {
	final var column = table.getColumn(columnIndex);
	column.setResizable(false);
	column.setWidth(0);
    }

    private void hideColumns() {
	final var map = cData.getColumnMap();
	hideColumn(map.get(csvHeader[0]).intValue());
	hideColumn(map.get(csvHeader[1]).intValue());
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
	if (passwordColumn.equals(table.getSortColumn())) {
	    table.setSortColumn(null);
	}
	passwordColumn.setWidth(0);
	passwordColumn.setResizable(false);
	final var viewMenu = getMenu().getItem(3).getMenu();
	viewMenu.getItem(6).setSelection(false);
	viewMenu.getItem(7).setSelection(true);
	final var title = map.get(csvHeader[2]).intValue();
	table.getColumn(title).setText(tableHeader[2]);
    }

    /**
     * Tests if key store is ready.
     *
     * @return true if the key store is ready
     */
    public boolean isKeyStoreReady() {
	final var file = cData.getFile();
	final var sensitiveData = cData.getSensitiveData();
	final var keyStoreData = sensitiveData.getKeyStoreData();
	final var keyStorePassword = sensitiveData.getKeyStorePassword();
	if (!IOUtil.isFileReady(file) || isNull(keyStoreData) || isNull(keyStorePassword)) {
	    return false;
	}
	return true;
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

    void resetTable() {
	table.removeAll();
	table.setSortColumn(null);
    }

    /**
     * Resizes the columns.
     */
    public void resizeColumns() {
	final var resize = getMenu().getItem(3).getMenu().getItem(4).getSelection();
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
	byte[] bytes = null;
	if (nonNull(sealedData) && nonNull(dataKey)) {
	    try {
		final var obj = SerializationUtils.deserialize(sealedData.clone());
		if (!(obj instanceof SealedObject)) {
		    throw new ClassCastException(obj.getClass().getName());
		}
		final var so = SealedObject.class.cast(obj);
		final var key = Crypto.getSecretKey(dataKey, keyAES);
		bytes = ((ByteContainer) so.getObject(key)).getData();
	    } catch (ClassCastException | ClassNotFoundException | InvalidKeyException | IOException
		    | NoSuchAlgorithmException e) {
		LOG.error(ERROR, e);
		msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
	    }
	}
	fillTable(false, isNull(bytes) ? extractData(false) : bytes);
	clear(bytes);
    }

    private void setText() {
	final var file = cData.getFile();
	final var sb = new StringBuilder(APP_NAME);
	if (IOUtil.isFileReady(file)) {
	    final var filePath = IOUtil.getFilePath(file);
	    if (cData.isModified()) {
		sb.append(titleMD);
	    } else {
		sb.append(titlePH);
	    }
	    sb.append(filePath);
	}
	shell.setText(sb.toString());

	final var menu = getMenu();
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
	final var finalDir = dir;
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

	colorTable();
	table.setSortDirection(dir);
	LOG.info(TIME_TO_SORT, Long.valueOf(System.currentTimeMillis() - startTime));
    }

    /**
     * Stores the table data in a sealed object.
     *
     * @param data the table data to store
     */
    public void storeTableData(final byte[] data) {
	if (isNull(data)) {
	    LOG.error(DATA_NOT_NULL);
	    return;
	}
	final var sensitiveData = cData.getSensitiveData();
	var key = sensitiveData.getDataKey();
	try {
	    if (isNull(key)) {
		key = Crypto.generateSecretKey(keyAES).getEncoded();
		sensitiveData.setDataKey(key);
	    }
	    final var so = Crypto.generateSealedObject(data, key, cipherAES, keyAES);
	    cData.getSensitiveData().setSealedData(SerializationUtils.serialize(so));
	} catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException
		| IOException | ClassNotFoundException e) {
	    LOG.error(ERROR, e);
	    msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
	} finally {
	    clear(data);
	}
    }

    /**
     * Enables menu and toolbar items and sets the menu, shell and toolbar text.
     */
    public void updateUI() {
	enableItems();
	setText();
    }
}
