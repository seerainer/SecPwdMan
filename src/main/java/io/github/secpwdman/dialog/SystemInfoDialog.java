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

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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
		final var dialog = new Shell(action.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		final var image = IMG.getImage(dialog.getDisplay(), IMG.APP_ICON);
		final var layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		dialog.setImage(image);
		dialog.setLayout(layout);
		image.dispose();

		final var table = new Table(dialog, SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);

		if (cData.isDarkTheme()) {
			final var parent = action.getTable();
			table.setBackground(parent.getBackground());
			table.setForeground(parent.getForeground());
		}

		final var col1 = new TableColumn(table, SWT.LEFT, 0);
		final var col2 = new TableColumn(table, SWT.LEFT, 1);

		final var properties = System.getProperties();
		for (final Enumeration<?> e = properties.keys(); e.hasMoreElements();) {
			final var item = new TableItem(table, SWT.NONE);
			final Object key = e.nextElement();
			item.setText(new String[] { (String) key, (String) properties.get(key) });
		}

		final var env = System.getenv();
		for (final String envName : env.keySet())
			new TableItem(table, SWT.NONE).setText(new String[] { envName, env.get(envName) });

		col1.pack();
		col2.pack();

		dialog.setText(cData.systInfo);
		dialog.open();
	}
}
