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
import static io.github.seerainer.secpwdman.ui.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.ui.Widgets.group;
import static io.github.seerainer.secpwdman.ui.Widgets.label;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.ui.Widgets.spinner;
import static io.github.seerainer.secpwdman.ui.Widgets.text;
import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.MACOS;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.getUUID;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.Arrays;
import java.util.Objects;

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
import io.github.seerainer.secpwdman.util.RandomPassword;
import io.github.seerainer.secpwdman.util.Win32Affinity;

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

    private void editEntry(final Shell dialog, final TableItem tableItem) {
	final var child = dialog.getChildren();
	final var cData = action.getCData();
	final var index = getColumnIndexNumbers(cData);
	final var textFields = new String[index.length];
	final var password = ((Text) child[10]).getTextChars();
	for (var i = 0; i < textFields.length; i++) {
	    if (i != 5) {
		textFields[index[i]] = ((Text) child[i * 2]).getText();
	    }
	}
	if (Objects.nonNull(tableItem)) {
	    final var items = new String[textFields.length];
	    final var PASSWORD_INDEX = index[5];
	    for (var j = 0; j < items.length; j++) {
		if (j != PASSWORD_INDEX) {
		    items[j] = tableItem.getText(j);
		}
	    }
	    final var cas = new CharArrayString(tableItem.getText(PASSWORD_INDEX));
	    final var itemPassword = action.decryptPassword(cas.toCharArray());
	    if (isEqual(items, textFields) && isEqual(password, itemPassword)) {
		clear(itemPassword);
		clear(password);
		cas.clear();
		dialog.close();
		return;
	    }
	    clear(itemPassword);
	    cas.clear();
	}
	if (!isBlank(textFields[2]) || !isBlank(textFields[4])) {
	    final var groupChildren = ((Group) child[16]).getChildren();
	    editEntry(password.length > 0 ? password : RandomPassword.generate(action, groupChildren), tableItem,
		    textFields, groupChildren);
	    clear(password);
	    dialog.close();
	} else {
	    child[4].setFocus();
	    clear(password);
	}
    }

    private void editEntry(final char[] password, final TableItem tableItem, final String[] textFields,
	    final Control[] groupChildren) {
	final var selection = Arrays.stream(groupChildren)
		.map(control -> Boolean.valueOf(control instanceof Button && !((Button) control).getSelection()))
		.toArray(Boolean[]::new);
	if (Arrays.stream(selection).allMatch(Boolean::booleanValue)) {
	    for (var i = 0; i < selection.length - 1; i++) {
		((Button) groupChildren[i]).setSelection(true);
	    }
	}
	if (isEqual(textFields[4].toCharArray(), password)) {
	    msg(action.getShell(), SWT.ICON_WARNING | SWT.OK, titleWar, warnUPeq);
	}
	if (!isBlank(textFields[6])) {
	    textFields[6] = textFields[6].replaceAll(System.lineSeparator(), newLine);
	}
	final var table = action.getTable();
	if (action.getList().isVisible()) {
	    if (Objects.isNull(tableItem)) {
		action.resetGroupList();
		setText(password, textFields, new TableItem(table, SWT.NONE));
	    } else {
		final var uuid = tableItem.getText(0);
		action.resetGroupList();
		for (final var item : table.getItems()) {
		    if (uuid.equals(item.getText(0))) {
			setText(password, textFields, item);
			break;
		    }
		}
	    }
	} else if (Objects.isNull(tableItem)) {
	    setText(password, textFields, new TableItem(table, SWT.NONE));
	} else {
	    setText(password, textFields, table.getItem(table.getSelectionIndex()));
	}
	action.getCData().setModified(true);
	action.storeTableData(action.extractData(false));
	action.colorTable();
	action.fillGroupList();
	action.resizeColumns();
	action.updateUI();
	table.redraw();
    }

    Shell open(final int editLine) {
	final var cData = action.getCData();
	final var newEntry = editLine < 0;

	if (cData.isLocked() || cData.isCustomHeader() || (newEntry && cData.isReadOnly())
		|| !action.isKeyStoreReady()) {
	    return null;
	}
	DialogFactory.closeSearchDialog();

	final var shell = action.getShell();
	final var display = shell.getDisplay();
	final var image = getImage(display, APP_ICON);
	final var layout = getLayout(3, 6, 6, 20, 5, 5, 8);
	final var dialog = shell(shell, SWT.SHELL_TRIM & ~SWT.MIN | SWT.APPLICATION_MODAL, image, layout, null);
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
	pwd.addModifyListener(_ -> evalPasswordStrength(cData, pwdStrengthLabel, pwd.getTextChars()));

	emptyLabel(dialog, 1);

	final var random = group(dialog, new GridLayout(4, false), entrRand);
	button(random, true, rTextLoC);
	button(random, true, rTextUpC);
	button(random, true, rNumbers);
	button(random, true, rSpecia1);
	button(random, false, rSpecia2);
	button(random, false, entrSpac);
	label(random, SWT.HORIZONTAL, entrCust);
	final var customValueTxt = text(random, SWT.BORDER | SWT.SINGLE);
	customValueTxt.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0, 3, 1));

	label(random, SWT.HORIZONTAL, entrLgth);
	spinner(random, PWD_DEFAULT_LENGTH, PWD_MIN_LENGTH, PWD_MAX_LENGTH, 0, 1, 4);

	final var genBtn = button(random, SWT.PUSH, entrGene, widgetSelectedAdapter(_ -> {
	    final var randPwd = RandomPassword.generate(action, random.getChildren());
	    pwd.setTextChars(randPwd);
	    clear(randPwd);
	}));

	if (!MACOS) { // macOS does not support modification of the echo char
	    emptyLabel(dialog, 1);
	    final var echoChar = pwd.getEchoChar() == NULL_CHAR ? ECHO_CHAR : NULL_CHAR;
	    button(dialog, SWT.CHECK, entrShow, widgetSelectedAdapter(_ -> pwd.setEchoChar(echoChar)));
	}

	emptyLabel(dialog, 3);

	final var okBtn = button(dialog, SWT.PUSH, dialOkay, null);
	var gridData = getGridData(SWT.END, SWT.CENTER, 1, 0, 2, 1);
	gridData.widthHint = BUTTON_WIDTH;
	okBtn.setLayoutData(gridData);
	dialog.setDefaultButton(okBtn);

	final var clBtn = button(dialog, SWT.PUSH, diaCancl, widgetSelectedAdapter(_ -> dialog.close()));
	gridData = getGridData(SWT.LEAD, SWT.CENTER, 1, 0);
	gridData.widthHint = BUTTON_WIDTH;
	clBtn.setLayoutData(gridData);

	if (newEntry) {
	    okBtn.addSelectionListener(widgetSelectedAdapter(_ -> editEntry(dialog, null)));
	    dialog.setText(entrNewe);
	    uuid.setText(getUUID());
	} else {
	    final var item = action.getTable().getItem(editLine);
	    okBtn.addSelectionListener(widgetSelectedAdapter(_ -> editEntry(dialog, item)));

	    final var index = getColumnIndexNumbers(cData);
	    uuid.setText(item.getText(index[0]));

	    if (isBlank(uuid.getText())) {
		uuid.setText(getUUID());
	    }

	    group.setText(item.getText(index[1]));
	    title.setText(item.getText(index[2]));
	    url.setText(item.getText(index[3]));
	    user.setText(item.getText(index[4]));
	    final var cas = new CharArrayString(item.getText(index[5]));
	    final var password = action.decryptPassword(cas.toCharArray());
	    pwd.setTextChars(password);
	    clear(password);
	    cas.clear();
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
	display.asyncExec(() -> Win32Affinity.setWindowDisplayAffinity(dialog));
	return dialog;
    }

    private void setText(final char[] password, final String[] textFields, final TableItem item) {
	final var sb = new StringBuilder();
	final var cData = action.getCData();
	sb.append(action.encryptPassword(password));
	item.setText(textFields);
	item.setText(cData.getColumnMap().get(csvHeader[5]).intValue(), sb.toString());
    }
}
