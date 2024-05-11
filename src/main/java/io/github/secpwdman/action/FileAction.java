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

import static io.github.secpwdman.util.SWTUtil.msgShowPasswords;
import static io.github.secpwdman.util.Util.WIN32;
import static io.github.secpwdman.util.Util.getFilePath;
import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.isFileOpen;
import static io.github.secpwdman.util.Util.isReadable;
import static io.github.secpwdman.widgets.Widgets.fileDialog;
import static io.github.secpwdman.widgets.Widgets.msg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

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
		defaultHeader();
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
	 * Unminimize the app, ask to save before exit, dispose resources, clear
	 * clipboard and exit.
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

		final var tray = shell.getDisplay().getSystemTray();

		if (tray != null && WIN32)
			tray.getItem(0).getImage().dispose();

		cData.setData(null);
		getList().getFont().dispose();
		table.getFont().dispose();
		shell.getFont().dispose();
		shell.getImage().dispose();
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
		final var dialog = fileDialog(shell, style, cData.imexFile, cData.imexExte);

		if (style == SWT.OPEN) {
			final var file = dialog.open();

			if (isFileOpen(file) && io.openFile(null, file)) {
				cData.setModified(true);
				enableItems();
				fillGroupList();
				setText();
			}
		} else if (style == SWT.SAVE) {
			if (!cData.isCustomHeader() && !msgShowPasswords(cData, shell))
				return;

			final var file = dialog.open();

			if (!isEmpty(file))
				io.saveFile(null, file);
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
		SearchDialog.close();

		if (!cData.isCustomHeader() && cData.isModified() && table.getItemCount() > 0)
			switch (msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL, cData.titleWar, cData.warnNewF)) {
			case SWT.YES:
				openSave(SWT.SAVE);
				cData.setClearAfterSave(true);
				break;
			case SWT.NO:
				clearData();
				break;
			default:
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
			final var dialog = fileDialog(shell, style, cData.passFile, cData.passExte);
			final var file = dialog.open();

			if (!isEmpty(file)) {
				cData.setFile(file);

				if (style == SWT.OPEN && isReadable(file)) {
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
	 * Lock the app.
	 */
	public void setLocked() {
		if (isFileOpen(cData.getFile()) && !cData.isModified()) {
			cData.setLocked(true);
			table.removeAll();

			SearchDialog.close();

			clearClipboard();
			enableItems();
			fillGroupList();
			hidePasswordColumn();
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
}
