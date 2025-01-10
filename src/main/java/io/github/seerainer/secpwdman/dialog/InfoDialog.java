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

import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.widgets.Widgets.button;
import static io.github.seerainer.secpwdman.widgets.Widgets.group;
import static io.github.seerainer.secpwdman.widgets.Widgets.label;
import static io.github.seerainer.secpwdman.widgets.Widgets.link;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class InfoDialog.
 */
final class InfoDialog implements StringConstants {

	private final Action action;

	/**
	 * Instantiates a new info dialog.
	 *
	 * @param action the action
	 */
	InfoDialog(final Action action) {
		this.action = action;
	}

	void open() {
		final var cData = action.getCData();
		var layout = getLayout(1, 20, 30, 10, 50, 50, 20);
		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, layout, titleInf);

		final var info = label(dialog, SWT.HORIZONTAL, APP_NAME + space + APP_VERS + APP_INFO);
		info.setAlignment(SWT.CENTER);
		info.setFont(new Font(dialog.getDisplay(), new FontData(consFont, 10, SWT.BOLD)));
		layout = getLayout(2, 5, 5, 10, 10, 10, 10);
		layout.makeColumnsEqualWidth = true;
		final var depencies = group(dialog, layout, infoDepe);
		depencies.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		final var linkColor = cData.getLinkColor();
		link(depencies, slfAddress, linkColor, slfLink);
		link(depencies, slfLicense, linkColor, mitLink);
		link(depencies, zxcAddress, linkColor, zxcLink);
		link(depencies, zxcLicense, linkColor, mitLink);
		link(depencies, jsnAddress, linkColor, jsnLink);
		link(depencies, apaAddress, linkColor, apaLink);
		link(depencies, p4jAddress, linkColor, p4jLink);
		link(depencies, apaAddress, linkColor, apaLink);
		link(depencies, csvAddress, linkColor, csvLink);
		link(depencies, apaAddress, linkColor, apaLink);
		link(depencies, valAddress, linkColor, valLink);
		link(depencies, apaAddress, linkColor, apaLink);
		link(depencies, swtAddress, linkColor, swtLink);
		link(depencies, eplAddress, linkColor, eplLink);
		link(dialog, appAddress, linkColor, appLink, safeFont);

		final var okBtn = button(dialog, SWT.PUSH, entrOkay, widgetSelectedAdapter(e -> dialog.close()));
		final var gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridData.widthHint = 80;
		okBtn.setFocus();
		okBtn.setLayoutData(gridData);

		dialog.pack();
		dialog.setDefaultButton(okBtn);
		setCenter(dialog);
		dialog.open();
	}
}
