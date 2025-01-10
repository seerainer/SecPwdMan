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

import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.setCenter;
import static io.github.seerainer.secpwdman.widgets.Widgets.shell;
import static io.github.seerainer.secpwdman.widgets.Widgets.table;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;

/**
 * The class SystemDialog.
 */
final class SystemDialog implements CryptoConstants, Icons, StringConstants {

	private static final class TablePopulator {

		private static void populateAlgorithms(final Table tbl, final String type) {
			Arrays.stream(Security.getProviders()).flatMap(provider -> provider.getServices().stream())
					.filter(service -> type.equals(service.getType())).map(Provider.Service::getAlgorithm).toList()
					.forEach(algorithm -> populateTable(tbl, type, algorithm));
		}

		private static void populateEnvironmentVariables(final Table tbl) {
			populateTable(tbl, System.getenv());
		}

		private static void populateSystemProperties(final Table tbl) {
			populateTable(tbl, System.getProperties().entrySet().stream()
					.collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue()))));
		}

		private static void populateTable(final Table tbl, final Map<String, String> map) {
			map.forEach((name, value) -> populateTable(tbl, name, value));
		}

		private static void populateTable(final Table tbl, final String name, final String value) {
			new TableItem(tbl, SWT.NONE).setText(new String[] { name, value });
		}

		private TablePopulator() {
		}
	}

	private final Action action;

	SystemDialog(final Action action) {
		this.action = action;
	}

	void open() {
		final var shell = action.getShell();
		final var image = getImage(shell.getDisplay(), APP_ICON);
		final var dialog = shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE, image, getLayout(),
				systInfo);
		final var tbl = table(dialog);
		final var col1 = new TableColumn(tbl, SWT.LEAD, 0);
		final var col2 = new TableColumn(tbl, SWT.LEAD, 1);

		TablePopulator.populateSystemProperties(tbl);
		TablePopulator.populateEnvironmentVariables(tbl);
		TablePopulator.populateTable(tbl, empty, empty);
		TablePopulator.populateAlgorithms(tbl, cipher);
		TablePopulator.populateAlgorithms(tbl, keyStore);
		TablePopulator.populateAlgorithms(tbl, mac);
		TablePopulator.populateAlgorithms(tbl, messageDigest);
		TablePopulator.populateAlgorithms(tbl, signature);

		col1.pack();
		col2.pack();

		setCenter(dialog);
		image.dispose();
		dialog.open();
	}
}
