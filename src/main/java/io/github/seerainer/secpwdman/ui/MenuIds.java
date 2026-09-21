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

/**
 * Stable identifiers attached to menu and toolbar items (see
 * {@link Widgets#tag}). All enablement, selection and text updates resolve
 * items through these ids instead of positional indexes, so reordering menus
 * can no longer silently retarget them.
 */
public final class MenuIds {

    public static final String MENU_FILE = "menu.file";
    public static final String MENU_EDIT = "menu.edit";
    public static final String MENU_FIND = "menu.find";
    public static final String MENU_VIEW = "menu.view";

    public static final String FILE_OPEN = "file.open";
    public static final String FILE_SAVE = "file.save";
    public static final String FILE_CLOSE = "file.close";
    public static final String FILE_CHANGE_KEY = "file.changeKey";
    public static final String FILE_LOCK = "file.lock";
    public static final String FILE_IMPORT = "file.import";
    public static final String FILE_EXPORT = "file.export";

    public static final String EDIT_NEW = "edit.new";
    public static final String EDIT_EDIT = "edit.edit";
    public static final String EDIT_SELECT_ALL = "edit.selectAll";
    public static final String EDIT_DELETE = "edit.delete";
    public static final String EDIT_COPY_URL = "edit.copyUrl";
    public static final String EDIT_COPY_USER = "edit.copyUser";
    public static final String EDIT_COPY_PASS = "edit.copyPass";
    public static final String EDIT_COPY_NOTES = "edit.copyNotes";
    public static final String EDIT_OPEN_URL = "edit.openUrl";

    public static final String FIND_SEARCH = "find.search";

    public static final String VIEW_READ_ONLY = "view.readOnly";
    public static final String VIEW_GROUPS = "view.groups";
    public static final String VIEW_RESIZE = "view.resize";
    public static final String VIEW_SHOW_PASS = "view.showPass";
    public static final String VIEW_HIDE_PASS = "view.hidePass";
    public static final String VIEW_TEXT = "view.text";

    private MenuIds() {
	throw new UnsupportedOperationException("Class not instantiable");
    }
}
