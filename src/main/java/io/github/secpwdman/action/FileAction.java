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
import static io.github.secpwdman.util.Util.getFilePath;
import static io.github.secpwdman.util.Util.isArrayEqual;
import static io.github.secpwdman.util.Util.isEmptyString;
import static io.github.secpwdman.util.Util.isFileOpen;
import static io.github.secpwdman.util.Util.isReadable;
import static io.github.secpwdman.util.Util.msgShowPasswords;
import static io.github.secpwdman.widgets.Widgets.fileDialog;
import static io.github.secpwdman.widgets.Widgets.msg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.dialog.PasswordDialog;
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
	private void clearData() {
		table.setRedraw(false);
		createColumns(cData.defaultHeader);
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
		setText();
	}

	/**
	 * Confirm password.
	 *
	 * @param dialog the dialog
	 */
	public void confirmPassword(final Shell dialog) {
		final var io = new IO(this);
		final var pwd = ((Text) dialog.getChildren()[1]);
		final var pwdStr = pwd.getText();
		pwd.selectAll();

		if (dialog.getBounds().height == 150) {
			final var minPwdLength = cData.getPasswordMinLength();
			final var pwdConfirm = ((Text) dialog.getChildren()[4]);
			pwdConfirm.selectAll();

			if ((!isEmptyString(pwdStr) && pwdStr.equals(pwdConfirm.getText())))
				if (pwd.getText().length() < minPwdLength)
					msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, String.format(cData.errorLen, Integer.valueOf(minPwdLength)));
				else if (io.saveFile(pwdStr, cData.getFile())) {
					cData.setModified(false);
					dialog.close();

					if (cData.isExitAfterSave())
						shell.close();
					else if (cData.isClearAfterSave())
						clearData();
				}
		} else if (!isEmptyString(pwdStr) && io.openFile(pwdStr)) {
			cData.setLocked(false);
			cData.setModified(false);
			cData.setReadOnly(true);
			dialog.close();
		}

		if (!dialog.isDisposed())
			pwd.setFocus();

		if (shell != null && !shell.isDisposed()) {
			enableItems();
			setText();
		}
	}

	/**
	 * Creates the columns.
	 *
	 * @param stdHead true if default header
	 * @param header  the header
	 */
	public void createColumns(final boolean stdHead, final String[] header) {
		for (var i = 0; i < header.length; i++) {
			final var col = new TableColumn(table, SWT.NONE, i);
			col.setText(header[i]);

			if (stdHead && i < 2) {
				col.setResizable(false);
				col.setWidth(0);
			} else
				col.setWidth(cData.getColumnWidth());
		}

		hidePasswordColumn();
	}

	/**
	 * Create new table columns.
	 *
	 * @param header the header
	 */
	public void createColumns(final String[] header) {
		final var stdHead = isArrayEqual(header, cData.defaultHeader);

		if (stdHead) {
			cData.setCustomHeader(false);
			cData.setHeader(cData.tableHeader);
		} else {
			cData.setCustomHeader(true);
			final var strTrim = arrayToString(header).replace(cData.comma + cData.space, cData.comma);
			cData.setHeader(strTrim.substring(1, strTrim.length() - 1));
		}

		table.removeAll();

		while (table.getColumnCount() > 0)
			table.getColumns()[0].dispose();

		createColumns(stdHead, header);
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

		final var toolBar = (ToolBar) shell.getChildren()[0];
		for (final var item : toolBar.getItems()) {
			final var image = item.getImage();

			if (image != null)
				image.dispose();
		}

		table.getFont().dispose();
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

			if (isFileOpen(f)) {
				cData.setFile(f);

				if (io.openFile(null))
					cData.setModified(true);

				enableItems();
				setText();
			}
		} else if (style == SWT.SAVE) {
			if (!cData.isCustomHeader() && !msgShowPasswords(cData, shell))
				return;

			final var f = dialog.open();
			if (!isEmptyString(f))
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

			if (!isEmptyString(f) && f.endsWith(cData.passExte.substring(1))) {
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

			clearClipboard();
			hidePasswordColumn();
			enableItems();
			setText();

			final var tray = shell.getDisplay().getSystemTray();
			if (tray != null && ConfData.WIN32) {
				shell.setMinimized(true);
				shell.setVisible(false);
				final var trayItem = tray.getItem(0);
				trayItem.setToolTipText(ConfData.APP_NAME + cData.titlePH + getFilePath(cData.getFile()));
				trayItem.setVisible(true);
			}
		}
	}
}
