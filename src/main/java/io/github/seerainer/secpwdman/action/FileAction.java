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

import static io.github.seerainer.secpwdman.ui.DialogFactory.closeAllDialogs;
import static io.github.seerainer.secpwdman.ui.DialogFactory.closeSearchDialog;
import static io.github.seerainer.secpwdman.ui.DialogFactory.createPasswordDialog;
import static io.github.seerainer.secpwdman.ui.Widgets.fileDialog;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgYesNo;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isBlank;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.io.IO;
import io.github.seerainer.secpwdman.io.IOUtil;
import io.github.seerainer.secpwdman.util.AutoLockManager;
import io.github.seerainer.secpwdman.util.FileShredder;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class FileAction.
 */
public class FileAction extends Action {

    private static final Logger LOG = LogFactory.getLog();

    /**
     * Instantiates a new file action.
     *
     * @param cData the cdata
     * @param shell the shell
     * @param table the table
     */
    public FileAction(final ConfigData cData, final Shell shell, final Table table) {
	super(cData, shell, table);
    }

    private void clearConfidentialData() {
	final var sensitiveData = cData.getSensitiveData();
	clear(sensitiveData.getDataKey());
	clear(sensitiveData.getKeyStorePassword());
	clear(sensitiveData.getKeyStoreData());
	sensitiveData.setDataKey(null);
	sensitiveData.setKeyStorePassword(null);
	sensitiveData.setKeyStoreData(null);
	sensitiveData.setSealedData(null);
    }

    /**
     * Clears the data.
     */
    public void clearData() {
	table.setRedraw(false);
	defaultHeader();
	table.setRedraw(true);

	cData.setClearAfterSave(false);
	cData.setCustomHeader(false);
	cData.setExitAfterSave(false);
	cData.setImport(false);
	cData.setLocked(false);
	cData.setModified(false);
	cData.setReadOnly(false);
	cData.setFile(null);
	cData.setTempFile(null);
	clearConfidentialData();
	stopAutoLockManager();
	System.gc();

	fillGroupList();
	updateUI();
    }

    /**
     * Closes the database, prompts to save if modified, and clears data if
     * necessary.
     */
    public void closeDatabase() {
	closeSearchDialog();

	if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0) {
	    modifiedFile();
	}
	if (cData.isCustomHeader() || !cData.isModified()) {
	    clearData();
	}
    }

    private void disposeResources() {
	final var tray = shell.getDisplay().getSystemTray();
	if (Objects.nonNull(tray) && WIN32) {
	    tray.getItem(0).getImage().dispose();
	}
	for (final var item : getToolBar().getItems()) {
	    final var image = item.getImage();
	    if (Objects.nonNull(image)) {
		image.dispose();
	    }
	}
	getList().getFont().dispose();
	table.getFont().dispose();
	shell.getFont().dispose();
	shell.getImage().dispose();
	System.gc();
    }

    /**
     * Unminimizes the app, requests to save before exiting, saves the
     * configuration, clears the clipboard, discards resources, and exits.
     *
     * @return true, if successful
     */
    public boolean exit() {
	if (shell.getMinimized()) {
	    shell.setMinimized(false);
	}
	if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0) {
	    switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, titleWar, warnExit)) {
	    case SWT.YES -> {
		cData.setExitAfterSave(true);
		saveDialog();
		return false;
	    }
	    case SWT.NO -> {
		break;
	    }
	    default -> {
		return false;
	    }
	    }
	}
	IOUtil.saveConfig(this);
	stopAutoLockManager();
	clearClipboard();
	clearConfidentialData();
	disposeResources();
	return true;
    }

    /**
     * Opens the export dialog.
     */
    public void exportDialog() {
	final var file = fileDialog(shell, SWT.SAVE, imexFile, imexExte);
	if (isBlank(file)) {
	    return;
	}
	resetGroupList();
	final var io = new IO(this);
	io.saveFile(null, file);
    }

    private void handleFileError(final String file, final String errorMessage) {
	LOG.warn(FILE_ERR, quote, file, quote);
	msg(shell, SWT.ICON_ERROR, titleErr, errorMessage.formatted(file));
	clearData();
    }

    private void handleNonPasswordFile(final String file) {
	final var csv = imexExte.substring(1, 5);
	final var txt = imexExte.substring(8);
	if (file.endsWith(csv) || file.endsWith(txt) || msgYesNo(cData, shell, infoImpo)) {
	    final var io = new IO(this);
	    if (io.openFile(null, file)) {
		fillGroupList();
		cData.setImport(true);
	    }
	}
	cData.setTempFile(null);
    }

    /**
     * Opens the import dialog.
     */
    public void importDialog() {
	final var file = fileDialog(shell, SWT.OPEN, imexFile, imexExte);
	if (!IOUtil.isFileReady(file)) {
	    return;
	}
	final var io = new IO(this);
	if (!io.openFile(null, file)) {
	    return;
	}
	cData.setImport(true);
	fillGroupList();
	updateUI();
    }

    /**
     * Displays an error if it is not a password file.
     *
     * @param file the file
     * @return true if it is a password file
     */
    public boolean isPasswordFileReady(final String file) {
	if (!IOUtil.isFileReady(file)) {
	    handleFileError(file, errorInp);
	    return false;
	}
	if (IOUtil.isPasswordFile(file)) {
	    return true;
	}
	handleFileError(file, errorImp);
	return false;
    }

    /**
     * Lock switch.
     */
    public void lockSwitch() {
	if (cData.isLocked()) {
	    if (isPasswordFileReady(cData.getFile())) {
		createPasswordDialog(this, false);
	    }
	} else {
	    setLocked();
	}
    }

    private void modifiedFile() {
	switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, titleWar, warnNewF)) {
	case SWT.YES -> {
	    cData.setClearAfterSave(true);
	    saveDialog();
	}
	case SWT.NO -> clearData();
	default -> {
	    if (!cData.isCustomHeader()) {
		return;
	    }
	    clearData();
	}
	}
    }

    /**
     * Creates a new database file.
     */
    public void newDatabase() {
	closeSearchDialog();

	if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0) {
	    modifiedFile();
	} else {
	    clearData();
	    if (msg(shell, SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL, titleInf, infoNewF) == SWT.OK) {
		saveDialog();
	    }
	}
    }

    /**
     * Opens the file open dialog.
     */
    public void openDialog() {
	closeSearchDialog();

	final var file = fileDialog(shell, SWT.OPEN, passFile, passExte);
	if (IOUtil.isFileReady(file) && isPasswordFileReady(file)) {
	    table.setRedraw(false);
	    defaultHeader();
	    fillGroupList();
	    table.setRedraw(true);
	    cData.setFile(file);
	    cData.setImport(false);
	    cData.setLocked(true);
	    cData.setModified(false);

	    createPasswordDialog(this, false);
	}
	updateUI();
    }

    /**
     * Opens the file argument.
     */
    public void openFileArg() {
	final var file = cData.getTempFile();
	if (isBlank(file)) {
	    return;
	}
	if (!IOUtil.isReadable(file)) {
	    handleFileError(file, errorInp);
	    return;
	}
	if (IOUtil.isPasswordFile(file)) {
	    cData.setFile(file);
	    cData.setImport(false);
	    cData.setLocked(true);
	    createPasswordDialog(this, false);
	} else {
	    handleNonPasswordFile(file);
	}
	updateUI();
    }

    /**
     * Closes the app or clears the data after saving if either is true. If the file
     * is an import, it will be reopend.
     */
    public void postSave() {
	cData.setReadOnly(table.getItemCount() > 0);
	if (cData.isExitAfterSave()) {
	    shell.close();
	    return;
	}
	if (cData.isClearAfterSave()) {
	    clearData();
	    return;
	}
	if (!cData.isImport()) {
	    return;
	}
	cData.setImport(false);
	final var file = cData.getFile();
	if (!isPasswordFileReady(file) || !isKeyStoreReady()) {
	    return;
	}
	final var io = new IO(this);
	if (io.openFile(getPassword(), file)) {
	    fillGroupList();
	}
    }

    /**
     * Opens the file save dialog.
     */
    public void saveDialog() {
	closeSearchDialog();
	resetGroupList();

	if (isKeyStoreReady()) {
	    final var io = new IO(this);
	    final var file = cData.getFile();
	    if (io.saveFile(getPassword(), file)) {
		cData.setModified(false);
		postSave();
	    }
	} else {
	    final var file = fileDialog(shell, SWT.SAVE, passFile, passExte);
	    if (!isBlank(file)) {
		cData.setFile(file);
		cData.setLocked(true);
		createPasswordDialog(this, true);
	    }
	}
	if (Objects.isNull(shell) || shell.isDisposed()) {
	    return;
	}
	updateUI();
    }

    /**
     * Locks the app.
     */
    public void setLocked() {
	if (!IOUtil.isFileReady(cData.getFile()) || cData.isModified()) {
	    return;
	}
	cData.setLocked(true);
	clearConfidentialData();
	resetTable();

	closeAllDialogs();
	clearClipboard();
	fillGroupList();
	hidePasswordColumn();
	updateUI();
	stopAutoLockManager();
	System.gc();

	final var tray = shell.getDisplay().getSystemTray();
	if (Objects.isNull(tray) || !WIN32) {
	    return;
	}
	shell.setMinimized(true);
	shell.setVisible(false);
	final var trayItem = tray.getItem(0);
	final var sb = new StringBuilder();
	sb.append(APP_NAME).append(titlePH).append(IOUtil.getFilePath(cData.getFile()));
	trayItem.setToolTipText(sb.toString());
	trayItem.setVisible(true);
    }

    /**
     * Shreds the file.
     */
    public void shredFile() {
	final var file = fileDialog(shell, SWT.OPEN, allFiles, allFExte);
	if (!IOUtil.isFileReady(file) || !msgYesNo(cData, shell, warnShre.formatted(file))) {
	    return;
	}
	shell.getDisplay().asyncExec(() -> FileShredder.shredFile(this, shell, file));
    }

    private void stopAutoLockManager() {
	AutoLockManager.getInstance(shell.getDisplay(), this).stop();
    }
}
