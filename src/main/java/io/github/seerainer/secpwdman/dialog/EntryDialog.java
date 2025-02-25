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

import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.MACOS;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.getBase64Char;
import static io.github.seerainer.secpwdman.util.Util.getBase64Encode;
import static io.github.seerainer.secpwdman.util.Util.getUUID;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
import static io.github.seerainer.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.group;
import static io.github.seerainer.secpwdman.widgets.Widgets.label;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.spinner;
import static io.github.seerainer.secpwdman.widgets.Widgets.text;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.io.CharArrayString;
import io.github.seerainer.secpwdman.util.CharsetUtil;
import io.github.seerainer.secpwdman.util.RandomPassword;

/**
 * The record EntryDialog.
 */
record EntryDialog(Action action) implements Icons, PrimitiveConstants, StringConstants {

	private static int[] getColumnIndexNumbers(final ConfigData cData) {
		final var length = csvHeader.length;
		final var header = new int[length];
		final var map = cData.getColumnMap();
		for (var i = 0; i < length; i++) {
			header[i] = map.get(csvHeader[i]).intValue();
		}
		return header;
	}

	private static void setText(final char[] password, final ConfigData cData, final String[] textFields,
			final TableItem item) {
		final var index = cData.getColumnMap().get(csvHeader[5]).intValue();
		item.setText(textFields);
		item.setText(index, new String(getBase64Encode(CharsetUtil.toBytes(password)), StandardCharsets.UTF_8));
	}

	private void editEntry(final Shell dialog, final TableItem tableItem) {
		final var child = dialog.getChildren();
		final var index = getColumnIndexNumbers(action.getCData());
		final var textFields = new String[index.length];
		final var password = ((Text) child[10]).getTextChars();
		for (var i = 0; i < textFields.length; i++) {
			if (i != 5) {
				textFields[index[i]] = ((Text) child[i * 2]).getText();
			}
		}
		if (tableItem != null) {
			final var items = new String[textFields.length];
			for (var j = 0; j < items.length; j++) {
				if (j != 5) {
					items[j] = tableItem.getText(j);
				}
			}
			final var itemPassword = getBase64Char(new CharArrayString(tableItem.getText(5)));
			if (isEqual(items, textFields) && isEqual(password, itemPassword)) {
				dialog.close();
				clear(itemPassword);
				clear(password);
				return;
			}
			clear(itemPassword);
		}
		if (!isBlank(textFields[2]) || !isBlank(textFields[4])) {
			editEntry(password, tableItem, textFields, ((Group) child[16]).getChildren());
			dialog.close();
		} else {
			child[4].setFocus();
		}
	}

	private void editEntry(final char[] password, final TableItem tableItem, final String[] textFields,
			final Control[] groupChildren) {
		final var cData = action.getCData();
		final var table = action.getTable();
		final var selection = Arrays.stream(groupChildren)
				.map(control -> Boolean.valueOf(control instanceof Button && !((Button) control).getSelection()))
				.toArray(Boolean[]::new);
		if (Arrays.stream(selection).allMatch(Boolean::booleanValue)) {
			for (var i = 0; i < selection.length - 1; i++) {
				((Button) groupChildren[i]).setSelection(true);
			}
		}
		var newPassword = password.clone();
		clear(password);
		if (newPassword.length == 0) {
			newPassword = RandomPassword.generate(action, groupChildren);
		} else if (isEqual(textFields[4].toCharArray(), password)) {
			msg(action.getShell(), SWT.ICON_WARNING | SWT.OK, titleWar, warnUPeq);
		}
		if (!isBlank(textFields[6])) {
			textFields[6] = textFields[6].replaceAll(System.lineSeparator(), newLine);
		}
		if (action.getList().isVisible()) {
			if (tableItem == null) {
				action.resetGroupList();
				setText(newPassword, cData, textFields, new TableItem(table, SWT.NONE));
			} else {
				final var uuid = tableItem.getText(0);
				action.resetGroupList();
				for (final var item : table.getItems()) {
					if (uuid.equals(item.getText(0))) {
						setText(newPassword, cData, textFields, item);
						break;
					}
				}
			}
		} else if (tableItem == null) {
			setText(newPassword, cData, textFields, new TableItem(table, SWT.NONE));
		} else {
			setText(newPassword, cData, textFields, table.getItem(table.getSelectionIndex()));
		}
		cData.getSensitiveData().setSealedData(action.cryptData(action.extractData()));
		cData.setModified(true);
		action.colorURL();
		action.fillGroupList();
		action.resizeColumns();
		action.updateUI();
		table.redraw();
	}

	void open(final int editLine) {
		final var cData = action.getCData();
		final var newEntry = editLine < 0;

		if (cData.isLocked() || cData.isCustomHeader() || (newEntry && cData.isReadOnly())) {
			return;
		}

		SearchDialog.close();

		final var shell = action.getShell();
		final var image = getImage(shell.getDisplay(), APP_ICON);
		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE, image,
				getLayout(3, 6, 6, 20, 5, 5, 8), null);
		final var uuid = text(dialog, SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrGrou);
		final var group = text(dialog, SWT.BORDER | SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrTitl);
		final var title = text(dialog, SWT.BORDER | SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrLink);
		final var url = text(dialog, SWT.BORDER | SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrUser);
		final var user = text(dialog, SWT.BORDER | SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrPass);
		final var pwd = text(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);

		label(dialog, SWT.HORIZONTAL, entrNote);
		final var notes = text(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);

		emptyLabel(dialog, 1);

		final var pwdStrength = group(dialog, new GridLayout(), entrPInd);
		final var pwdStrengthLabel = label(pwdStrength, SWT.HORIZONTAL, passShor);
		pwdStrengthLabel.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
		pwdStrengthLabel.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0));
		pwd.addModifyListener(e -> evalPasswordStrength(cData, pwdStrengthLabel, pwd.getTextChars()));

		emptyLabel(dialog, 1);

		final var random = group(dialog, new GridLayout(4, false), entrRand);
		button(random, true, rTextLoC);
		button(random, true, rTextUpC);
		button(random, true, rNumbers);
		button(random, true, rSpecia1);
		button(random, false, rSpecia2);
		button(random, false, entrSpac);

		emptyLabel(random, 4);

		label(random, SWT.HORIZONTAL, entrLgth);
		spinner(random, 20, PWD_MIN_LENGTH, PWD_MAX_LENGTH, 0, 1, 4);

		final var genBtn = button(random, SWT.PUSH, entrGene, widgetSelectedAdapter(e -> {
			final var randPwd = RandomPassword.generate(action, random.getChildren());
			pwd.setTextChars(randPwd);
			clear(randPwd);
		}));

		emptyLabel(dialog, 1);

		if (!MACOS) { // macOS does not support modification of the echo char
			button(dialog, SWT.CHECK, entrShow,
					widgetSelectedAdapter(e -> pwd.setEchoChar(pwd.getEchoChar() == nullChr ? echoChr : nullChr)));
		}

		emptyLabel(dialog, 3);

		final var okBtn = button(dialog, SWT.PUSH, entrOkay, null);
		var gridData = getGridData(SWT.END, SWT.TOP, 1, 0, 2, 1);
		gridData.widthHint = BUTTON_WIDTH;
		okBtn.setLayoutData(gridData);
		dialog.setDefaultButton(okBtn);

		final var clBtn = button(dialog, SWT.PUSH, entrCanc, widgetSelectedAdapter(e -> dialog.close()));
		gridData = getGridData(SWT.LEAD, SWT.TOP, 1, 0);
		gridData.widthHint = BUTTON_WIDTH;
		clBtn.setLayoutData(gridData);

		if (newEntry) {
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, null)));
			dialog.setText(entrNewe);
			uuid.setText(getUUID());
		} else {
			final var item = action.getTable().getItem(editLine);
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, item)));

			final var index = getColumnIndexNumbers(cData);
			uuid.setText(item.getText(index[0]));

			if (isBlank(uuid.getText())) {
				uuid.setText(getUUID());
			}

			group.setText(item.getText(index[1]));
			title.setText(item.getText(index[2]));
			url.setText(item.getText(index[3]));
			user.setText(item.getText(index[4]));
			pwd.setTextChars(getBase64Char(new CharArrayString(item.getText(index[5]))));
			notes.setText(item.getText(index[6]));

			if (cData.isReadOnly()) {
				group.setEditable(false);
				title.setEditable(false);
				url.setEditable(false);
				user.setEditable(false);
				pwd.setEditable(false);
				notes.setEditable(false);
				genBtn.setEnabled(false);
				dialog.setText(entrView);
			} else {
				dialog.setText(entrEdit);
			}
		}

		final var size = 25;
		final var point = getPrefSize(dialog);
		dialog.setSize(point.x + size, point.y + size * 2);
		image.dispose();
		dialog.open();
		group.selectAll();
		title.selectAll();
		url.selectAll();
		user.selectAll();
		pwd.selectAll();
		title.setFocus();
	}
}
