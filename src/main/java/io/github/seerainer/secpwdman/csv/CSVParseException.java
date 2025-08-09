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
package io.github.seerainer.secpwdman.csv;

import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * CSVParseException is an exception class that represents errors encountered
 * during the parsing of CSV data. It includes information about the line number
 * and position in the CSV file where the error occurred.
 */
public class CSVParseException extends Exception implements StringConstants {

    private static final long serialVersionUID = 1L;

    public CSVParseException(final String message, final int lineNumber, final int position) {
	super(csvException.formatted(Integer.valueOf(lineNumber), Integer.valueOf(position), message));
    }
}
