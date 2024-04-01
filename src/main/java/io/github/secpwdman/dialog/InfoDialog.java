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
import static io.github.secpwdman.widgets.Widgets.link;
import static io.github.secpwdman.widgets.Widgets.newButton;
import static io.github.secpwdman.widgets.Widgets.newLabel;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
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
		final var darkTheme = cData.isDarkTheme();
		final var linkColor = cData.getLinkColor();
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final var layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 30;
		layout.marginRight = 30;
		layout.marginTop = 20;
		layout.verticalSpacing = 30;
		dialog.setLayout(layout);

		if (darkTheme) {
			final var table = action.getTable();
			dialog.setBackground(table.getBackground());
			dialog.setForeground(table.getForeground());
			dialog.setBackgroundMode(SWT.INHERIT_FORCE);
		}

		final var info = newLabel(dialog, SWT.HORIZONTAL, ConfData.APP_INFO);
		info.setAlignment(SWT.CENTER);
		info.setFont(new Font(dialog.getDisplay(), new FontData("Courier New", 10, SWT.BOLD))); //$NON-NLS-1$

		final var depend = new Group(dialog, SWT.SHADOW_NONE);

		if (darkTheme)
			depend.setForeground(dialog.getForeground());

		final var groupLayout = new GridLayout();
		groupLayout.marginBottom = 10;
		groupLayout.marginLeft = 10;
		groupLayout.marginRight = 10;
		groupLayout.marginTop = 10;
		depend.setLayout(groupLayout);
		depend.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		depend.setText(cData.infoDepe);

		link(depend, cData.valAddress, linkColor, cData.valLink);
		link(depend, cData.zxcAddress, linkColor, cData.zxcLink);
		link(depend, cData.p4jAddress, linkColor, cData.p4jLink);
		link(depend, cData.csvAddress, linkColor, cData.csvLink);
		link(depend, cData.swtAddress, linkColor, cData.swtLink);

		link(dialog, cData.appAddress, linkColor, cData.appLink, "Arial"); //$NON-NLS-1$

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
