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
 * Contains information about a parsed CSV field including its value and
 * metadata
 */
class CSVFieldInfo implements StringConstants {

    private final String value;
    private final boolean wasQuoted;
    private final boolean isEmpty;
    private final boolean isNull;
    private final int startPosition;
    private final int endPosition;
    private final int columnIndex;

    CSVFieldInfo(final String value, final boolean wasQuoted, final boolean isEmpty, final boolean isNull,
	    final int startPosition, final int endPosition, final int columnIndex) {
	this.value = value;
	this.wasQuoted = wasQuoted;
	this.isEmpty = isEmpty;
	this.isNull = isNull;
	this.startPosition = startPosition;
	this.endPosition = endPosition;
	this.columnIndex = columnIndex;
    }

    public int getEndPosition() {
	return endPosition;
    }

    public int getStartPosition() {
	return startPosition;
    }

    public String getValue() {
	return value;
    }

    public boolean isEmpty() {
	return isEmpty;
    }

    public boolean isNull() {
	return isNull;
    }

    @Override
    public String toString() {
	return csvField.formatted(value, Boolean.valueOf(wasQuoted), Boolean.valueOf(isEmpty), Boolean.valueOf(isNull),
		Integer.valueOf(startPosition), Integer.valueOf(endPosition), Integer.valueOf(columnIndex));
    }

    public boolean wasQuoted() {
	return wasQuoted;
    }
}
