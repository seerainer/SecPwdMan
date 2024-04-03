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

import static io.github.secpwdman.util.Util.getImage;
import static io.github.secpwdman.util.Util.setCenter;
import static io.github.secpwdman.widgets.Widgets.newTable;
import static io.github.secpwdman.widgets.Widgets.shell;

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import io.github.secpwdman.action.Action;
import io.github.secpwdman.images.IMG;

/**
 * The Class SystemInfoDialog.
 */
public class SystemInfoDialog {
	private final Action action;

	/**
	 * Instantiates a new system info dialog.
	 *
	 * @param action the action
	 */
	public SystemInfoDialog(final Action action) {
		this.action = action;
		open();
	}

	/**
	 * Open.
	 */
	private void open() {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var image = getImage(shell.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE, image, layout, cData.systInfo);
		final var sysTable = newTable(dialog);
		final var col1 = new TableColumn(sysTable, SWT.LEFT, 0);
		final var col2 = new TableColumn(sysTable, SWT.LEFT, 1);

		final var properties = System.getProperties();
		for (final Enumeration<?> e = properties.keys(); e.hasMoreElements();) {
			final var item = new TableItem(sysTable, SWT.NONE);
			final Object key = e.nextElement();
			item.setText(new String[] { (String) key, (String) properties.get(key) });
		}

		final var env = System.getenv();
		for (final String envName : env.keySet())
			new TableItem(sysTable, SWT.NONE).setText(new String[] { envName, env.get(envName) });

		col1.pack();
		col2.pack();

		setCenter(dialog);

		image.dispose();
		dialog.open();
	}
}
