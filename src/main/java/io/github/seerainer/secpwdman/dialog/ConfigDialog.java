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
package io.github.seerainer.secpwdman.dialog;

import static io.github.seerainer.secpwdman.crypto.Crypto.getRandomValue;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
import static io.github.seerainer.secpwdman.widgets.Widgets.cTabItem;
import static io.github.seerainer.secpwdman.widgets.Widgets.combo;
import static io.github.seerainer.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.group;
import static io.github.seerainer.secpwdman.widgets.Widgets.horizontalSeparator;
import static io.github.seerainer.secpwdman.widgets.Widgets.label;
import static io.github.seerainer.secpwdman.widgets.Widgets.link;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.spinner;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;

import com.password4j.types.Argon2;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.crypto.CryptoFactory;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The record ConfigDialog.
 */
record ConfigDialog(Action action) implements CryptoConstants, Icons, PrimitiveConstants, StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	private static GridLayout gridLayout(final int numColumns) {
		return getLayout(numColumns, 12, 12, 10, 10, 10, 10);
	}

	private static void switchKDF(final Group group) {
		final var groupKey = (Group) group.getChildren()[2];
		final var groupArgon2 = (Group) group.getChildren()[3];
		final var groupPBKDF2 = (Group) group.getChildren()[4];
		final var selection = ((Button) groupKey.getChildren()[0]).getSelection();
		groupArgon2.setRedraw(false);
		groupPBKDF2.setRedraw(false);
		for (final var item : groupArgon2.getChildren()) {
			item.setEnabled(selection);
		}
		for (final var item : groupPBKDF2.getChildren()) {
			item.setEnabled(!selection);
		}
		groupArgon2.setRedraw(true);
		groupPBKDF2.setRedraw(true);
	}

	private static void testArgon2(final ConfigData cData, final Shell shell, final Spinner memo, final Spinner iter,
			final Spinner para) {
		final var oldArgo = cData.isArgon2();
		final var oldType = cData.getArgon2Type();
		final var oldMemo = cData.getArgon2Memo();
		final var oldIter = cData.getArgon2Iter();
		final var oldPara = cData.getArgon2Para();
		cData.setArgon2(true);
		cData.setArgon2Memo(memo.getSelection());
		cData.setArgon2Iter(iter.getSelection());
		cData.setArgon2Para(para.getSelection());
		timeTest(cData, shell);
		cData.setArgon2(oldArgo);
		cData.setArgon2Type(oldType);
		cData.setArgon2Memo(oldMemo);
		cData.setArgon2Iter(oldIter);
		cData.setArgon2Para(oldPara);
	}

	private static void testPBKDF2(final ConfigData cData, final Shell shell, final Spinner iter) {
		final var oldArgo = cData.isArgon2();
		final var oldIter = cData.getPBKDF2Iter();
		cData.setArgon2(false);
		cData.setPBKDF2Iter(iter.getSelection());
		timeTest(cData, shell);
		cData.setArgon2(oldArgo);
		cData.setPBKDF2Iter(oldIter);
	}

	private static void timeTest(final ConfigData cData, final Shell shell) {
		final var oldKeyA = cData.getKeyALGO();
		final var oldCiph = cData.getCipherALGO();
		final var oldArgT = cData.getArgon2Type();
		final var group = ((Group) ((CTabFolder) shell.getChildren()[0]).getChildren()[1]);
		final var comboC = (Combo) ((Group) group.getChildren()[0]).getChildren()[0];
		final var comboA = (Combo) ((Group) group.getChildren()[3]).getChildren()[0];
		final var select = comboC.getSelectionIndex() == 0;
		cData.setKeyALGO(select ? keyAES : keyChaCha20);
		cData.setCipherALGO(select ? cipherAES : cipherChaCha20);
		cData.setArgon2Type(comboA.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
		final var crypt = CryptoFactory.crypto(cData);
		final var txt = getRandomValue(LOG_FILE_SIZE);
		final var pwd1 = getRandomValue(OUT_LENGTH);
		final var pwd2 = pwd1.clone();
		try {
			final var start = System.currentTimeMillis();
			final var enc = crypt.encrypt(txt, pwd1);
			final var end = System.currentTimeMillis();
			crypt.decrypt(enc, pwd2);
			final var t0 = Long.valueOf(end - start);
			final var t1 = Long.valueOf(System.currentTimeMillis() - end);
			LOG.info(timeCrypto, t0, t1);
			msg(shell, SWT.ICON_INFORMATION | SWT.OK, titleInf, cfgTestI.formatted(t0, t1));
		} catch (final Exception e) {
			LOG.error(error, e);
			msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
		}
		cData.setKeyALGO(oldKeyA);
		cData.setCipherALGO(oldCiph);
		cData.setArgon2Type(oldArgT);
	}

	void open() {
		final var cData = action.getCData();
		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, gridLayout(2), cfgTitle);
		final var cFolder = new CTabFolder(dialog, SWT.FLAT | SWT.TOP);
		cFolder.setFont(dialog.getFont());
		cFolder.setForeground(dialog.getForeground());
		cFolder.setLayoutData(getGridData(SWT.FILL, SWT.FILL, 1, 1, 2, 1));
		cFolder.setSelection(0);
		cFolder.setSelectionBackground(dialog.getBackground());
		cFolder.setSelectionForeground(dialog.getForeground());

		final var encTab = cTabItem(cFolder, SWT.NONE, KEY, cfgEnTab);
		final var encGroup = group(cFolder, gridLayout(2), empty);
		encGroup.setLayoutData(getGridData());

		final var groupCipher = group(encGroup, gridLayout(1), cfgEncry);
		final var comboCipher = combo(groupCipher, SWT.READ_ONLY);
		comboCipher.setLayoutData(getGridData(SWT.CENTER, SWT.CENTER, 1, 0));
		comboCipher.setItems(cfgAESGC, cfgCHA20);

		horizontalSeparator(encGroup);

		final var groupKey = group(encGroup, gridLayout(3), cfgKeyDF);
		final var btnArgon2 = button(groupKey, SWT.RADIO, cfgRecAr, widgetSelectedAdapter(e -> switchKDF(encGroup)));
		final var btnPBKDF2 = button(groupKey, SWT.RADIO, cfgPIter.substring(0, 18),
				widgetSelectedAdapter(e -> switchKDF(encGroup)));
		link(groupKey, owaAddress, cData.getLinkColor(), owaLink);

		final var groupArgon2 = group(encGroup, gridLayout(5), cfgArgon);
		final var comboArgon2 = combo(groupArgon2, SWT.READ_ONLY);
		comboArgon2.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
		comboArgon2.setItems(argon2d, argon2id);

		final var spinArgon2M = spinner(groupArgon2, cData.getArgon2Memo(), ARGON_MEMO_MIN, ARGON_MEMO_MAX, 0, 1, 16);
		final var spinArgon2I = spinner(groupArgon2, cData.getArgon2Iter(), ARGON_ITER_MIN, ARGON_ITER_MAX, 0, 1, 4);
		final var spinArgon2P = spinner(groupArgon2, cData.getArgon2Para(), ARGON_PARA_MIN, ARGON_PARA_MAX, 0, 1, 2);
		spinArgon2M.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
		spinArgon2I.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
		spinArgon2P.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
		final var btnTestA = button(groupArgon2, SWT.PUSH, cfgTestB,
				widgetSelectedAdapter(e -> testArgon2(cData, dialog, spinArgon2M, spinArgon2I, spinArgon2P)));

		final var groupPBKDF2 = group(encGroup, gridLayout(2), cfgPIter);
		final var spinPBKDF2 = spinner(groupPBKDF2, cData.getPBKDF2Iter(), PBKDF2_MIN, PBKDF2_MAX, 0, 1, 0x10000);
		spinPBKDF2.setLayoutData(getGridData(SWT.CENTER, SWT.CENTER, 1, 0));
		final var btnTestP = button(groupPBKDF2, SWT.PUSH, cfgTestB,
				widgetSelectedAdapter(e -> testPBKDF2(cData, dialog, spinPBKDF2)));

		encTab.setControl(encGroup);

		final var optTab = cTabItem(cFolder, SWT.NONE, GEAR, cfgOpTab);
		final var optGroup = group(cFolder, gridLayout(2), empty);
		optGroup.setLayoutData(getGridData());

		label(optGroup, SWT.HORIZONTAL, cfgClPwd);
		final var clearPwd = spinner(optGroup, cData.getClearPassword(), CLEAR_PWD_MIN, CLEAR_PWD_MAX, 0, 1, 10);
		clearPwd.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));

		horizontalSeparator(optGroup);

		label(optGroup, SWT.HORIZONTAL, cfgMinPl);
		final var minPwdLength = spinner(optGroup, cData.getPasswordMinLength(), PWD_MIN_LENGTH, PWD_MAX_LENGTH, 0, 1,
				4);
		minPwdLength.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));

		horizontalSeparator(optGroup);

		label(optGroup, SWT.HORIZONTAL, cfgColWh);
		final var columnWidth = spinner(optGroup, cData.getColumnWidth(), COL_MIN_WIDTH, COL_MAX_WIDTH, 0, 1, 10);
		columnWidth.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));

		label(optGroup, SWT.HORIZONTAL, cfgBuffL);
		final var bufferLength = spinner(optGroup, cData.getBufferLength(), BUFFER_MIN, BUFFER_MAX, 0, 1, 0x1000);
		bufferLength.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));

		label(optGroup, SWT.HORIZONTAL, cfgDivid);
		final var csvDivider = text(optGroup, SWT.BORDER | SWT.SINGLE);
		final var showPasswd = cData.isShowPassword();
		csvDivider.setEditable(showPasswd);
		csvDivider.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));
		csvDivider.setText(String.valueOf(cData.getDivider()));
		csvDivider.setTextLimit(1);
		csvDivider.setToolTipText(cfgDWarn);
		csvDivider.selectAll();

		horizontalSeparator(optGroup);

		final var base64Btn = button(optGroup, cData.isBase64(), cfgBase6);
		final var showPassBtn = button(optGroup, showPasswd, cfgShPwd);
		showPassBtn.addSelectionListener(widgetSelectedAdapter(e -> {
			base64Btn.setEnabled(showPassBtn.getSelection());
			csvDivider.setEditable(showPassBtn.getSelection());
			if (!showPassBtn.getSelection()) {
				base64Btn.setSelection(true);
				csvDivider.setText(String.valueOf(cData.getDivider()));
			}
		}));
		showPassBtn.setToolTipText(cfgShPTt);
		base64Btn.setEnabled(showPassBtn.getSelection());

		optTab.setControl(optGroup);

		emptyLabel(dialog, 2);

		final var okBtn = button(dialog, SWT.PUSH, entrOkay, widgetSelectedAdapter(e -> {
			final var cipherSelection = comboCipher.getSelectionIndex() == 0;
			cData.setCipherALGO(cipherSelection ? cipherAES : cipherChaCha20);
			cData.setKeyALGO(cipherSelection ? keyAES : keyChaCha20);
			cData.setArgon2(btnArgon2.getSelection());
			cData.setArgon2Type(comboArgon2.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
			cData.setArgon2Memo(spinArgon2M.getSelection());
			cData.setArgon2Iter(spinArgon2I.getSelection());
			cData.setArgon2Para(spinArgon2P.getSelection());
			cData.setPBKDF2Iter(spinPBKDF2.getSelection());
			cData.setBase64(base64Btn.getSelection());
			cData.setBufferLength(bufferLength.getSelection());
			cData.setClearPassword(clearPwd.getSelection());
			cData.setColumnWidth(columnWidth.getSelection());
			cData.setPasswordMinLength(minPwdLength.getSelection());
			cData.setShowPassword(showPassBtn.getSelection());

			if (csvDivider.getCharCount() > 0) {
				final var newDivider = csvDivider.getTextChars()[0];
				cData.setHeader(cData.getHeader().replace(cData.getDivider(), newDivider));
				cData.setDivider(newDivider);
			} else {
				cData.setDivider(comma.charAt(0));
			}
			if (!cData.isShowPassword()) {
				action.hidePasswordColumn();
			}

			dialog.close();
			action.resizeColumns();
			action.updateUI();
		}));

		var gridData = getGridData(SWT.END, SWT.CENTER, 1, 0);
		gridData.widthHint = BUTTON_WIDTH;
		okBtn.setLayoutData(gridData);
		dialog.setDefaultButton(okBtn);

		final var clBtn = button(dialog, SWT.PUSH, entrCanc, widgetSelectedAdapter(e -> dialog.close()));
		gridData = getGridData(SWT.LEAD, SWT.CENTER, 1, 0);
		gridData.widthHint = BUTTON_WIDTH;
		clBtn.setLayoutData(gridData);

		comboCipher.select(cipherAES.equals(cData.getCipherALGO()) ? 0 : 1);
		comboArgon2.select(cData.getArgon2Type() == Argon2.D ? 0 : 1);

		final var isArgon2 = cData.isArgon2();
		btnArgon2.setSelection(isArgon2);
		btnPBKDF2.setSelection(!isArgon2);

		switchKDF(encGroup);

		if (cData.isLocked()) {
			comboCipher.setEnabled(false);
			comboArgon2.setEnabled(false);
			btnArgon2.setEnabled(false);
			btnPBKDF2.setEnabled(false);
			spinArgon2M.setEnabled(false);
			spinArgon2I.setEnabled(false);
			spinArgon2P.setEnabled(false);
			spinPBKDF2.setEnabled(false);
			btnTestA.setEnabled(false);
			btnTestP.setEnabled(false);
		}

		dialog.pack();
		dialog.open();
	}
}
