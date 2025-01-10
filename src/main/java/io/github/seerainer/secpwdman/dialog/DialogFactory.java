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

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.action.FileAction;

/**
 * The class DialogFactory.
 */
public final class DialogFactory {

	/**
	 * Creates the config dialog.
	 *
	 * @param action the action
	 */
	public static void createConfigDialog(final Action action) {
		new ConfigDialog(action).open();
	}

	/**
	 * Creates the entry dialog.
	 *
	 * @param action the action
	 * @param index  the index
	 */
	public static void createEntryDialog(final Action action, final int index) {
		new EntryDialog(action).open(index);
	}

	/**
	 * Creates the info dialog.
	 *
	 * @param action the action
	 */
	public static void createInfoDialog(final Action action) {
		new InfoDialog(action).open();
	}

	/**
	 * Creates the password dialog.
	 *
	 * @param action  the fileAction
	 * @param confirm the confirm
	 */
	public static void createPasswordDialog(final FileAction action, final boolean confirm) {
		new PasswordDialog(action).open(confirm);
	}

	/**
	 * Creates the search dialog.
	 *
	 * @param action the action
	 */
	public static void createSearchDialog(final Action action) {
		new SearchDialog(action).open();
	}

	/**
	 * Creates the system dialog.
	 *
	 * @param action the action
	 */
	public static void createSystemDialog(final Action action) {
		new SystemDialog(action).open();
	}

	/**
	 * Creates the text dialog.
	 *
	 * @param action the action
	 */
	public static void createTextDialog(final Action action) {
		new TextDialog(action).open();
	}

	private DialogFactory() {
	}
}