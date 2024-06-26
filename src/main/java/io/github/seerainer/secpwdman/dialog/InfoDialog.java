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

import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.widgets.Widgets.group;
import static io.github.seerainer.secpwdman.widgets.Widgets.link;
import static io.github.seerainer.secpwdman.widgets.Widgets.newButton;
import static io.github.seerainer.secpwdman.widgets.Widgets.newLabel;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfData;

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
		var layout = new GridLayout();
		layout.marginBottom = 10;
		layout.marginLeft = 30;
		layout.marginRight = 30;
		layout.marginTop = 20;
		layout.verticalSpacing = 30;

		final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, layout, cData.titleInf);

		final var info = newLabel(dialog, SWT.HORIZONTAL, ConfData.APP_INFO);
		info.setAlignment(SWT.CENTER);
		info.setFont(new Font(dialog.getDisplay(), new FontData("Courier New", 10, SWT.BOLD))); //$NON-NLS-1$

		layout = new GridLayout(2, true);
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.verticalSpacing = 5;

		final var depend = group(dialog, layout, cData.infoDepe);
		depend.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		final var linkColor = cData.getLinkColor();
		link(depend, cData.zxcAddress, linkColor, cData.zxcLink);
		link(depend, cData.mitAddress, linkColor, cData.mitLink);
		link(depend, cData.jsnAddress, linkColor, cData.jsnLink);
		link(depend, cData.apaAddress, linkColor, cData.apaLink);
		link(depend, cData.p4jAddress, linkColor, cData.p4jLink);
		link(depend, cData.apaAddress, linkColor, cData.apaLink);
		link(depend, cData.csvAddress, linkColor, cData.csvLink);
		link(depend, cData.apaAddress, linkColor, cData.apaLink);
		link(depend, cData.valAddress, linkColor, cData.valLink);
		link(depend, cData.apaAddress, linkColor, cData.apaLink);
		link(depend, cData.swtAddress, linkColor, cData.swtLink);
		link(depend, cData.eplAddress, linkColor, cData.eplLink);
		link(dialog, cData.appAddress, linkColor, cData.appLink, "Arial"); //$NON-NLS-1$

		final var okBtn = newButton(dialog, SWT.PUSH, widgetSelectedAdapter(e -> dialog.close()), cData.entrOkay);
		final var data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		data.widthHint = 80;
		okBtn.setFocus();
		okBtn.setLayoutData(data);

		final var point = dialog.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		dialog.setSize(point.x, point.y);
		dialog.setDefaultButton(okBtn);

		setCenter(dialog);

		dialog.open();
	}
}
