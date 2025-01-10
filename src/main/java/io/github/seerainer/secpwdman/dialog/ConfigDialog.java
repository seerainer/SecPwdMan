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

import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
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
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
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
import io.github.seerainer.secpwdman.crypto.CryptoUtil;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class ConfigDialog.
 */
final class ConfigDialog implements CryptoConstants, Icons, PrimitiveConstants, StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	private static GridLayout gridLayout(final int numColumns) {
		return getLayout(numColumns, 20, 12, 10, 10, 10, 10);
	}

	private static void switchKDF(final Group group) {
		final var groupKey = (Group) group.getChildren()[2];
		final var groupArgon = (Group) group.getChildren()[3];
		final var groupPBKDF = (Group) group.getChildren()[4];
		final var selection = ((Button) groupKey.getChildren()[0]).getSelection();
		groupArgon.setRedraw(false);
		groupPBKDF.setRedraw(false);
		for (final var item : groupArgon.getChildren()) {
			item.setEnabled(selection);
		}
		for (final var item : groupPBKDF.getChildren()) {
			item.setEnabled(!selection);
		}
		groupArgon.setRedraw(true);
		groupPBKDF.setRedraw(true);
	}

	private static void testArgon2(final ConfigData cData, final Shell shell, final Spinner memo, final Spinner iter,
			final Spinner para) {
		final var oldArgo = cData.isArgon2();
		final var oldType = cData.getArgonType();
		final var oldMemo = cData.getArgonMemo();
		final var oldIter = cData.getArgonIter();
		final var oldPara = cData.getArgonPara();
		cData.setArgon2(true);
		cData.setArgonMemo(memo.getSelection());
		cData.setArgonIter(iter.getSelection());
		cData.setArgonPara(para.getSelection());
		timeTest(cData, shell);
		cData.setArgon2(oldArgo);
		cData.setArgonType(oldType);
		cData.setArgonMemo(oldMemo);
		cData.setArgonIter(oldIter);
		cData.setArgonPara(oldPara);
	}

	private static void testPBKDF2(final ConfigData cData, final Shell shell, final Spinner iter) {
		final var oldArgo = cData.isArgon2();
		final var oldIter = cData.getPBKDFIter();
		cData.setArgon2(false);
		cData.setPBKDFIter(iter.getSelection());
		timeTest(cData, shell);
		cData.setArgon2(oldArgo);
		cData.setPBKDFIter(oldIter);
	}

	private static void timeTest(final ConfigData cData, final Shell shell) {
		final var oldKeyA = cData.getKeyALGO();
		final var oldCiph = cData.getCipherALGO();
		final var oldArgT = cData.getArgonType();
		final var group = ((Group) ((CTabFolder) shell.getChildren()[0]).getChildren()[1]);
		final var comboC = (Combo) ((Group) group.getChildren()[0]).getChildren()[0];
		final var comboA = (Combo) ((Group) group.getChildren()[3]).getChildren()[0];
		final var select = comboC.getSelectionIndex() == 0;
		cData.setKeyALGO(select ? keyAES : keyChaCha20);
		cData.setCipherALGO(select ? cipherAES : cipherChaCha20);
		cData.setArgonType(comboA.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
		final var crypt = CryptoFactory.crypto(cData);
		final var rand = CryptoUtil.getSecureRandom();
		final var txt = new byte[MEM_SIZE];
		final var pwd = new byte[OUT_LENGTH];
		rand.nextBytes(txt);
		rand.nextBytes(pwd);
		try {
			final var start = System.currentTimeMillis();
			final var enc = crypt.encrypt(txt, pwd);
			final var end = System.currentTimeMillis();
			crypt.decrypt(enc, pwd);
			final var t0 = Long.valueOf(end - start);
			final var t1 = Long.valueOf(System.currentTimeMillis() - end);
			LOG.info("Encrypted: %d ms, Decrypted: %d ms".formatted(t0, t1));
			msg(shell, SWT.ICON_INFORMATION | SWT.OK, titleInf, cfgTestI.formatted(t0, t1));
		} catch (final Exception e) {
			LOG.error(e.fillInStackTrace().toString());
			msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
		}
		cData.setKeyALGO(oldKeyA);
		cData.setCipherALGO(oldCiph);
		cData.setArgonType(oldArgT);
	}

	private final Action action;

	/**
	 * Instantiates a new config dialog.
	 *
	 * @param action the action
	 */
	ConfigDialog(final Action action) {
		this.action = action;
	}

	/**
	 * Open.
	 */
	void open() {
		final var cData = action.getCData();
		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, gridLayout(1), cfgTitle);

		final var display = dialog.getDisplay();
		final var cFolder = new CTabFolder(dialog, SWT.FLAT | SWT.TOP);
		final var clientArea = dialog.getClientArea();
		cFolder.setFont(dialog.getFont());
		cFolder.setForeground(dialog.getForeground());
		cFolder.setLocation(clientArea.x, clientArea.y);
		cFolder.setSelection(0);
		cFolder.setSelectionBackground(dialog.getBackground());
		cFolder.setSelectionForeground(dialog.getForeground());

		final var encTab = new CTabItem(cFolder, SWT.NONE);
		encTab.setImage(getImage(display, KEY));
		encTab.setText(cfgEnTab);

		final var encGroup = group(cFolder, gridLayout(2), empty);
		encGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL));

		final var groupCipher = group(encGroup, gridLayout(1), cfgEncry);
		final var comboCipher = combo(groupCipher, SWT.READ_ONLY);
		comboCipher.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		comboCipher.setItems(cfgAESGC, cfgCHA20);

		horizontalSeparator(encGroup);

		final var groupKey = group(encGroup, gridLayout(3), cfgKeyDF);
		final var argon2btn = button(groupKey, SWT.RADIO, cfgRecAr, widgetSelectedAdapter(e -> switchKDF(encGroup)));
		final var pbkdf2btn = button(groupKey, SWT.RADIO, cfgPIter.substring(0, 18),
				widgetSelectedAdapter(e -> switchKDF(encGroup)));
		link(groupKey, owaAddress, cData.getLinkColor(), owaLink);

		final var groupArgon = group(encGroup, gridLayout(5), cfgArgon);
		final var comboArgon = combo(groupArgon, SWT.READ_ONLY);
		comboArgon.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, true, false));
		comboArgon.setItems(argon2d, argon2id);

		final var argonMspinner = spinner(groupArgon, cData.getArgonMemo(), 19, 256, 0, 1, 16);
		final var argonTspinner = spinner(groupArgon, cData.getArgonIter(), 2, 128, 0, 1, 8);
		final var argonPspinner = spinner(groupArgon, cData.getArgonPara(), 1, 64, 0, 1, 4);
		argonMspinner.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, true, false));
		argonTspinner.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, true, false));
		argonPspinner.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, true, false));
		final var testBtnA = button(groupArgon, SWT.PUSH, cfgTestB,
				widgetSelectedAdapter(e -> testArgon2(cData, dialog, argonMspinner, argonTspinner, argonPspinner)));

		final var groupPBKDF = group(encGroup, gridLayout(2), cfgPIter);
		final var pbkdfIter = spinner(groupPBKDF, cData.getPBKDFIter(), 600000, 9999999, 0, 1, 10000);
		pbkdfIter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		final var testBtnB = button(groupPBKDF, SWT.PUSH, cfgTestB,
				widgetSelectedAdapter(e -> testPBKDF2(cData, dialog, pbkdfIter)));

		encTab.setControl(encGroup);

		final var optTab = new CTabItem(cFolder, SWT.NONE);
		optTab.setImage(getImage(display, GEAR));
		optTab.setText(cfgOpTab);

		final var optGroup = group(cFolder, gridLayout(2), empty);
		optGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL));

		label(optGroup, SWT.HORIZONTAL, cfgClPwd);
		final var clearPwd = spinner(optGroup, cData.getClearPassword(), 5, 300, 0, 1, 10);

		horizontalSeparator(optGroup);

		label(optGroup, SWT.HORIZONTAL, cfgMinPl);
		final var minPwdLength = spinner(optGroup, cData.getPasswordMinLength(), PASSWORD_ABSOLUTE_MIN_LENGTH, 64, 0, 1, 4);

		horizontalSeparator(optGroup);

		label(optGroup, SWT.HORIZONTAL, cfgColWh);
		final var columnWidth = spinner(optGroup, cData.getColumnWidth(), 10, 5000, 0, 1, 10);

		label(optGroup, SWT.HORIZONTAL, cfgBuffL);
		final var bufferLength = spinner(optGroup, cData.getBufferLength(), 64, 65536, 0, 1, 64);

		label(optGroup, SWT.HORIZONTAL, cfgDivid);
		final var csvDivider = text(optGroup, SWT.BORDER | SWT.SINGLE);
		final var showPasswd = cData.isShowPassword();
		csvDivider.setEditable(showPasswd);
		csvDivider.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, true, false));
		csvDivider.setText(String.valueOf(cData.getDivider()));
		csvDivider.setTextLimit(1);
		csvDivider.setToolTipText(cfgDWarn);
		csvDivider.selectAll();

		horizontalSeparator(optGroup);

		final var showPassBtn = button(optGroup, showPasswd, cfgShPwd);
		showPassBtn.addSelectionListener(widgetSelectedAdapter(e -> csvDivider.setEditable(showPassBtn.getSelection())));

		optTab.setControl(optGroup);

		emptyLabel(dialog, 1);

		final var okBtn = button(dialog, SWT.PUSH, entrOkay, widgetSelectedAdapter(e -> {
			final var cipherSelection = comboCipher.getSelectionIndex() == 0;
			cData.setCipherALGO(cipherSelection ? cipherAES : cipherChaCha20);
			cData.setKeyALGO(cipherSelection ? keyAES : keyChaCha20);
			cData.setArgon2(argon2btn.getSelection());
			cData.setArgonType(comboArgon.getSelectionIndex() == 0 ? Argon2.D : Argon2.ID);
			cData.setArgonMemo(argonMspinner.getSelection());
			cData.setArgonIter(argonTspinner.getSelection());
			cData.setArgonPara(argonPspinner.getSelection());
			cData.setPBKDFIter(pbkdfIter.getSelection());
			cData.setClearPassword(clearPwd.getSelection());
			cData.setColumnWidth(columnWidth.getSelection());
			cData.setBufferLength(bufferLength.getSelection());
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

		final var gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridData.widthHint = 80;
		okBtn.setLayoutData(gridData);
		dialog.setDefaultButton(okBtn);

		comboCipher.select(cipherAES.equals(cData.getCipherALGO()) ? 0 : 1);
		comboArgon.select(cData.getArgonType() == Argon2.D ? 0 : 1);

		final var isArgon2 = cData.isArgon2();
		argon2btn.setSelection(isArgon2);
		pbkdf2btn.setSelection(!isArgon2);

		switchKDF(encGroup);

		if (cData.isLocked()) {
			comboCipher.setEnabled(false);
			comboArgon.setEnabled(false);
			argon2btn.setEnabled(false);
			pbkdf2btn.setEnabled(false);
			argonMspinner.setEnabled(false);
			argonTspinner.setEnabled(false);
			argonPspinner.setEnabled(false);
			pbkdfIter.setEnabled(false);
			testBtnA.setEnabled(false);
			testBtnB.setEnabled(false);
		}

		dialog.pack();
		dialog.open();
	}
}
