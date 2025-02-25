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
package io.github.seerainer.secpwdman.config;

import io.github.seerainer.secpwdman.util.SWTUtil;

/**
 * The interface PrimitiveConstants.
 */
public interface PrimitiveConstants {

	int ASCII_LENGTH = 127;
	int BUFFER_MIN = 64;
	int BUFFER_MAX = 0x100000;
	int BUTTON_WIDTH = SWTUtil.WIN32 ? 80 : 100;
	int CLEAR_PWD_MIN = 5;
	int CLEAR_PWD_MAX = 300;
	int COL_MIN_WIDTH = 10;
	int COL_MAX_WIDTH = 5000;
	int DEFAULT_PWD_LENGTH = 32;
	int LOG_FILE_SIZE = 0x100000;
	int LOG_FILES = 5;
	int MAX_URL_LENGTH = 2083;
	int PWD_MIN_LENGTH = 6;
	int PWD_MAX_LENGTH = 64;
	int PWD_CONFIRM_HEIGHT = 210;
	int PREF_POS_XY = 25;
	int PREF_SIZE_Y = 600;
	int SASH_FORM_WEIGHT_1 = 16;
	int SASH_FORM_WEIGHT_2 = 84;
	int SECONDS = 1000;
	int UTF8_BYTES = 3; // UTF-8 can use up to 3 bytes per char

	// Colors
	int DARK_FORE = 0xEE;
	int HEAD_BACK = 0x48;
	int HEAD_FORE = 0xDD;
	int LINK_COL1 = 0x00;
	int LINK_COL2 = 0xBB;
	int LINK_COL3 = 0xFF;
	int MENU_BACK = 0x32;
	int MENU_BORD = 0x32;
	int MENU_FORE = 0xF8;
	int TOOL_BACK = 0x64;

	char echoChr = '\u25CF';
	char nullChr = '\0';
}
