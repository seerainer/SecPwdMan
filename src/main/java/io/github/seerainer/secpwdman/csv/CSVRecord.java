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

import java.util.Arrays;

import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * CSVRecord represents a single record in a CSV file, containing an array of
 * fields and the line number where the record is located.
 */
public class CSVRecord implements StringConstants {

    private final CSVFieldInfo[] fields;
    private final int lineNumber;
    private final int recordLength;
    private final boolean hadErrors;
    private final String[] errors;

    CSVRecord(final CSVFieldInfo[] fields, final int lineNumber, final int recordLength, final boolean hadErrors,
	    final String[] errors) {
	this.fields = Arrays.copyOf(fields, fields.length);
	this.lineNumber = lineNumber;
	this.recordLength = recordLength;
	this.hadErrors = hadErrors;
	this.errors = errors != null ? Arrays.copyOf(errors, errors.length) : new String[0];
    }

    int getEmptyFieldCount() {
	return (int) Arrays.stream(fields).filter(CSVFieldInfo::isEmpty).count();
    }

    String[] getErrors() {
	return Arrays.copyOf(errors, errors.length);
    }

    String getField(final int index) {
	return getFieldInfo(index).getValue();
    }

    int getFieldCount() {
	return fields.length;
    }

    CSVFieldInfo getFieldInfo(final int index) {
	if (index < 0 || index >= fields.length) {
	    throw new IndexOutOfBoundsException(
		    new StringBuilder().append(fieldIndex).append(index).append(outOfBounds).toString());
	}
	return fields[index];
    }

    public String[] getFields() {
	return Arrays.stream(fields).map(CSVFieldInfo::getValue).toArray(String[]::new);
    }

    int getLineNumber() {
	return lineNumber;
    }

    /**
     * Get count of null fields in this record
     */
    int getNullFieldCount() {
	return (int) Arrays.stream(fields).filter(CSVFieldInfo::isNull).count();
    }

    boolean hadErrors() {
	return hadErrors;
    }

    @Override
    public String toString() {
	return csvRecord.formatted(Integer.valueOf(fields.length), Integer.valueOf(lineNumber),
		Integer.valueOf(recordLength), Boolean.valueOf(hadErrors));
    }
}
