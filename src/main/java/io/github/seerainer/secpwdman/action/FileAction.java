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

import static io.github.seerainer.secpwdman.crypto.KeyStoreManager.getPasswordFromKeyStore;
import static io.github.seerainer.secpwdman.dialog.DialogFactory.createPasswordDialog;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.msgYesNo;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.widgets.Widgets.fileDialog;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.SensitiveData;
import io.github.seerainer.secpwdman.dialog.SearchDialog;
import io.github.seerainer.secpwdman.io.IO;
import io.github.seerainer.secpwdman.io.IOUtil;
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
		var sensitiveData = cData.getSensitiveData();
		clear(sensitiveData.getDataKey());
		clear(sensitiveData.getKeyStorePassword());
		clear(sensitiveData.getKeyStoreData());
		sensitiveData = new SensitiveData();
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
		cData.setLocked(false);
		cData.setModified(false);
		cData.setReadOnly(false);
		cData.setFile(null);
		clearConfidentialData();

		fillGroupList();
		updateUI();
	}

	private void disposeResources() {
		final var tray = shell.getDisplay().getSystemTray();
		if (tray != null && WIN32) {
			tray.getItem(0).getImage().dispose();
		}
		for (final var item : getToolBar().getItems()) {
			final var image = item.getImage();
			if (image != null) {
				image.dispose();
			}
		}
		getList().getFont().dispose();
		table.getFont().dispose();
		shell.getFont().dispose();
		shell.getImage().dispose();
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
		clearClipboard();
		clearConfidentialData();
		disposeResources();
		return true;
	}

	/**
	 * Opens the export dialog.
	 */
	public void exportDialog() {
		if (!cData.isCustomHeader() && !msgYesNo(cData, shell, warnExpo)) {
			return;
		}
		final var file = fileDialog(shell, SWT.SAVE, imexFile, imexExte);
		if (isBlank(file)) {
			return;
		}
		final var io = new IO(this);
		io.saveFile(null, file);
	}

	private void handleFileError(final String file, final String errorMessage) {
		LOG.warn(fileError, quote, file, quote);
		msg(shell, SWT.ICON_ERROR, titleErr, errorMessage.formatted(file));
		clearData();
	}

	private void handleNonPasswordFile(final String file) {
		final var csv = imexExte.substring(1, 5);
		final var txt = imexExte.substring(8);
		if ((file.endsWith(csv) || file.endsWith(txt)) || msgYesNo(cData, shell, infoImpo)) {
			final var io = new IO(this);
			if (io.openFile(null, file)) {
				fillGroupList();
			}
		}
		cData.setFile(null);
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
			handleFileError(file, errorFil);
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

	/**
	 * Creates a new database (clear data).
	 */
	public void newDatabase() {
		SearchDialog.close();

		if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0) {
			switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, titleWar, warnNewF)) {
			case SWT.YES -> {
				cData.setClearAfterSave(true);
				saveDialog();
			}
			case SWT.NO -> clearData();
			default -> {
				break;
			}
			}
		} else {
			clearData();
		}
	}

	/**
	 * Opens the file open dialog.
	 */
	public void openDialog() {
		SearchDialog.close();

		final var file = fileDialog(shell, SWT.OPEN, passFile, passExte);
		if (IOUtil.isFileReady(file) && isPasswordFileReady(file)) {
			table.setRedraw(false);
			defaultHeader();
			fillGroupList();
			table.setRedraw(true);
			cData.setFile(file);
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
		final var file = cData.getFile();
		if (isBlank(file)) {
			return;
		}
		if (!IOUtil.isReadable(file)) {
			handleFileError(file, errorFil);
			return;
		}
		if (IOUtil.isPasswordFile(file)) {
			cData.setLocked(true);
			createPasswordDialog(this, false);
		} else {
			handleNonPasswordFile(file);
		}
		updateUI();
	}

	/**
	 * Closes the app or clears the data after saving if either is true.
	 */
	public void postSave() {
		if (cData.isExitAfterSave()) {
			shell.close();
		} else if (cData.isClearAfterSave()) {
			clearData();
		}
	}

	/**
	 * Opens the file save dialog.
	 */
	public void saveDialog() {
		SearchDialog.close();

		final var sensitiveData = cData.getSensitiveData();
		final var keyStoreData = sensitiveData.getKeyStoreData();
		final var keyStorePassword = sensitiveData.getKeyStorePassword();
		var file = cData.getFile();
		if (IOUtil.isFileReady(file) && keyStoreData != null && keyStorePassword != null) {
			final var io = new IO(this);
			if (io.saveFile(getPasswordFromKeyStore(keyStorePassword, keyStoreData), file)) {
				cData.setModified(false);
				cData.setReadOnly(true);
				postSave();
			}
		} else {
			file = fileDialog(shell, SWT.SAVE, passFile, passExte);
			if (!isBlank(file)) {
				cData.setFile(file);
				createPasswordDialog(this, true);
			}
		}
		if (shell == null || shell.isDisposed()) {
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
		table.removeAll();
		table.setSortColumn(null);

		SearchDialog.close();
		clearClipboard();
		fillGroupList();
		hidePasswordColumn();
		updateUI();

		final var tray = shell.getDisplay().getSystemTray();
		if (tray == null || !WIN32) {
			return;
		}
		shell.setMinimized(true);
		shell.setVisible(false);
		final var trayItem = tray.getItem(0);
		trayItem.setToolTipText(APP_NAME + titlePH + IOUtil.getFilePath(cData.getFile()));
		trayItem.setVisible(true);
	}
}
