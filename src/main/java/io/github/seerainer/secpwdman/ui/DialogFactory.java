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

import java.util.Objects;

import org.eclipse.swt.widgets.Shell;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.action.FileAction;

/**
 * The class DialogFactory.
 */
public class DialogFactory {

    private static Shell configDialog;
    private static Shell entryDialog;
    private static Shell infoDialog;
    private static Shell passwordDialog;
    private static Shell passwordGeneratorDialog;
    private static Shell progressDialog;
    private static Shell searchDialog;
    private static Shell systemDialog;
    private static Shell textDialog;

    private DialogFactory() {
    }

    /**
     * Closes all open dialogs.
     */
    public static void closeAllDialogs() {
	closeDialog(configDialog);
	closeDialog(entryDialog);
	closeDialog(infoDialog);
	closeDialog(passwordDialog);
	closeDialog(passwordGeneratorDialog);
	closeDialog(progressDialog);
	closeDialog(searchDialog);
	closeDialog(systemDialog);
	closeDialog(textDialog);
    }

    private static void closeDialog(final Shell dialog) {
	if (Objects.nonNull(dialog) && !dialog.isDisposed()) {
	    dialog.close();
	}
    }

    /**
     * Closes the search dialog.
     */
    public static void closeSearchDialog() {
	closeDialog(searchDialog);
    }

    static void createConfigDialog(final Action action) {
	configDialog = new ConfigDialog(action).open();
    }

    static void createEntryDialog(final Action action, final int index) {
	entryDialog = new EntryDialog(action).open(index);
    }

    static void createInfoDialog(final Action action) {
	infoDialog = new InfoDialog(action).open();
    }

    /**
     * Creates a new password dialog.
     *
     * @param action  the fileAction
     * @param confirm the confirm
     */
    public static void createPasswordDialog(final FileAction action, final boolean confirm) {
	if (Objects.isNull(passwordDialog) || passwordDialog.isDisposed()) {
	    passwordDialog = new PasswordDialog(action).open(confirm);
	}
    }

    static void createPasswordGeneratorDialog(final Action action) {
	passwordGeneratorDialog = new PasswordGeneratorDialog(action).open();
    }

    public static Shell createProgressDialog(final Action action, final String title, final int maximum) {
	progressDialog = new ProgressDialog(action).open(title, maximum);
	return progressDialog;
    }

    static void createSearchDialog(final Action action) {
	if (Objects.isNull(searchDialog) || searchDialog.isDisposed()) {
	    searchDialog = new SearchDialog(action).open();
	} else {
	    searchDialog.forceActive();
	}
    }

    static void createSystemDialog(final Action action) {
	systemDialog = new SystemDialog(action).open();
    }

    static void createTextDialog(final Action action) {
	textDialog = new TextDialog(action).open();
    }
}
