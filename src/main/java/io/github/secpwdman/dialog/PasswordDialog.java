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
package io.github.secpwdman.dialog;

import static io.github.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.secpwdman.util.SWTUtil.setCenter;
import static io.github.secpwdman.util.Util.clear;
import static io.github.secpwdman.util.Util.valueOf;
import static io.github.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import io.github.secpwdman.action.FileAction;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.io.IO;

/**
 * The Class PasswordDialog.
 */
public class PasswordDialog {

	/**
	 * Checks if array is equal.
	 *
	 * @param a the first char[] a
	 * @param b the second char[] b
	 * @return true, if equal
	 */
	public static boolean isEqual(final char[] a, final char[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Tests the password strength.
	 *
	 * @param e     the ModifyEvent e
	 * @param cData the cData
	 * @param text2 the text2
	 */
	private static void testPassword(final ModifyEvent e, final ConfData cData, final Text text2) {
		final var text1 = (Text) e.widget;
		final var label = (Label) text1.getParent().getChildren()[5];
		final var pwd1 = text1.getTextChars();
		final var pwd2 = text2.getTextChars();

		if (isEqual(pwd1, pwd2))
			evalPasswordStrength(cData, label, pwd1);
		else {
			label.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
			label.setText(cData.passNoMa);
			label.setToolTipText(cData.empty);
		}

		clear(pwd1);
		clear(pwd2);
	}

	/**
	 * Convert char[] to byte[].
	 *
	 * @param c the char[]
	 * @return the byte[]
	 */
	private static byte[] toBytes(final char[] c) {
		final var charBuffer = CharBuffer.wrap(c);
		final var byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		final var bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		clear(charBuffer.array());
		clear(byteBuffer.array());
		return bytes;
	}

	private final int saveHeight = 210;

	private final FileAction action;

	/**
	 * Instantiates a new password dialog.
	 *
	 * @param action the action
	 */
	public PasswordDialog(final FileAction action) {
		this.action = action;
	}

	/**
	 * Confirm password.
	 *
	 * @param dialog the dialog
	 */
	private void confirmPassword(final Shell dialog) {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var file = cData.getFile();
		final var io = new IO(action);
		final var pwd = ((Text) dialog.getChildren()[1]);
		final var pwdCharsA = pwd.getTextChars();
		final var length = pwdCharsA.length;
		pwd.selectAll();

		if (dialog.getBounds().height == saveHeight) {
			final var pwdMinLength = cData.getPasswordMinLength();
			final var pwdConfirm = ((Text) dialog.getChildren()[3]);
			final var pwdCharsB = pwdConfirm.getTextChars();
			pwdConfirm.selectAll();

			if ((length > 0 && isEqual(pwdCharsA, pwdCharsB)))
				if (length < pwdMinLength)
					msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, String.format(cData.errorLen, valueOf(pwdMinLength)));
				else if (io.saveFile(toBytes(pwdCharsA), file)) {
					cData.setModified(false);
					dialog.close();

					if (cData.isExitAfterSave())
						shell.close();
					else if (cData.isClearAfterSave())
						action.clearData();
				}

			clear(pwdCharsB);
		} else if (length > 0 && io.openFile(toBytes(pwdCharsA), file)) {
			cData.setLocked(false);
			cData.setModified(false);
			cData.setReadOnly(true);
			dialog.close();
		}

		if (!dialog.isDisposed())
			pwd.setFocus();

		if (shell != null && !shell.isDisposed()) {
			action.enableItems();
			action.fillGroupList();
			action.setText();
			clear(pwdCharsA);
		}
	}

	/**
	 * Open.
	 *
	 * @param save if true save file
	 */
	public void open(final boolean save) {
		SearchDialog.close();

		final var cData = action.getCData();
		final var layout = new GridLayout(3, false);
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;

		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.ON_TOP | SWT.SYSTEM_MODAL, layout, cData.passTitl);

		newLabel(dialog, SWT.HORIZONTAL, cData.passWord);
		final var pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
		pwd.setFocus();
		pwd.setTextLimit(64);

		if (save) {
			newLabel(dialog, SWT.HORIZONTAL, cData.passConf);
			final var pwdConfirm = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
			pwd.addModifyListener(e -> testPassword(e, cData, pwdConfirm));
			pwdConfirm.addModifyListener(e -> testPassword(e, cData, pwd));
			pwdConfirm.setTextLimit(64);

			emptyLabel(dialog);

			final var label = newLabel(dialog, SWT.HORIZONTAL, cData.passShor + cData.getPasswordMinLength());
			label.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			dialog.setSize(480, saveHeight);
		} else
			dialog.setSize(480, 150);

		emptyLabel(dialog);
		emptyLabel(dialog);
		emptyLabel(dialog);

		final var okBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> confirmPassword(dialog)), cData.entrOkay);
		var data = new GridData(SWT.END, SWT.TOP, true, false, 2, 1);
		data.widthHint = 80;
		okBtn.setLayoutData(data);
		dialog.setDefaultButton(okBtn);

		final var clBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrCanc);
		data = new GridData(SWT.LEAD, SWT.TOP, true, false);
		data.widthHint = 80;
		clBtn.setLayoutData(data);

		setCenter(dialog);

		dialog.open();
	}
}
