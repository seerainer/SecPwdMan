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

import static io.github.secpwdman.util.Util.getSecureRandom;
import static io.github.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.secpwdman.widgets.Widgets.group;
import static io.github.secpwdman.widgets.Widgets.horizontalSeparator;
import static io.github.secpwdman.widgets.Widgets.link;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.shell;
import static io.github.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.crypto.Crypto;

/**
 * The Class ConfigDialog.
 */
public class ConfigDialog {

	/**
	 * Enables the selected key derivation function.
	 *
	 * @param shell the shell
	 */
	private static void enableSelectedKDF(final Shell shell) {
		final var groupKey = (Group) shell.getChildren()[0];
		final var groupArgon = (Group) shell.getChildren()[1];
		final var groupPBKDF = (Group) shell.getChildren()[2];
		final var selection = ((Button) groupKey.getChildren()[0]).getSelection();

		groupArgon.setRedraw(false);
		groupPBKDF.setRedraw(false);

		for (final var item : groupArgon.getChildren())
			item.setEnabled(selection);

		for (final var item : groupPBKDF.getChildren())
			item.setEnabled(!selection);

		groupArgon.setRedraw(true);
		groupPBKDF.setRedraw(true);
	}

	/**
	 * Get the grid layout.
	 *
	 * @param numColumns
	 * @return GridLayout
	 */
	private static GridLayout getLayout(final int numColumns) {
		final var layout = new GridLayout(numColumns, false);
		layout.horizontalSpacing = 50;
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.verticalSpacing = 12;
		return layout;
	}

	/**
	 * Time test for Argon2.
	 *
	 * @param cData the cData
	 * @param shell the shell
	 * @param memo  the memory
	 * @param iter  the iterations
	 * @param para  the parallelism
	 */
	private static void testArgon2(final ConfData cData, final Shell shell, final Spinner memo, final Spinner iter, final Spinner para) {
		final var oldArgo = cData.isArgon2id();
		final var oldMemo = cData.getArgonMemo();
		final var oldIter = cData.getArgonIter();
		final var oldPara = cData.getArgonPara();
		cData.setArgon2id(true);
		cData.setArgonMemo(memo.getSelection());
		cData.setArgonIter(iter.getSelection());
		cData.setArgonPara(para.getSelection());

		timeTest(cData, shell);

		cData.setArgon2id(oldArgo);
		cData.setArgonMemo(oldMemo);
		cData.setArgonIter(oldIter);
		cData.setArgonPara(oldPara);
	}

	/**
	 * Time test for PBKDF2-HMAC-SHA512.
	 *
	 * @param cData the cData
	 * @param shell the shell
	 * @param iter  the iterations
	 */
	private static void testPBKDF2(final ConfData cData, final Shell shell, final Spinner iter) {
		final var oldArgo = cData.isArgon2id();
		final var oldIter = cData.getPBKDFIter();
		cData.setArgon2id(false);
		cData.setPBKDFIter(iter.getSelection());

		timeTest(cData, shell);

		cData.setArgon2id(oldArgo);
		cData.setPBKDFIter(oldIter);
	}

	/**
	 * Time test for encrypt / decrypt.
	 *
	 * @param cData the cData
	 * @param shell the shell
	 */
	private static void timeTest(final ConfData cData, final Shell shell) {
		try {
			final var rand = getSecureRandom();
			final var txt = new byte[1024];
			final var pwd = new byte[64];
			rand.nextBytes(txt);
			rand.nextBytes(pwd);

			final var start = System.currentTimeMillis();
			final var enc = new Crypto(cData).encrypt(txt, pwd);
			final var end = System.currentTimeMillis();
			new Crypto(cData).decrypt(enc, pwd);
			final var t0 = Long.valueOf(end - start);
			final var t1 = Long.valueOf(System.currentTimeMillis() - end);
			final var str = String.format(cData.cfgTestI, t0, t1);
			msg(shell, SWT.ICON_INFORMATION | SWT.OK, cData.titleInf, str);
		} catch (final Exception ex) {
			msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
		}
	}

	private final Action action;

	/**
	 * Instantiates a new config dialog.
	 *
	 * @param action the action
	 */
	public ConfigDialog(final Action action) {
		this.action = action;
		open();
	}

	/**
	 * Open.
	 */
	private void open() {
		final var cData = action.getCData();
		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, getLayout(2), cData.cfgTitle);

		final var groupKey = group(dialog, getLayout(3), cData.cfgKeyDF);
		final var argon2 = newButton(groupKey, SWT.RADIO, widgetSelectedAdapter(e -> enableSelectedKDF(dialog)), cData.argon);
		final var pbkdf2 = newButton(groupKey, SWT.RADIO, widgetSelectedAdapter(e -> enableSelectedKDF(dialog)), cData.pbkdf);
		link(groupKey, cData.owaAddress, cData.getLinkColor(), cData.owaLink);

		final var groupArgon = group(dialog, getLayout(4), cData.cfgArgon);
		final var argonM = spinner(groupArgon, cData.getArgonMemo(), 0, 256, 0, 1, 16);
		final var argonT = spinner(groupArgon, cData.getArgonIter(), 0, 128, 0, 1, 8);
		final var argonP = spinner(groupArgon, cData.getArgonPara(), 1, 64, 0, 1, 4);
		newButton(groupArgon, SWT.PUSH, widgetSelectedAdapter(e -> testArgon2(cData, dialog, argonM, argonT, argonP)), cData.cfgTestB);

		final var groupPBKDF = group(dialog, getLayout(2), cData.cfgPIter);
		final var pbkdfIter = spinner(groupPBKDF, cData.getPBKDFIter(), 210000, 9999999, 0, 1, 10000);
		pbkdfIter.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
		newButton(groupPBKDF, SWT.PUSH, widgetSelectedAdapter(e -> testPBKDF2(cData, dialog, pbkdfIter)), cData.cfgTestB);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgMinPl);
		final var minPwdLength = spinner(dialog, cData.getPasswordMinLength(), 6, 64, 0, 1, 4);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgClPwd);
		final var clearPwd = spinner(dialog, cData.getClearPassword(), 10, 300, 0, 1, 10);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgColWh);
		final var columnWidth = spinner(dialog, cData.getColumnWidth(), 10, 4000, 0, 1, 10);

		emptyLabel(dialog);
		emptyLabel(dialog);

		final var okBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> {
			if (argon2.getSelection())
				cData.setArgon2id(true);
			else
				cData.setArgon2id(false);

			cData.setArgonMemo(argonM.getSelection());
			cData.setArgonIter(argonT.getSelection());
			cData.setArgonPara(argonP.getSelection());
			cData.setPBKDFIter(pbkdfIter.getSelection());
			cData.setClearPassword(clearPwd.getSelection());
			cData.setColumnWidth(columnWidth.getSelection());
			cData.setPasswordMinLength(minPwdLength.getSelection());
			dialog.close();
			action.resizeColumns();
		}), cData.entrOkay);

		final var data = new GridData(SWT.CENTER, SWT.END, false, false, 2, 1);
		data.widthHint = 80;
		okBtn.setLayoutData(data);
		okBtn.setFocus();
		dialog.setDefaultButton(okBtn);

		if (cData.isArgon2id())
			argon2.setSelection(true);
		else
			pbkdf2.setSelection(true);

		enableSelectedKDF(dialog);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, point.y);
		dialog.open();
	}
}
