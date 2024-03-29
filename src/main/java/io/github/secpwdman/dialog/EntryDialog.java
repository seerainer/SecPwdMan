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
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.newText;
import static io.github.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import io.github.secpwdman.action.EditAction;
import io.github.secpwdman.images.IMG;
import io.github.secpwdman.util.RandomPassword;

/**
 * The Class EntryDialog.
 */
public class EntryDialog {
	private final EditAction action;

	/**
	 * Instantiates a new entry dialog.
	 *
	 * @param editLine the edit line
	 * @param action   the action
	 */
	public EntryDialog(final int editLine, final EditAction action) {
		this.action = action;
		open(editLine);
	}

	/**
	 * Open.
	 *
	 * @param editLine the edit line
	 */
	private void open(final int editLine) {
		final var cData = action.getCData();
		final var newEntry = editLine < 0;

		if (cData.isLocked() || cData.isCustomHeader() || (newEntry && cData.isReadOnly()))
			return;

		final var table = action.getTable();
		final var darkTheme = cData.isDarkTheme();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		final var image = IMG.getImage(dialog.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout(3, false);
		layout.marginBottom = 20;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 8;
		dialog.setImage(image);
		dialog.setLayout(layout);
		image.dispose();

		if (darkTheme) {
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		final var uuid = newText(dialog, SWT.SINGLE);
		final var group = newText(dialog, SWT.SINGLE);

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

		new Label(dialog, SWT.NONE);

		final var pwdStrength = new Group(dialog, SWT.SHADOW_NONE);

		if (darkTheme)
			pwdStrength.setForeground(dialog.getForeground());

		pwdStrength.setLayout(new GridLayout());
		pwdStrength.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		pwdStrength.setText(cData.entrPInd);

		final var pwdStrengthLabel = newLabel(pwdStrength, SWT.HORIZONTAL, cData.passShor + cData.getPasswordMinLength());
		pwdStrengthLabel.setForeground(dialog.getDisplay().getSystemColor(SWT.COLOR_RED));
		pwdStrengthLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		pwd.addModifyListener(e -> evalPasswordStrength(cData, pwdStrengthLabel, pwd.getText()));

		new Label(dialog, SWT.NONE);

		final var random = new Group(dialog, SWT.SHADOW_NONE);

		if (darkTheme)
			random.setForeground(dialog.getForeground());

		random.setLayout(new GridLayout(4, false));
		random.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		random.setText(cData.entrRand);

		newButton(random, true, cData.rTextLoC);
		newButton(random, true, cData.rTextUpC);
		newButton(random, true, cData.rNumbers);
		newButton(random, true, cData.rSpecia1);
		newButton(random, false, cData.rSpecia2);
		newButton(random, false, cData.entrSpac);

		new Label(random, SWT.NONE);
		new Label(random, SWT.NONE);
		new Label(random, SWT.NONE);
		new Label(random, SWT.NONE);

		newLabel(random, SWT.HORIZONTAL, cData.entrLgth);
		final var spinner = spinner(random, 20, cData.getPasswordMinLength(), 64, 0, 1, 4);
		if (darkTheme)
			spinner.setForeground(random.getForeground());

		final var rand = new RandomPassword(action);
		final var genButton = newButton(random, SWT.PUSH, widgetSelectedAdapter(e -> {
			pwd.setText(rand.generate(random.getChildren()));
		}), cData.entrGene);

		new Label(dialog, SWT.NONE);

		newButton(dialog, SWT.CHECK, widgetSelectedAdapter(e -> {
			if (pwd.getEchoChar() == cData.nullChr)
				pwd.setEchoChar(cData.echoChr);
			else
				pwd.setEchoChar(cData.nullChr);
		}), cData.entrShow);

		new Label(dialog, SWT.NONE);
		new Label(dialog, SWT.NONE);
		new Label(dialog, SWT.NONE);
		new Label(dialog, SWT.NONE);

		final var okBtn = newButton(dialog, SWT.PUSH, null, cData.entrOkay);
		dialog.setDefaultButton(okBtn);

		newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrCanc);

		if (newEntry) {
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> action.editEntry(true, dialog, null)));
			dialog.setText(cData.entrNewe);
		} else {
			final var item = table.getItem(editLine);
			okBtn.addSelectionListener(widgetSelectedAdapter(e -> action.editEntry(false, dialog, item)));
			uuid.setText(item.getText(0));
			group.setText(item.getText(1));
			title.setText(item.getText(2));
			url.setText(item.getText(3));
			user.setText(item.getText(4));
			pwd.setText(item.getText(5));
			notes.setText(item.getText(6));

			if (cData.isReadOnly()) {
				title.setEditable(false);
				url.setEditable(false);
				user.setEditable(false);
				pwd.setEditable(false);
				notes.setEditable(false);
				genButton.setEnabled(false);
				dialog.setText(cData.entrView);
			} else
				dialog.setText(cData.entrEdit);
		}

		dialog.setSize(600, 550);
		dialog.open();
		title.selectAll();
		url.selectAll();
		user.selectAll();
		pwd.selectAll();
		title.setFocus();
	}
}
