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
package io.github.seerainer.secpwdman.config;

import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;

/**
 * The class PrimitiveConstants.
 */
public final class PrimitiveConstants {

    public static final int ASCII_LENGTH = 127;
    public static final int BUFFER_MIN = 64;
    public static final int BUFFER_MAX = 0x100000;
    public static final int BUTTON_WIDTH = WIN32 ? 80 : 100;
    public static final int AUTOLOCK_MIN = 1;
    public static final int AUTOLOCK_MAX = 60;
    public static final int CLEAR_PWD_MIN = 5;
    public static final int CLEAR_PWD_MAX = 300;
    public static final int COL_MIN_WIDTH = 10;
    public static final int COL_MAX_WIDTH = 5000;
    public static final int KEYSTORE_PWD_LENGTH = 32;
    public static final int MAX_FIELD_SIZE = 0x2000;
    public static final int LOG_FILE_SIZE = 0x100000;
    public static final int LOG_FILES = 5;
    public static final int MAX_FILE_SIZE = 0x1000000;
    public static final int MAX_TABLE_ENTRIES = 100000;
    public static final int MAX_URL_LENGTH = 2083;
    public static final int MEMORY_SIZE = 1024;
    public static final int PWD_DEFAULT_LENGTH = 20;
    public static final int PWD_MIN_LENGTH = 8;
    public static final int PWD_MAX_LENGTH = 64;
    public static final int PWD_CONFIRM_HEIGHT = 210;
    public static final int PREF_POS_XY = 25;
    public static final int PREF_SIZE_Y = 600;
    public static final int RAND_BUFFER_SIZE = 0x2000;
    public static final int RANDOM_PASSWORD_COUNT = 15;
    public static final int SASH_FORM_WEIGHT_1 = 16;
    public static final int SASH_FORM_WEIGHT_2 = 84;
    public static final int SECONDS = 1000;
    public static final int TEST_SIZE = 0x10000;
    public static final int WDA_EXCLUDEFROMCAPTURE = 0x11;
    // Colors
    public static final int DARK_FORE = 0xEE;
    public static final int HEAD_BACK = 0x48;
    public static final int HEAD_FORE = 0xDD;
    public static final int LINK_COL1 = 0x00;
    public static final int LINK_COL2 = 0xBB;
    public static final int LINK_COL3 = 0xFF;
    public static final int MENU_BACK = 0x32;
    public static final int MENU_BORD = 0x32;
    public static final int MENU_FORE = 0xF8;
    public static final int TABL_BACK = 0x24;
    public static final int TOOL_BACK = 0x64;
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final char DELIMITER = ',';
    public static final char ECHO_CHAR = '\u25CF';
    public static final char ESCAPE_CHAR = '"';
    public static final char NULL_CHAR = '\0';
    public static final char QUOTE_CHAR = '"';

    private PrimitiveConstants() {
	throw new UnsupportedOperationException("Class not instantiable");
    }
}
