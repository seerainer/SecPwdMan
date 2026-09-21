/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.BUTTON_WIDTH;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_INFO;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_NAME;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_VERS;
import static io.github.seerainer.secpwdman.config.StringConstants.apaAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.apaLink;
import static io.github.seerainer.secpwdman.config.StringConstants.appAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.appLink;
import static io.github.seerainer.secpwdman.config.StringConstants.consFont;
import static io.github.seerainer.secpwdman.config.StringConstants.diaClose;
import static io.github.seerainer.secpwdman.config.StringConstants.eplAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.eplLink;
import static io.github.seerainer.secpwdman.config.StringConstants.infoDepe;
import static io.github.seerainer.secpwdman.config.StringConstants.jsnAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.jsnLicense;
import static io.github.seerainer.secpwdman.config.StringConstants.jsnLink;
import static io.github.seerainer.secpwdman.config.StringConstants.logical;
import static io.github.seerainer.secpwdman.config.StringConstants.mitLink;
import static io.github.seerainer.secpwdman.config.StringConstants.p4jAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.p4jLink;
import static io.github.seerainer.secpwdman.config.StringConstants.safeFont;
import static io.github.seerainer.secpwdman.config.StringConstants.slfAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.slfLicense;
import static io.github.seerainer.secpwdman.config.StringConstants.slfLink;
import static io.github.seerainer.secpwdman.config.StringConstants.space;
import static io.github.seerainer.secpwdman.config.StringConstants.swtAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.swtLink;
import static io.github.seerainer.secpwdman.config.StringConstants.titleInf;
import static io.github.seerainer.secpwdman.config.StringConstants.zxcAddress;
import static io.github.seerainer.secpwdman.config.StringConstants.zxcLicense;
import static io.github.seerainer.secpwdman.config.StringConstants.zxcLink;
import static io.github.seerainer.secpwdman.ui.Widgets.button;
import static io.github.seerainer.secpwdman.ui.Widgets.group;
import static io.github.seerainer.secpwdman.ui.Widgets.label;
import static io.github.seerainer.secpwdman.ui.Widgets.link;
import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.util.SWTUtil.disposeOnExit;
import static io.github.seerainer.secpwdman.util.SWTUtil.getFont;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import io.github.seerainer.secpwdman.action.Action;

/**
 * The record InfoDialog.
 */
record InfoDialog(Action action) {

    Shell open() {
	final var cData = action.getCData();
	var layout = getLayout(1, 20, 30, 10, 40, 40, 20);
	final var dialog = shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, layout, titleInf);
	final var display = dialog.getDisplay();
	final var sb = new StringBuilder();
	sb.append(APP_NAME).append(space).append(APP_VERS).append(APP_INFO);
	final var info = label(dialog, SWT.HORIZONTAL, sb.toString());
	info.setAlignment(SWT.CENTER);
	final var infoFont = getFont(display, consFont, 12, SWT.BOLD);
	info.setFont(infoFont);
	disposeOnExit(dialog, infoFont);
	info.setLayoutData(getGridData(SWT.CENTER, SWT.CENTER, 0, 0));
	layout = getLayout(2, 5, 5, 10, 10, 10, 10);
	layout.makeColumnsEqualWidth = true;
	final var depencies = group(dialog, layout, infoDepe);
	depencies.setLayoutData(getGridData(SWT.CENTER, SWT.CENTER, 0, 0));
	sb.setLength(0);

	final var linkColor = cData.getLinkColor();
	link(depencies, slfAddress, linkColor, slfLink);
	link(depencies, slfLicense, linkColor, mitLink);
	link(depencies, zxcAddress, linkColor, zxcLink);
	link(depencies, zxcLicense, linkColor, mitLink);
	link(depencies, jsnAddress, linkColor, jsnLink);
	sb.append(mitLink.substring(0, 8)).append(logical).append(space).append(apaLink.substring(4));
	link(depencies, jsnLicense, linkColor, sb.toString());
	link(depencies, p4jAddress, linkColor, p4jLink);
	link(depencies, apaAddress, linkColor, apaLink);
	link(depencies, swtAddress, linkColor, swtLink);
	link(depencies, eplAddress, linkColor, eplLink);
	final var url = link(dialog, appAddress, linkColor, appLink);
	final var urlFont = getFont(display, safeFont, 13, SWT.BOLD);
	url.setFont(urlFont);
	disposeOnExit(dialog, urlFont);

	final var closeBtn = button(dialog, SWT.PUSH, diaClose, widgetSelectedAdapter(_ -> dialog.close()));
	final var gridData = getGridData(SWT.CENTER, SWT.CENTER, 0, 0);
	gridData.widthHint = BUTTON_WIDTH;
	closeBtn.setFocus();
	closeBtn.setLayoutData(gridData);

	dialog.pack();
	dialog.setDefaultButton(closeBtn);
	setCenter(dialog);
	dialog.open();
	return dialog;
    }
}
