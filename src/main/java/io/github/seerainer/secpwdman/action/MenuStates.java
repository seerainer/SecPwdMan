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
package io.github.seerainer.secpwdman.action;

import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_NOTES;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_USER;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_DELETE;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_EDIT;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_NEW;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_OPEN_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_SELECT_ALL;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CHANGE_KEY;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CLOSE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_EXPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_IMPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_LOCK;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_OPEN;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_SAVE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FIND_SEARCH;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_GROUPS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_HIDE_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_READ_ONLY;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_SHOW_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_TEXT;

import java.util.HashMap;
import java.util.Map;

/**
 * Pure enablement rules for menu and toolbar items, keyed by {@code MenuIds}.
 *
 * <p>
 * Extracted from {@code Action.enableItems} so the decision matrix is
 * unit-testable without an SWT display; {@code enableItems} only gathers the
 * {@link State} and applies the resulting map to looked-up widgets.
 * </p>
 */
public final class MenuStates {

    private MenuStates() {
	throw new UnsupportedOperationException("Class not instantiable");
    }

    /**
     * Computes item-id to enabled-state mappings for the given UI state.
     *
     * @param s the UI state snapshot
     * @return mutable map of enablement rules (one entry per toggled item)
     */
    public static Map<String, Boolean> enabled(final State s) {
	final var states = new HashMap<String, Boolean>();
	final var singleDefault = s.selectionCount() == 1 && s.defaultHeader();

	states.put(FILE_OPEN, Boolean.valueOf(!s.fileOpen()));
	states.put(FILE_SAVE, Boolean.valueOf(s.itemCount() > 0 && s.writeable() && s.defaultHeader()));
	states.put(FILE_CLOSE, Boolean.valueOf(s.fileOpen()));
	states.put(FILE_CHANGE_KEY, Boolean.valueOf(s.keyReady() && !s.modified() && s.unlocked() && s.writeable()));
	states.put(FILE_LOCK, Boolean.valueOf(s.fileOpen() && !s.modified() && s.defaultHeader()));
	states.put(FILE_IMPORT, Boolean.valueOf(s.itemCount() == 0 && s.unlocked() && s.writeable()));
	states.put(FILE_EXPORT, Boolean.valueOf(s.itemCount() > 0));

	states.put(EDIT_NEW, Boolean.valueOf(s.keyReady() && s.unlocked() && s.writeable() && s.defaultHeader()));
	states.put(EDIT_EDIT, Boolean.valueOf(singleDefault && s.keyReady()));
	states.put(EDIT_SELECT_ALL, Boolean.valueOf(s.itemCount() > 0));
	states.put(EDIT_DELETE, Boolean.valueOf(s.selectionCount() > 0 && s.writeable()));
	states.put(EDIT_COPY_URL, Boolean.valueOf(singleDefault));
	states.put(EDIT_COPY_USER, Boolean.valueOf(singleDefault));
	states.put(EDIT_COPY_PASS, Boolean.valueOf(singleDefault));
	states.put(EDIT_COPY_NOTES, Boolean.valueOf(singleDefault));
	states.put(EDIT_OPEN_URL, Boolean.valueOf(singleDefault && s.urlCell()));

	states.put(FIND_SEARCH, Boolean.valueOf(s.itemCount() > 1));

	states.put(VIEW_READ_ONLY, Boolean
		.valueOf(s.itemCount() > 0 && s.fileOpen() && s.unlocked() && !s.modified() && s.defaultHeader()));
	states.put(VIEW_GROUPS, Boolean.valueOf(s.defaultHeader()));
	states.put(VIEW_SHOW_PASS, Boolean.valueOf(s.hidePassSelected() && s.fileOpen() && s.defaultHeader()));
	states.put(VIEW_HIDE_PASS, Boolean.valueOf(s.showPassSelected() && s.fileOpen() && s.defaultHeader()));
	states.put(VIEW_TEXT, Boolean.valueOf(s.unlocked()));

	return states;
    }

    /**
     * Snapshot of everything the enablement rules depend on.
     */
    public record State(boolean defaultHeader, boolean fileOpen, boolean keyReady, boolean modified, boolean unlocked,
	    boolean writeable, int itemCount, int selectionCount, boolean urlCell, boolean hidePassSelected,
	    boolean showPassSelected) {
    }
}
