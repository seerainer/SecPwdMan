/*
 * SecPwdMan
 * Copyright (C) 2025  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.ui.Widgets.shell;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The record ProgressDialog.
 */
public record ProgressDialog(Action action) implements PrimitiveConstants, StringConstants {

    Shell open(final String title, final int maximum) {
	final var layout = getLayout(1, 10, 10, 10, 10, 10, 10);
	final var dialog = shell(action.getShell(), SWT.NO_TRIM | SWT.APPLICATION_MODAL, layout, title);
	final var progressBar = new ProgressBar(dialog, SWT.HORIZONTAL);
	progressBar.setMaximum(maximum);
	progressBar.setSelection(0);
	progressBar.setLayoutData(getGridData(SWT.FILL, SWT.CENTER, 1, 0));

	dialog.setSize(400, 45);
	setCenter(dialog);
	dialog.open();

	return dialog;
    }

    /**
     * Updates the progress.
     *
     * @param dialog   the dialog shell
     * @param progress the current progress value
     */
    public static void updateProgressDialog(final Shell dialog, final int progress) {
	if (dialog == null || dialog.isDisposed()) {
	    return;
	}
	final var progressBar = (ProgressBar) dialog.getChildren()[0];
	if (progressBar != null && !progressBar.isDisposed()) {
	    progressBar.setSelection(progress);
	}
    }
}
