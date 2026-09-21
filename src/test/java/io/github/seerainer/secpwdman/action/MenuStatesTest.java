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
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Headless unit tests for the menu/toolbar enablement matrix. The rules used to
 * live inline in {@code Action.enableItems} behind positional
 * {@code getItem(N)} indexes, which made them untestable and silently
 * order-dependent; they now live in {@link MenuStates}.
 */
@Tag("unit")
@DisplayName("Menu Enablement Unit Tests")
class MenuStatesTest {

    private static MenuStates.State freshState() {
	return new MenuStates.State(true, false, false, false, true, true, 0, 0, false, false, false);
    }

    private static MenuStates.State unlockedPopulated() {
	return new MenuStates.State(true, true, true, false, true, true, 3, 1, true, false, true);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Rule map covers exactly the toggled items")
    void ruleMapIsComplete() {
	assertThat(MenuStates.enabled(freshState()).keySet()).containsExactlyInAnyOrder(FILE_OPEN, FILE_SAVE,
		FILE_CLOSE, FILE_CHANGE_KEY, FILE_LOCK, FILE_IMPORT, FILE_EXPORT, EDIT_NEW, EDIT_EDIT, EDIT_SELECT_ALL,
		EDIT_DELETE, EDIT_COPY_URL, EDIT_COPY_USER, EDIT_COPY_PASS, EDIT_COPY_NOTES, EDIT_OPEN_URL, FIND_SEARCH,
		VIEW_READ_ONLY, VIEW_GROUPS, VIEW_SHOW_PASS, VIEW_HIDE_PASS, VIEW_TEXT);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Fresh state only allows opening or importing")
    void freshStateRules() {
	final var states = MenuStates.enabled(freshState());
	assertThat(states.get(FILE_OPEN)).isTrue();
	assertThat(states.get(FILE_IMPORT)).isTrue();
	assertThat(states.get(VIEW_TEXT)).isTrue();
	assertThat(states.get(VIEW_GROUPS)).isTrue();
	assertThat(List.of(states.get(FILE_SAVE), states.get(FILE_CLOSE), states.get(FILE_LOCK),
		states.get(FILE_EXPORT), states.get(EDIT_NEW), states.get(EDIT_EDIT), states.get(FIND_SEARCH),
		states.get(VIEW_READ_ONLY), states.get(VIEW_SHOW_PASS), states.get(VIEW_HIDE_PASS)))
		.allMatch(Boolean.FALSE::equals);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Unlocked populated vault enables editing and copy actions")
    void unlockedPopulatedRules() {
	final var states = MenuStates.enabled(unlockedPopulated());
	assertThat(states.get(FILE_SAVE)).isTrue();
	assertThat(states.get(FILE_CLOSE)).isTrue();
	assertThat(states.get(FILE_CHANGE_KEY)).isTrue();
	assertThat(states.get(EDIT_NEW)).isTrue();
	assertThat(states.get(EDIT_EDIT)).isTrue();
	assertThat(states.get(EDIT_SELECT_ALL)).isTrue();
	assertThat(states.get(EDIT_COPY_URL)).isTrue();
	assertThat(states.get(EDIT_COPY_USER)).isTrue();
	assertThat(states.get(EDIT_COPY_PASS)).isTrue();
	assertThat(states.get(EDIT_COPY_NOTES)).isTrue();
	assertThat(states.get(EDIT_OPEN_URL)).isTrue();
	assertThat(states.get(FIND_SEARCH)).isTrue();
	assertThat(states.get(VIEW_READ_ONLY)).isTrue();
	assertThat(states.get(VIEW_TEXT)).isTrue();
	assertThat(states.get(FILE_OPEN)).isFalse();
	assertThat(states.get(FILE_IMPORT)).isFalse();
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Locked vault disables editing but keeps open enabled")
    void lockedStateRules() {
	final var states = MenuStates
		.enabled(new MenuStates.State(true, true, true, false, false, true, 3, 0, false, false, true));
	assertThat(states.get(FILE_OPEN)).isFalse();
	assertThat(states.get(FILE_CLOSE)).isTrue();
	assertThat(states.get(EDIT_NEW)).isFalse();
	assertThat(states.get(EDIT_DELETE)).isFalse();
	assertThat(states.get(VIEW_READ_ONLY)).isFalse();
	assertThat(states.get(VIEW_TEXT)).isFalse();
	assertThat(states.get(FILE_EXPORT)).isTrue();
	assertThat(states.get(EDIT_SELECT_ALL)).isTrue();
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Password column radios cross-enable on each other's selection")
    void passwordRadiosCrossEnable() {
	final var hideSelected = MenuStates
		.enabled(new MenuStates.State(true, true, false, false, true, true, 1, 0, false, true, false));
	assertThat(hideSelected.get(VIEW_SHOW_PASS)).isTrue();
	assertThat(hideSelected.get(VIEW_HIDE_PASS)).isFalse();
	final var showSelected = MenuStates
		.enabled(new MenuStates.State(true, true, false, false, true, true, 1, 0, false, false, true));
	assertThat(showSelected.get(VIEW_SHOW_PASS)).isFalse();
	assertThat(showSelected.get(VIEW_HIDE_PASS)).isTrue();
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Custom header disables header-dependent actions")
    void customHeaderRules() {
	final var states = MenuStates
		.enabled(new MenuStates.State(false, true, true, false, true, true, 2, 1, true, false, true));
	assertThat(states.get(FILE_SAVE)).isFalse();
	assertThat(states.get(FILE_LOCK)).isFalse();
	assertThat(states.get(EDIT_NEW)).isFalse();
	assertThat(states.get(EDIT_EDIT)).isFalse();
	assertThat(states.get(EDIT_COPY_URL)).isFalse();
	assertThat(states.get(VIEW_GROUPS)).isFalse();
	assertThat(states.get(FILE_CLOSE)).isTrue();
	assertThat(states.get(FILE_EXPORT)).isTrue();
	assertThat(states.get(EDIT_SELECT_ALL)).isTrue();
	assertThat(states.get(EDIT_DELETE)).isTrue();
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Non-URL selection disables open-URL but keeps copy actions")
    void nonUrlSelectionRules() {
	final var states = MenuStates
		.enabled(new MenuStates.State(true, true, true, false, true, true, 2, 1, false, false, true));
	assertThat(states.get(EDIT_OPEN_URL)).isFalse();
	assertThat(states.get(EDIT_COPY_URL)).isTrue();
	assertThat(states.get(EDIT_EDIT)).isTrue();
    }
}
