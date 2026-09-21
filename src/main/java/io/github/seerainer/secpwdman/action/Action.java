/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.ASCII_LENGTH;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.BUFFER_MIN;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.DARK_FORE;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.MAX_TABLE_ENTRIES;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.QUOTE_CHAR;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.TABL_BACK;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_NAME;
import static io.github.seerainer.secpwdman.config.StringConstants.CUSTOM_HEADER;
import static io.github.seerainer.secpwdman.config.StringConstants.DATA_NOT_NULL;
import static io.github.seerainer.secpwdman.config.StringConstants.ERROR;
import static io.github.seerainer.secpwdman.config.StringConstants.MAX_ENTRY;
import static io.github.seerainer.secpwdman.config.StringConstants.TIME_TO_SORT;
import static io.github.seerainer.secpwdman.config.StringConstants.csvHeader;
import static io.github.seerainer.secpwdman.config.StringConstants.entrEdit;
import static io.github.seerainer.secpwdman.config.StringConstants.entrView;
import static io.github.seerainer.secpwdman.config.StringConstants.errorSev;
import static io.github.seerainer.secpwdman.config.StringConstants.lineBrk;
import static io.github.seerainer.secpwdman.config.StringConstants.listFirs;
import static io.github.seerainer.secpwdman.config.StringConstants.menuEent;
import static io.github.seerainer.secpwdman.config.StringConstants.menuLock;
import static io.github.seerainer.secpwdman.config.StringConstants.menuUnlo;
import static io.github.seerainer.secpwdman.config.StringConstants.menuVent;
import static io.github.seerainer.secpwdman.config.StringConstants.newLine;
import static io.github.seerainer.secpwdman.config.StringConstants.nullStr;
import static io.github.seerainer.secpwdman.config.StringConstants.quote;
import static io.github.seerainer.secpwdman.config.StringConstants.space;
import static io.github.seerainer.secpwdman.config.StringConstants.tableHeader;
import static io.github.seerainer.secpwdman.config.StringConstants.titleErr;
import static io.github.seerainer.secpwdman.config.StringConstants.titleMD;
import static io.github.seerainer.secpwdman.config.StringConstants.titlePH;
import static io.github.seerainer.secpwdman.config.StringConstants.warnMaxE;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.cipherAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.keyAES;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_NOTES;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_USER;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_DELETE;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_EDIT;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_NEW;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_OPEN_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_SELECT_ALL;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CHANGE_KEY;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CLOSE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_EXPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_IMPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_LOCK;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_OPEN;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_SAVE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FIND_SEARCH;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_EDIT;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_FILE;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_FIND;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_VIEW;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_GROUPS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_HIDE_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_READ_ONLY;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_RESIZE;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_SHOW_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_TEXT;
import static io.github.seerainer.secpwdman.ui.Widgets.findMenuItem;
import static io.github.seerainer.secpwdman.ui.Widgets.findToolItem;
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
import java.util.Map;

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
import io.github.seerainer.secpwdman.crypto.Crypto;
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
import io.github.seerainer.secpwdman.util.SWTUtil;
import io.github.seerainer.secpwdman.util.SerializationUtils;
import io.github.seerainer.secpwdman.util.Win32Affinity;

/**
 * Abstract class for actions.
 */
public abstract class Action {

    private static final Logger LOG = LogFactory.getLog(Action.class);

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
	final var display = shell.getDisplay();
	if (display == null || display.isDisposed()) {
	    return;
	}
	final var cb = new Clipboard(display);
	try {
	    final var data = new Object[] { nullStr };
	    final var dataTypes = new Transfer[] { TextTransfer.getInstance() };
	    cb.setContents(data, dataTypes, DND.CLIPBOARD);
	    cb.clearContents();
	} finally {
	    cb.dispose();
	}
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

    private static boolean rule(final Map<String, Boolean> states, final String id) {
	final var state = states.get(id);
	if (state == null) {
	    throw new IllegalStateException("No enablement rule for menu item: " + id);
	}
	return state.booleanValue();
    }

    private static void applyStates(final Menu menu, final Map<String, Boolean> states, final String... ids) {
	for (final var id : ids) {
	    findMenuItem(menu, id).setEnabled(rule(states, id));
	}
    }

    private static void applyToolStates(final ToolBar toolBar, final Map<String, Boolean> states, final String... ids) {
	for (final var id : ids) {
	    findToolItem(toolBar, id).setEnabled(rule(states, id));
	}
    }

    /**
     * Disables menu and toolbar items. Items resolve through stable
     * {@code MenuIds}, never positional indexes; the decision matrix lives in
     * {@link MenuStates} and the toolbar mirrors its menu counterpart.
     */
    public void enableItems() {
	final var menuBar = getMenu();
	final var file = findMenuItem(menuBar, MENU_FILE).getMenu();
	final var edit = findMenuItem(menuBar, MENU_EDIT).getMenu();
	final var find = findMenuItem(menuBar, MENU_FIND).getMenu();
	final var view = findMenuItem(menuBar, MENU_VIEW).getMenu();
	final var state = new MenuStates.State(!cData.isCustomHeader(), IOUtil.isFileReady(cData.getFile()),
		isKeyStoreReady(), cData.isModified(), !cData.isLocked(), !cData.isReadOnly(), table.getItemCount(),
		table.getSelectionCount(), isUrl(cData, table), findMenuItem(view, VIEW_HIDE_PASS).getSelection(),
		findMenuItem(view, VIEW_SHOW_PASS).getSelection());
	final var states = MenuStates.enabled(state);

	applyStates(file, states, FILE_OPEN, FILE_SAVE, FILE_CLOSE, FILE_CHANGE_KEY, FILE_LOCK, FILE_IMPORT,
		FILE_EXPORT);
	applyStates(edit, states, EDIT_NEW, EDIT_EDIT, EDIT_SELECT_ALL, EDIT_DELETE, EDIT_COPY_URL, EDIT_COPY_USER,
		EDIT_COPY_PASS, EDIT_COPY_NOTES, EDIT_OPEN_URL);
	applyStates(find, states, FIND_SEARCH);
	applyStates(view, states, VIEW_READ_ONLY, VIEW_GROUPS, VIEW_SHOW_PASS, VIEW_HIDE_PASS, VIEW_TEXT);
	findMenuItem(view, VIEW_READ_ONLY).setSelection(cData.isReadOnly());

	applyToolStates(getToolBar(), states, FILE_OPEN, FILE_SAVE, FILE_LOCK, EDIT_NEW, EDIT_EDIT, FIND_SEARCH,
		EDIT_COPY_URL, EDIT_COPY_USER, EDIT_COPY_PASS, EDIT_COPY_NOTES, EDIT_OPEN_URL);
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
	// OWASP CSV Injection: neutralize cells starting with =,+,-,@,|,%,tab/CR
	// by prefixing a single quote. The quote is part of the exported data so
	// Excel/LibreOffice treat the cell as text instead of a formula.
	final var safe = sanitizeCsvFormula(chars);
	if (!containsSpecialChar(safe)) {
	    try {
		return new String(CharsetUtil.replaceSequence(safe, lineBrk.toCharArray(), space.toCharArray()));
	    } finally {
		if (safe != chars) {
		    clear(safe);
		}
	    }
	}
	final var quoteChar1 = quote.toCharArray();
	final var quoteChar2 = (quote + quote).toCharArray();
	final var specialCha = CharsetUtil.replaceSequence(safe, quoteChar1, quoteChar2);
	if (safe != chars) {
	    clear(safe);
	}
	final var result = new char[specialCha.length + quoteChar2.length];
	System.arraycopy(quoteChar1, 0, result, 0, quoteChar1.length);
	System.arraycopy(specialCha, 0, result, quoteChar1.length, specialCha.length);
	System.arraycopy(quoteChar1, 0, result, quoteChar1.length + specialCha.length, quoteChar1.length);
	return new String(result);
    }

    private static char[] sanitizeCsvFormula(final char[] chars) {
	if (chars == null || chars.length == 0) {
	    return chars;
	}
	var start = 0;
	// Skip leading whitespace/control for trigger detection only; prefix still
	// applies to the whole cell.
	while (start < chars.length && (chars[start] == ' ' || chars[start] == '\t')) {
	    start++;
	}
	if (start >= chars.length) {
	    return chars;
	}
	final var c = chars[start];
	if ((c != '=') && (c != '+') && (c != '-') && (c != '@') && (c != '|') && (c != '%')) {
	    return chars;
	}
	final var prefixed = new char[chars.length + 1];
	prefixed[0] = '\'';
	System.arraycopy(chars, 0, prefixed, 1, chars.length);
	return prefixed;
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
	final var viewMenu = findMenuItem(getMenu(), MENU_VIEW).getMenu();
	findMenuItem(viewMenu, VIEW_SHOW_PASS).setSelection(false);
	findMenuItem(viewMenu, VIEW_HIDE_PASS).setSelection(true);
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
	final var resize = findMenuItem(findMenuItem(getMenu(), MENU_VIEW).getMenu(), VIEW_RESIZE).getSelection();
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
     * Sets the display affinity of the shell.
     *
     * @param shellAffinity the shell
     */
    public void setAffinity(final Shell shellAffinity) {
	if (!SWTUtil.WIN32) {
	    return;
	}
	shell.getDisplay().asyncExec(() -> Win32Affinity.setWindowDisplayAffinity(shellAffinity));
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
	final var fileMenu = findMenuItem(menu, MENU_FILE).getMenu();
	final var editMenu = findMenuItem(menu, MENU_EDIT).getMenu();
	final var lockText = cData.isLocked() ? menuUnlo : menuLock;
	findMenuItem(fileMenu, FILE_LOCK).setText(lockText);
	findToolItem(tool, FILE_LOCK).setToolTipText(lockText);

	final var readOnly = cData.isReadOnly();
	final var readOnlyText = readOnly ? menuVent : menuEent;
	findMenuItem(table.getMenu(), EDIT_EDIT).setText(readOnlyText);
	findMenuItem(editMenu, EDIT_EDIT).setText(readOnlyText);
	findToolItem(tool, EDIT_EDIT).setToolTipText(readOnly ? entrView : entrEdit);
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
