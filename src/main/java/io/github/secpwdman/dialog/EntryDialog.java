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
import static io.github.secpwdman.util.Util.getImage;
import static io.github.secpwdman.util.Util.getUUID;
import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.isEqual;
import static io.github.secpwdman.widgets.Widgets.emptyLabel;
import static io.github.secpwdman.widgets.Widgets.group;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.shell;
import static io.github.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.images.IMG;
import io.github.secpwdman.io.IO;
import io.github.secpwdman.util.RandomPassword;

/**
 * The Class EntryDialog.
 */
public class EntryDialog {
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
		final var uuid = ((Text) child[0]).getText();
		final var group = ((Text) child[2]).getText();
		final var title = ((Text) child[4]).getText();
		final var url = ((Text) child[6]).getText();
		final var user = ((Text) child[8]).getText();
		final var pass = ((Text) child[10]).getText();
		final var notes = ((Text) child[12]).getText();
		final var textFields = new String[] { uuid, group, title, url, user, pass, notes };

		if (tableItem != null) {
			final var items = new String[textFields.length];
			for (var i = 0; i < items.length; i++)
				items[i] = tableItem.getText(i);

			if (isEqual(items, textFields)) {
				dialog.close();
				return;
			}
		}

		if (!isEmpty(textFields[2]) || !isEmpty(textFields[4])) {
			editEntry(tableItem, textFields, ((Group) child[16]).getChildren());
			dialog.close();
		} else
			((Text) child[4]).setFocus();
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

		cData.setData(action.cryptData(IO.extractData(cData, table), true));
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

		final var search = SearchDialog.getDialog();

		if (search != null && !search.isDisposed())
			search.close();

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
		pwdStrength.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		final var pwdStrengthLabel = newLabel(pwdStrength, SWT.HORIZONTAL, cData.passShor + cData.getPasswordMinLength());
		pwdStrengthLabel.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
		pwdStrengthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		pwd.addModifyListener(e -> evalPasswordStrength(cData, pwdStrengthLabel, pwd.getTextChars()));

		emptyLabel(dialog);

		final var random = group(dialog, new GridLayout(4, false), cData.entrRand);
		random.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

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
		spinner(random, 20, cData.getPasswordMinLength(), 64, 0, 1, 4);

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
		emptyLabel(dialog);

		final var okBtn = newButton(dialog, SWT.PUSH, null, cData.entrOkay);
		dialog.setDefaultButton(okBtn);

		newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrCanc);

		if (newEntry) {
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, null)));
			dialog.setText(cData.entrNewe);
			uuid.setText(getUUID());
		} else {
			final var item = action.getTable().getItem(editLine);
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> editEntry(dialog, item)));

			final var id = item.getText(0);
			if (isEmpty(id))
				uuid.setText(getUUID());
			else
				uuid.setText(id);

			group.setText(item.getText(1));
			title.setText(item.getText(2));
			url.setText(item.getText(3));
			user.setText(item.getText(4));
			pwd.setText(item.getText(5));
			notes.setText(item.getText(6));

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
		dialog.setSize(600, 580);
		dialog.open();
		group.selectAll();
		title.selectAll();
		url.selectAll();
		user.selectAll();
		pwd.selectAll();
		title.setFocus();
	}
}
