/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
 * philipp@seerainer.com
 * http://www.seerainer.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
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

import static io.github.secpwdman.widgets.Widgets.msg;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newText;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
//import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import io.github.secpwdman.action.Action;

/**
 * The Class LicenseDialog.
 */
public class LicenseDialog {
	private final Action action;

	/**
	 * Instantiates a new license dialog.
	 *
	 * @param action the action
	 */
	public LicenseDialog(final Action action) {
		this.action = action;
		open();
	}

	/**
	 * Open.
	 */
	private void open() {
		final var cData = action.getCData();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final var layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		dialog.setLayout(layout);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		final var text = newText(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		text.setEditable(false);
		if (cData.getLicense() == null)
			try {
				final var uri = URI.create(cData.licenseUrl);
				final var request = HttpRequest.newBuilder(uri).build();
				cData.setLicense(HttpClient.newHttpClient().send(request, BodyHandlers.ofString()).body());
			} catch (IOException | InterruptedException e) {
				msg(dialog, SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
				dialog.dispose();
				return;
			}
		text.setText(cData.getLicense());

		dialog.setDefaultButton(newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrOkay));
		final var data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		data.widthHint = 80;
		dialog.getDefaultButton().setLayoutData(data);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, 500);
		dialog.setText(cData.licenseD);
		dialog.open();
	}
}
