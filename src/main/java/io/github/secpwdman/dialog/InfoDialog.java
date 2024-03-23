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

import static io.github.secpwdman.util.Util.setCenter;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.config.ConfData;

/**
 * The Class InfoDialog.
 */
public class InfoDialog {
	private final Action action;

	/**
	 * Instantiates a new info dialog.
	 *
	 * @param action the action
	 */
	public InfoDialog(final Action action) {
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
		layout.marginLeft = 30;
		layout.marginRight = 30;
		layout.marginTop = 20;
		layout.verticalSpacing = 30;
		dialog.setLayout(layout);

		if (cData.isDarkTheme()) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		final var info = newLabel(dialog, SWT.HORIZONTAL, ConfData.APP_INFO);
		info.setAlignment(SWT.CENTER);

		final var link = new Link(dialog, SWT.NONE);
		link.addSelectionListener(widgetSelectedAdapter(e -> {
			Program.launch(cData.linkAddress);
			dialog.getDefaultButton().setFocus();
		}));
		link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		link.setLinkForeground(cData.getLinkColor());
		link.setText(cData.appLink);

		final var okBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrOkay);
		final var data = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 80;
		okBtn.setFocus();
		okBtn.setLayoutData(data);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, point.y);
		dialog.setLocation(setCenter(dialog));
		dialog.setDefaultButton(okBtn);
		dialog.setText(cData.titleInf);
		dialog.open();
	}
}
