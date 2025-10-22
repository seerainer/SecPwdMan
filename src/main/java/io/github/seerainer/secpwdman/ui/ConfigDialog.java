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
package io.github.seerainer.secpwdman.ui;

import static io.github.seerainer.secpwdman.ui.Widgets.button;
import static io.github.seerainer.secpwdman.ui.Widgets.cTabItem;
import static io.github.seerainer.secpwdman.ui.Widgets.combo;
import static io.github.seerainer.secpwdman.ui.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.ui.Widgets.group;
import static io.github.seerainer.secpwdman.ui.Widgets.horizontalSeparator;
import static io.github.seerainer.secpwdman.ui.Widgets.label;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.ui.Widgets.spinner;
import static io.github.seerainer.secpwdman.ui.Widgets.text;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.crypto.CryptoConfig;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.crypto.CryptoFactory;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The record ConfigDialog.
 */
record ConfigDialog(Action action) implements CryptoConstants, Icons, PrimitiveConstants, StringConstants {

    private static final Logger LOG = LogFactory.getLog();

    private static GridLayout gridLayout(final int numColumns) {
	return getLayout(numColumns, 12, 10, 10, 10, 10, 10);
    }

    private static void enableChildren(final Group group, final boolean enable) {
	for (final var item : group.getChildren()) {
	    item.setEnabled(enable);
	}
    }

    private static CryptoConfig.KDF getKeyDerivationFromCombo(final Combo combo) {
	return switch (combo.getSelectionIndex()) {
	case 0 -> CryptoConfig.KDF.Argon2;
	case 1 -> CryptoConfig.KDF.PBKDF2;
	case 2 -> CryptoConfig.KDF.scrypt;
	default -> CryptoConfig.KDF.Argon2;
	};
    }

    private static void setScryptPMin(final Combo combo, final Spinner spinScryptP) {
	final var selection = Integer.parseInt(combo.getText());
	final int[][] thresholds = { { 128, 1 }, { 64, 2 }, { 32, 3 }, { 16, 5 }, { 8, 10 } };
	for (final int[] pair : thresholds) {
	    if (selection >= pair[0]) {
		spinScryptP.setMinimum(pair[1]);
		break;
	    }
	}
    }

    private static int getScryptWorkFactor(final int n) {
	return switch (n) {
	case 8 -> 0;
	case 16 -> 1;
	case 32 -> 2;
	case 64 -> 3;
	case 128 -> 4;
	case 256 -> 5;
	case 512 -> 6;
	default -> 4;
	};
    }

    private static void switchKDF(final Group group) {
	final var groupKey = (Group) group.getChildren()[0];
	final var groupArgon2 = (Group) group.getChildren()[1];
	final var groupPBKDF2 = (Group) group.getChildren()[2];
	final var groupScrypt = (Group) group.getChildren()[3];
	final var combo = ((Combo) groupKey.getChildren()[1]);
	groupArgon2.setRedraw(false);
	groupPBKDF2.setRedraw(false);
	switch (combo.getSelectionIndex()) {
	case 0 -> {
	    enableChildren(groupArgon2, true);
	    enableChildren(groupPBKDF2, false);
	    enableChildren(groupScrypt, false);
	}
	case 1 -> {
	    enableChildren(groupArgon2, false);
	    enableChildren(groupPBKDF2, true);
	    enableChildren(groupScrypt, false);
	}
	default -> {
	    enableChildren(groupArgon2, false);
	    enableChildren(groupPBKDF2, false);
	    enableChildren(groupScrypt, true);
	}
	}
	groupArgon2.setRedraw(true);
	groupPBKDF2.setRedraw(true);
    }

    private static void testArgon2(final CryptoConfig cConf, final Shell shell, final Combo combo, final Spinner memo,
	    final Spinner iter, final Spinner para) {
	final var oldKDF = cConf.getKeyDerivation();
	final var oldType = cConf.getArgon2Type();
	final var oldMemo = cConf.getArgon2Memo();
	final var oldIter = cConf.getArgon2Iter();
	final var oldPara = cConf.getArgon2Para();
	cConf.setKeyDerivation(CryptoConfig.KDF.Argon2);
	cConf.setArgon2Type(combo.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
	cConf.setArgon2Memo(memo.getSelection());
	cConf.setArgon2Iter(iter.getSelection());
	cConf.setArgon2Para(para.getSelection());
	timeTest(cConf, shell);
	cConf.setKeyDerivation(oldKDF);
	cConf.setArgon2Type(oldType);
	cConf.setArgon2Memo(oldMemo);
	cConf.setArgon2Iter(oldIter);
	cConf.setArgon2Para(oldPara);
    }

    private static void testPBKDF2(final CryptoConfig cConf, final Shell shell, final Combo combo, final Spinner iter) {
	final var oldKDF = cConf.getKeyDerivation();
	final var oldHmac = cConf.getHmac();
	final var oldIter = cConf.getPBKDF2Iter();
	cConf.setKeyDerivation(CryptoConfig.KDF.PBKDF2);
	cConf.setHmac(combo.getSelectionIndex() == 0 ? Hmac.SHA256 : Hmac.SHA512);
	cConf.setPBKDF2Iter(iter.getSelection());
	timeTest(cConf, shell);
	cConf.setKeyDerivation(oldKDF);
	cConf.setHmac(oldHmac);
	cConf.setPBKDF2Iter(oldIter);
    }

    private static void testScrypt(final CryptoConfig cConf, final Shell shell, final Combo n, final Spinner r,
	    final Spinner p) {
	final var oldKDF = cConf.getKeyDerivation();
	final var oldN = cConf.getScryptN();
	final var oldR = cConf.getScryptR();
	final var oldP = cConf.getScryptP();
	cConf.setKeyDerivation(CryptoConfig.KDF.scrypt);
	cConf.setScryptN(Integer.parseInt(n.getText()));
	cConf.setScryptR(r.getSelection());
	cConf.setScryptP(p.getSelection());
	timeTest(cConf, shell);
	cConf.setKeyDerivation(oldKDF);
	cConf.setScryptN(oldN);
	cConf.setScryptR(oldR);
	cConf.setScryptP(oldP);
    }

    private static void timeTest(final CryptoConfig cConf, final Shell shell) {
	final var oldKeyA = cConf.getKeyALGO();
	final var oldCiph = cConf.getCipherALGO();
	final var group = ((Group) ((CTabFolder) shell.getChildren()[0]).getChildren()[1]);
	final var comboCiph = (Combo) ((Group) group.getChildren()[0]).getChildren()[0];
	final var comboKDF = (Combo) ((Group) group.getChildren()[0]).getChildren()[1];
	final var cipherText = comboCiph.getSelectionIndex() == 0 ? cipherAES : cipherChaCha20;
	final var kdfText = getKeyDerivationFromCombo(comboKDF).toString();
	final var select = comboCiph.getSelectionIndex() == 0;
	cConf.setKeyALGO(select ? keyAES : keyChaCha20);
	cConf.setCipherALGO(select ? cipherAES : cipherChaCha20);
	final var crypt = CryptoFactory.crypto(cConf);
	final var txt = Crypto.getRandomValue(TEST_SIZE);
	final var pwd = Crypto.getRandomValue(OUT_LENGTH);
	try {
	    final var start = System.currentTimeMillis();
	    final var enc = crypt.encrypt(txt, pwd);
	    final var end = System.currentTimeMillis();
	    crypt.decrypt(enc, pwd);
	    final var t0 = Long.valueOf(end - start);
	    final var t1 = Long.valueOf(System.currentTimeMillis() - end);
	    LOG.info(TIME_CRYPTO, cipherText, kdfText, t0, t1);
	    msg(shell, SWT.ICON_INFORMATION | SWT.OK, titleInf, cfgTestI.formatted(cipherText, kdfText, t0, t1));
	} catch (final Exception e) {
	    LOG.error(ERROR, e);
	    msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
	}
	cConf.setKeyALGO(oldKeyA);
	cConf.setCipherALGO(oldCiph);
    }

    Shell open() {
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

	final var groupCiphKDF = group(encGroup, gridLayout(2), cfgEncry);
	final var comboCipher = combo(groupCiphKDF, SWT.READ_ONLY);
	comboCipher.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboCipher.setItems(cfgAESGC, cfgCHA20);
	final var comboKDF = combo(groupCiphKDF, SWT.READ_ONLY);
	comboKDF.addSelectionListener(widgetSelectedAdapter(_ -> switchKDF(encGroup)));
	comboKDF.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));
	comboKDF.setItems(cfgRecAr, pbkdf2, SCRYPT);

	final var groupArgon2 = group(encGroup, gridLayout(5), cfgArgon);
	final var comboArgon2 = combo(groupArgon2, SWT.READ_ONLY);
	comboArgon2.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboArgon2.setItems(argon2d, argon2id);
	final var cConf = cData.getCryptoConfig();
	final var spinArgon2M = spinner(groupArgon2, cConf.getArgon2Memo(), ARGON2_MEMO_MIN, ARGON2_MEMO_MAX, 0, 1, 16);
	final var spinArgon2I = spinner(groupArgon2, cConf.getArgon2Iter(), ARGON2_ITER_MIN, ARGON2_ITER_MAX, 0, 1, 4);
	final var spinArgon2P = spinner(groupArgon2, cConf.getArgon2Para(), ARGON2_PARA_MIN, ARGON2_PARA_MAX, 0, 1, 2);
	spinArgon2M.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	spinArgon2I.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	spinArgon2P.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	final var btnTestA = button(groupArgon2, SWT.PUSH, cfgTestB, widgetSelectedAdapter(
		_ -> testArgon2(cConf, dialog, comboArgon2, spinArgon2M, spinArgon2I, spinArgon2P)));

	final var groupPBKDF2 = group(encGroup, gridLayout(3), cfgPIter);
	final var comboPBKDF2 = combo(groupPBKDF2, SWT.READ_ONLY);
	comboPBKDF2.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboPBKDF2.setItems(Hmac.SHA256.toString(), Hmac.SHA512.toString());
	final var spinPBKDF2 = spinner(groupPBKDF2, cConf.getPBKDF2Iter(), PBKDF2_MIN_SHA256, PBKDF2_MAX, 0, 1, 10000);
	spinPBKDF2.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboPBKDF2.addSelectionListener(widgetSelectedAdapter(_ -> spinPBKDF2
		.setMinimum(comboPBKDF2.getSelectionIndex() == 0 ? PBKDF2_MIN_SHA256 : PBKDF2_MIN_SHA512)));
	final var btnTestP = button(groupPBKDF2, SWT.PUSH, cfgTestB,
		widgetSelectedAdapter(_ -> testPBKDF2(cConf, dialog, comboPBKDF2, spinPBKDF2)));

	final var groupScrypt = group(encGroup, gridLayout(4), cfgScryp);
	final var comboScrypt = combo(groupScrypt, SWT.READ_ONLY);
	comboScrypt.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboScrypt.setItems(Arrays.stream(SCRYPT_N).mapToObj(String::valueOf).toArray(String[]::new));
	final var spinScryptR = spinner(groupScrypt, cConf.getScryptR(), SCRYPT_R, SCRYPT_R, 0, 1, 2);
	final var spinScryptP = spinner(groupScrypt, cConf.getScryptP(), SCRYPT_P_MIN, SCRYPT_P_MAX, 0, 1, 2);
	spinScryptR.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	spinScryptP.setLayoutData(getGridData(SWT.LEAD, SWT.CENTER, 1, 0));
	comboScrypt.addSelectionListener(widgetSelectedAdapter(_ -> setScryptPMin(comboScrypt, spinScryptP)));
	final var btnTestS = button(groupScrypt, SWT.PUSH, cfgTestB,
		widgetSelectedAdapter(_ -> testScrypt(cConf, dialog, comboScrypt, spinScryptR, spinScryptP)));

	encTab.setControl(encGroup);

	final var optTab = cTabItem(cFolder, SWT.NONE, GEAR, cfgOpTab);
	final var optGroup = group(cFolder, gridLayout(2), empty);
	optGroup.setLayoutData(getGridData());

	label(optGroup, SWT.HORIZONTAL, cfgAutoL);
	final var autoLock = spinner(optGroup, cData.getAutoLockTime(), AUTOLOCK_MIN, AUTOLOCK_MAX, 0, 1, 2);
	autoLock.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));

	horizontalSeparator(optGroup);

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
	csvDivider.setLayoutData(getGridData(SWT.END, SWT.CENTER, 1, 0));
	csvDivider.setText(String.valueOf(cData.getDivider()));
	csvDivider.setTextLimit(1);
	csvDivider.selectAll();

	horizontalSeparator(optGroup);

	final var deflateBtn = button(optGroup, cData.isCompress(), cfgDefla);

	optTab.setControl(optGroup);

	emptyLabel(dialog, 2);

	final var okBtn = button(dialog, SWT.PUSH, dialOkay, widgetSelectedAdapter(_ -> {
	    final var cipherSelection = comboCipher.getSelectionIndex() == 0;
	    cConf.setCipherALGO(cipherSelection ? cipherAES : cipherChaCha20);
	    cConf.setKeyALGO(cipherSelection ? keyAES : keyChaCha20);
	    cConf.setKeyDerivation(getKeyDerivationFromCombo(comboKDF));
	    cConf.setArgon2Type(comboArgon2.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
	    cConf.setArgon2Memo(spinArgon2M.getSelection());
	    cConf.setArgon2Iter(spinArgon2I.getSelection());
	    cConf.setArgon2Para(spinArgon2P.getSelection());
	    cConf.setHmac(comboPBKDF2.getSelectionIndex() == 0 ? Hmac.SHA256 : Hmac.SHA512);
	    cConf.setPBKDF2Iter(spinPBKDF2.getSelection());
	    cConf.setScryptN(Integer.parseInt(comboScrypt.getText()));
	    cConf.setScryptR(spinScryptR.getSelection());
	    cConf.setScryptP(spinScryptP.getSelection());
	    cData.setAutoLockTime(autoLock.getSelection());
	    cData.setBufferLength(bufferLength.getSelection());
	    cData.setClearPassword(clearPwd.getSelection());
	    cData.setColumnWidth(columnWidth.getSelection());
	    cData.setCompress(deflateBtn.getSelection());
	    cData.setPasswordMinLength(minPwdLength.getSelection());

	    if (csvDivider.getCharCount() > 0) {
		final var newDivider = csvDivider.getTextChars()[0];
		cData.setHeader(cData.getHeader().replace(cData.getDivider(), newDivider));
		cData.setDivider(newDivider);
	    } else {
		cData.setDivider(DELIMITER);
	    }
	    dialog.close();
	    action.hidePasswordColumn();
	    action.resizeColumns();
	    action.updateUI();
	}));

	var gridData = getGridData(SWT.END, SWT.CENTER, 1, 0);
	gridData.widthHint = BUTTON_WIDTH;
	okBtn.setLayoutData(gridData);
	dialog.setDefaultButton(okBtn);

	final var clBtn = button(dialog, SWT.PUSH, diaCancl, widgetSelectedAdapter(_ -> dialog.close()));
	gridData = getGridData(SWT.LEAD, SWT.CENTER, 1, 0);
	gridData.widthHint = BUTTON_WIDTH;
	clBtn.setLayoutData(gridData);

	comboCipher.select(cipherAES.equals(cConf.getCipherALGO()) ? 0 : 1);
	comboKDF.select(cConf.getKeyDerivation().ordinal());
	comboArgon2.select(cConf.getArgon2Type() == Argon2.D ? 0 : 1);
	comboPBKDF2.select(cConf.getHmac() == Hmac.SHA256 ? 0 : 1);
	comboScrypt.select(getScryptWorkFactor(cConf.getScryptN()));

	switchKDF(encGroup);

	if (cData.isLocked()) {
	    comboCipher.setEnabled(false);
	    comboArgon2.setEnabled(false);
	    comboKDF.setEnabled(false);
	    comboPBKDF2.setEnabled(false);
	    spinArgon2M.setEnabled(false);
	    spinArgon2I.setEnabled(false);
	    spinArgon2P.setEnabled(false);
	    spinPBKDF2.setEnabled(false);
	    comboScrypt.setEnabled(false);
	    spinScryptR.setEnabled(false);
	    spinScryptP.setEnabled(false);
	    btnTestA.setEnabled(false);
	    btnTestP.setEnabled(false);
	    btnTestS.setEnabled(false);
	}

	dialog.pack();
	dialog.open();
	return dialog;
    }
}
