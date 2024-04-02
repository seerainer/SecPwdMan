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

import static io.github.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.secpwdman.widgets.Widgets.horizontalSeparator;
import static io.github.secpwdman.widgets.Widgets.link;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.crypto.Crypto;

/**
 * The Class ConfigDialog.
 */
public class ConfigDialog {

	private static void argon2Test(final ConfData cData, final Shell parent, final Spinner memo, final Spinner iter, final Spinner para) {
		final var oldArgo = cData.isArgon2id();
		final var oldMemo = cData.getArgonMemo();
		final var oldIter = cData.getArgonIter();
		final var oldPara = cData.getArgonPara();
		cData.setArgon2id(true);
		cData.setArgonMemo(memo.getSelection());
		cData.setArgonIter(iter.getSelection());
		cData.setArgonPara(para.getSelection());

		timeTest(cData, parent);

		cData.setArgon2id(oldArgo);
		cData.setArgonMemo(oldMemo);
		cData.setArgonIter(oldIter);
		cData.setArgonPara(oldPara);
	}

	private static void pbkdf2Test(final ConfData cData, final Shell parent, final Spinner iter) {
		final var oldArgo = cData.isArgon2id();
		final var oldIter = cData.getPBKDFIter();
		cData.setArgon2id(false);
		cData.setPBKDFIter(iter.getSelection());

		timeTest(cData, parent);

		cData.setArgon2id(oldArgo);
		cData.setPBKDFIter(oldIter);
	}

	/**
	 * Time test for encrypt / decrypt.
	 *
	 * @param cData the cData
	 * @param shell the parent
	 */
	private static void timeTest(final ConfData cData, final Shell parent) {
		try {
			final var rand = new Random();
			final var txt = new byte[1024];
			final var pwd = new byte[64];
			rand.nextBytes(txt);
			rand.nextBytes(pwd);

			var start = Instant.now();
			final var enc = new Crypto(cData).encrypt(txt, pwd);
			final var t0 = Long.valueOf(ChronoUnit.MILLIS.between(start, Instant.now()));
			start = Instant.now();
			new Crypto(cData).decrypt(enc, pwd);
			final var t1 = Long.valueOf(ChronoUnit.MILLIS.between(start, Instant.now()));
			final var str = String.format(cData.cfgTestI, t0, t1);
			msg(parent, SWT.ICON_INFORMATION | SWT.OK, cData.titleInf, str);
		} catch (final Exception ex) {
			msg(parent, SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
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
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final var layout = new GridLayout(3, false);
		layout.horizontalSpacing = 15;
		layout.marginBottom = 15;
		layout.marginLeft = 15;
		layout.marginRight = 15;
		layout.marginTop = 15;
		layout.verticalSpacing = 12;
		dialog.setLayout(layout);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		var label = newLabel(dialog, SWT.HORIZONTAL, cData.cfgKeyDF);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		final var argon2 = newButton(dialog, SWT.RADIO, null, cData.argon);
		final var pbkdf2 = newButton(dialog, SWT.RADIO, null, cData.pbkdf);
		link(dialog, cData.owaAddress, cData.getLinkColor(), cData.owaLink);

		if (cData.isArgon2id())
			argon2.setSelection(true);
		else
			pbkdf2.setSelection(true);

		horizontalSeparator(dialog);

		label = newLabel(dialog, SWT.HORIZONTAL, cData.cfgArgon);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		final var testB = newButton(dialog, SWT.PUSH, null, cData.cfgTestB);

		final var argonM = spinner(dialog, cData.getArgonMemo(), 0, 256, 0, 1, 16);
		final var argonT = spinner(dialog, cData.getArgonIter(), 0, 128, 0, 1, 8);
		final var argonP = spinner(dialog, cData.getArgonPara(), 1, 64, 0, 1, 4);
		testB.addSelectionListener(widgetSelectedAdapter(e -> argon2Test(cData, dialog, argonM, argonT, argonP)));

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgPIter);
		final var pbkdfIter = spinner(dialog, cData.getPBKDFIter(), 210000, 9999999, 0, 1, 10000);
		newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> pbkdf2Test(cData, dialog, pbkdfIter)), cData.cfgTestB);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgMinPl);
		final var minPwdLength = spinner(dialog, cData.getPasswordMinLength(), 6, 64, 0, 1, 4);
		emptyLabel(dialog);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgClPwd);
		final var clearPwd = spinner(dialog, cData.getClearPasswd(), 10, 300, 0, 1, 10);
		emptyLabel(dialog);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgColWh);
		final var columnWidth = spinner(dialog, cData.getColumnWidth(), 10, 4000, 0, 1, 10);

		emptyLabel(dialog);
		emptyLabel(dialog);
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
			cData.setClearPasswd(clearPwd.getSelection());
			cData.setColumnWidth(columnWidth.getSelection());
			cData.setPasswordMinLength(minPwdLength.getSelection());
			dialog.close();
			action.resizeColumns();
		}), cData.entrOkay);

		final var data = new GridData(SWT.CENTER, SWT.END, false, false, 3, 1);
		data.widthHint = 80;
		okBtn.setLayoutData(data);
		okBtn.setFocus();
		dialog.setDefaultButton(okBtn);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, point.y);
		dialog.setText(cData.cfgTitle);
		dialog.open();
	}
}
