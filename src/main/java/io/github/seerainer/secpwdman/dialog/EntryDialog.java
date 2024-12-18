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
package io.github.seerainer.secpwdman.dialog;

import static io.github.seerainer.secpwdman.util.PasswordStrength.evalPasswordStrength;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.Util.isEmpty;
import static io.github.seerainer.secpwdman.util.Util.isEqual;
import static io.github.seerainer.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.group;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.newButton;
import static io.github.seerainer.secpwdman.widgets.Widgets.newLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.newText;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfData;
import io.github.seerainer.secpwdman.images.IMG;
import io.github.seerainer.secpwdman.util.RandomPassword;

/**
 * The Class EntryDialog.
 */
public class EntryDialog {

	/**
	 * Get column index numbers.
	 *
	 * @param cData the cData
	 * @return int[]
	 */
	private static int[] getColumnIndexNumbers(final ConfData cData) {
		final var csvHeader = cData.csvHeader;
		final var length = csvHeader.length;
		final var header = new int[length];
		final var map = cData.getColumnMap();

		for (var i = 0; i < length; i++)
			header[i] = map.get(csvHeader[i]).intValue();

		return header;
	}

	/**
	 * Get random UUID.
	 *
	 * @return randomUUID
	 */
	private static String getUUID() {
		return UUID.randomUUID().toString().toUpperCase();
	}

	private final Action action;

	/**
	 * Instantiates a new entry dialog.
	 *
	 * @param action the action
	 */
	public EntryDialog(final Action action) {
		this.action = action;
	}

	/**
	 * Edits the entry.
	 *
	 * @param dialog    the dialog
	 * @param tableItem the table item
	 */
	private void editEntry(final Shell dialog, final TableItem tableItem) {
		final var child = dialog.getChildren();
		final var index = getColumnIndexNumbers(action.getCData());
		final var textFields = new String[index.length];

		for (var i = 0; i < textFields.length; i++)
			textFields[index[i]] = ((Text) child[i * 2]).getText();

		if (tableItem != null) {
			final var items = new String[textFields.length];

			for (var j = 0; j < items.length; j++)
				items[j] = tableItem.getText(j);

			if (isEqual(items, textFields)) {
				dialog.close();
				return;
			}
		}

		if (!isEmpty(textFields[2]) || !isEmpty(textFields[4])) {
			editEntry(tableItem, textFields, ((Group) child[16]).getChildren());
			dialog.close();
		} else
			child[4].setFocus();
	}

	/**
	 * Edits the entry.
	 *
	 * @param tableItem     the table item
	 * @param textFields    the textFields
	 * @param groupChildren the children of the group
	 */
	private void editEntry(final TableItem tableItem, final String[] textFields, final Control[] groupChildren) {
		final var cData = action.getCData();
		final var table = action.getTable();

		final boolean[] selection = { false, false, false, false, false };

		for (var j = 0; j < selection.length; j++)
			selection[j] = !((Button) groupChildren[j]).getSelection();

		if (selection[0] && selection[1] && selection[2] && selection[3] && selection[4])
			for (var k = 0; k < selection.length - 1; k++)
				((Button) groupChildren[k]).setSelection(true);

		if (isEmpty(textFields[5]))
			textFields[5] = RandomPassword.generate(action, groupChildren);
		else if (textFields[4].equals(textFields[5]))
			msg(action.getShell(), SWT.ICON_WARNING | SWT.OK, cData.titleWar, cData.warnUPeq);

		if (!isEmpty(textFields[6]))
			textFields[6] = textFields[6].replaceAll(System.lineSeparator(), cData.newLine);

		if (action.getList().isVisible()) {
			if (tableItem == null) {
				action.resetGroupList();
				new TableItem(table, SWT.NONE).setText(textFields);
			} else {
				final var uuid = tableItem.getText(0);
				action.resetGroupList();

				for (final var item : table.getItems())
					if (uuid.equals(item.getText(0))) {
						item.setText(textFields);
						break;
					}
			}
		} else if (tableItem == null)
			new TableItem(table, SWT.NONE).setText(textFields);
		else
			table.getItem(table.getSelectionIndex()).setText(textFields);

		textFields[5] = null;
		cData.setData(action.cryptData(action.extractData(), true));
		cData.setModified(true);
		action.colorURL();
		action.enableItems();
		action.fillGroupList();
		action.resizeColumns();
		action.setText();
		table.redraw();
	}

	/**
	 * Open.
	 *
	 * @param editLine the edit line
	 */
	public void open(final int editLine) {
		final var cData = action.getCData();
		final var newEntry = editLine < 0;

		if (cData.isLocked() || cData.isCustomHeader() || (newEntry && cData.isReadOnly()))
			return;

		SearchDialog.close();

		final var shell = action.getShell();
		final var image = getImage(shell.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout(3, false);
		layout.marginBottom = 20;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 8;

		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE, image, layout, null);
		final var uuid = newText(dialog, SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrGrou);
		final var group = newText(dialog, SWT.BORDER | SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrTitl);
		final var title = newText(dialog, SWT.BORDER | SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrLink);
		final var url = newText(dialog, SWT.BORDER | SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrUser);
		final var user = newText(dialog, SWT.BORDER | SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrPass);
		final var pwd = newText(dialog, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);

		newLabel(dialog, SWT.HORIZONTAL, cData.entrNote);
		final var notes = newText(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);

		emptyLabel(dialog);

		final var pwdStrength = group(dialog, new GridLayout(), cData.entrPInd);
		final var pwdStrengthLabel = newLabel(pwdStrength, SWT.HORIZONTAL, cData.passShor);
		pwdStrengthLabel.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
		pwdStrengthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		pwd.addModifyListener(e -> evalPasswordStrength(cData, pwdStrengthLabel, pwd.getTextChars()));

		emptyLabel(dialog);

		final var random = group(dialog, new GridLayout(4, false), cData.entrRand);
		newButton(random, true, cData.rTextLoC);
		newButton(random, true, cData.rTextUpC);
		newButton(random, true, cData.rNumbers);
		newButton(random, true, cData.rSpecia1);
		newButton(random, false, cData.rSpecia2);
		newButton(random, false, cData.entrSpac);

		emptyLabel(random);
		emptyLabel(random);
		emptyLabel(random);
		emptyLabel(random);

		newLabel(random, SWT.HORIZONTAL, cData.entrLgth);
		spinner(random, 20, ConfData.PASSWORD_MIN_LENGTH, 64, 0, 1, 4);

		final var genBtn = newButton(random, SWT.PUSH, widgetSelectedAdapter(e -> {
			pwd.setText(RandomPassword.generate(action, random.getChildren()));
		}), cData.entrGene);

		emptyLabel(dialog);

		newButton(dialog, SWT.CHECK, widgetSelectedAdapter(e -> {
			if (pwd.getEchoChar() == cData.nullChr)
				pwd.setEchoChar(cData.echoChr);
			else
				pwd.setEchoChar(cData.nullChr);
		}), cData.entrShow);

		emptyLabel(dialog);
		emptyLabel(dialog);
		emptyLabel(dialog);

		final var okBtn = newButton(dialog, SWT.PUSH, null, cData.entrOkay);
		var data = new GridData(SWT.END, SWT.TOP, true, false, 2, 1);
		data.widthHint = 80;
		okBtn.setLayoutData(data);
		dialog.setDefaultButton(okBtn);

		final var clBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrCanc);
		data = new GridData(SWT.LEAD, SWT.TOP, true, false);
		data.widthHint = 80;
		clBtn.setLayoutData(data);

		if (newEntry) {
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, null)));
			dialog.setText(cData.entrNewe);
			uuid.setText(getUUID());
		} else {
			final var item = action.getTable().getItem(editLine);
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, item)));

			final var index = getColumnIndexNumbers(cData);
			uuid.setText(item.getText(index[0]));

			if (isEmpty(uuid.getText()))
				uuid.setText(getUUID());

			group.setText(item.getText(index[1]));
			title.setText(item.getText(index[2]));
			url.setText(item.getText(index[3]));
			user.setText(item.getText(index[4]));
			pwd.setText(item.getText(index[5]));
			notes.setText(item.getText(index[6]));

			if (cData.isReadOnly()) {
				group.setEditable(false);
				title.setEditable(false);
				url.setEditable(false);
				user.setEditable(false);
				pwd.setEditable(false);
				notes.setEditable(false);
				genBtn.setEnabled(false);
				dialog.setText(cData.entrView);
			} else
				dialog.setText(cData.entrEdit);
		}

		image.dispose();
		dialog.setSize(564, 546);
		dialog.open();
		group.selectAll();
		title.selectAll();
		url.selectAll();
		user.selectAll();
		pwd.selectAll();
		title.setFocus();
	}
}
