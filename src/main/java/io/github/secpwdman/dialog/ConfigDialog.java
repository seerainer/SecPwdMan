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

import static io.github.secpwdman.widgets.Widgets.horizontalSeparator;
import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static io.github.secpwdman.widgets.Widgets.spinner;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.io.crypto.Crypto;

/**
 * The Class ConfigDialog.
 */
public class ConfigDialog {

	private static void cryptoTest(final ConfData cData, final Shell parent, final Spinner iter) {
		final var oldIter = cData.getIterCount();
		cData.setIterCount(iter.getSelection());

		try {
			var start = Instant.now();
			final var enc = new Crypto(cData).encrypt(new byte[1024], new char[64]);
			final var t0 = Long.valueOf(ChronoUnit.MILLIS.between(start, Instant.now()));
			start = Instant.now();
			new Crypto(cData).decrypt(enc, new char[64]);
			final var t1 = Long.valueOf(ChronoUnit.MILLIS.between(start, Instant.now()));
			final var str = String.format(cData.cfgTestI, t0, t1);
			msg(parent, SWT.ICON_INFORMATION | SWT.OK, cData.titleInf, str);
		} catch (final Exception ex) {
			msg(parent, SWT.ICON_ERROR | SWT.OK, cData.titleErr, ex.fillInStackTrace().toString());
		}

		cData.setIterCount(oldIter);
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
		final var darkTheme = cData.isDarkTheme();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final var layout = new GridLayout(3, false);
		layout.horizontalSpacing = 25;
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.verticalSpacing = 10;
		dialog.setLayout(layout);

		if (darkTheme) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgPIter);
		final var pwdIter = spinner(dialog, cData.getIterCount(), 210000, 9999999, 0, 1, 10000);
		newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> cryptoTest(cData, dialog, pwdIter)), cData.cfgTestB);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgClPwd);
		final var clearPwd = spinner(dialog, cData.getClearPasswd(), 10, 300, 0, 1, 10);
		new Label(dialog, SWT.NONE);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgColWh);
		final var columnWidth = spinner(dialog, cData.getColumnWidth(), 10, 4000, 0, 1, 10);
		new Label(dialog, SWT.NONE);

		horizontalSeparator(dialog);

		newLabel(dialog, SWT.HORIZONTAL, cData.cfgMinPl);
		final var minPwdLength = spinner(dialog, cData.getPasswordMinLength(), 6, 64, 0, 1, 4);
		new Label(dialog, SWT.NONE);

		if (darkTheme) {
			final var color = dialog.getForeground();
			pwdIter.setForeground(color);
			clearPwd.setForeground(color);
			columnWidth.setForeground(color);
			minPwdLength.setForeground(color);
		}

		new Label(dialog, SWT.NONE);
		new Label(dialog, SWT.NONE);
		new Label(dialog, SWT.NONE);

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> {
			cData.setIterCount(pwdIter.getSelection());
			cData.setClearPasswd(clearPwd.getSelection());
			cData.setColumnWidth(columnWidth.getSelection());
			cData.setPasswordMinLength(minPwdLength.getSelection());
			dialog.close();
			action.resizeColumns();
		}), cData.entrOkay));
		final var data = new GridData(SWT.CENTER, SWT.END, false, false, 3, 1);
		data.widthHint = 80;
		dialog.getDefaultButton().setLayoutData(data);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, point.y);
		dialog.setText(cData.cfgTitle);
		dialog.open();
	}
}
