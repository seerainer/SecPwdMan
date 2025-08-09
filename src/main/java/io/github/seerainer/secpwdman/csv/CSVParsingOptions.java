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

/**
 * CSVParsingOptions provides configuration options for parsing CSV files. It
 * allows customization of how empty fields, null values, quoting, and line
 * endings are handled during parsing.
 */
public class CSVParsingOptions {

    private final boolean skipEmptyLines;
    private final boolean skipBlankLines;
    private final String nullValueRepresentation;
    private final boolean strictQuoting;
    private final boolean allowUnescapedQuotesInFields;
    private final char[] customLineEndings;

    private CSVParsingOptions(final Builder builder) {
	this.skipEmptyLines = builder.skipEmptyLines;
	this.skipBlankLines = builder.skipBlankLines;
	this.nullValueRepresentation = builder.nullValueRepresentation;
	this.strictQuoting = builder.strictQuoting;
	this.allowUnescapedQuotesInFields = builder.allowUnescapedQuotesInFields;
	this.customLineEndings = builder.customLineEndings;
    }

    public static Builder builder() {
	return new Builder();
    }

    char[] getCustomLineEndings() {
	return customLineEndings;
    }

    String getNullValueRepresentation() {
	return nullValueRepresentation;
    }

    boolean isAllowUnescapedQuotesInFields() {
	return allowUnescapedQuotesInFields;
    }

    boolean isSkipBlankLines() {
	return skipBlankLines;
    }

    boolean isSkipEmptyLines() {
	return skipEmptyLines;
    }

    boolean isStrictQuoting() {
	return strictQuoting;
    }

    public static class Builder {
	private boolean skipEmptyLines = false;
	private boolean skipBlankLines = false;
	private String nullValueRepresentation = null;
	private boolean strictQuoting = true;
	private boolean allowUnescapedQuotesInFields = false;
	private char[] customLineEndings = null;

	/**
	 * Allow unescaped quotes within fields (less strict parsing)
	 */
	public Builder allowUnescapedQuotesInFields(final boolean allow) {
	    this.allowUnescapedQuotesInFields = allow;
	    return this;
	}

	public CSVParsingOptions build() {
	    return new CSVParsingOptions(this);
	}

	/**
	 * Custom line ending characters (default is \n and \r\n)
	 */
	public Builder customLineEndings(final char[] endings) {
	    this.customLineEndings = endings;
	    return this;
	}

	/**
	 * Specific representation for null values (e.g., "NULL", "\\N")
	 */
	public Builder nullValueRepresentation(final String nullRep) {
	    this.nullValueRepresentation = nullRep;
	    return this;
	}

	/**
	 * Skip lines that contain only whitespace
	 */
	public Builder skipBlankLines(final boolean skip) {
	    this.skipBlankLines = skip;
	    return this;
	}

	/**
	 * Skip completely empty lines (no characters at all)
	 */
	public Builder skipEmptyLines(final boolean skip) {
	    this.skipEmptyLines = skip;
	    return this;
	}

	/**
	 * Enforce strict quoting rules
	 */
	public Builder strictQuoting(final boolean strict) {
	    this.strictQuoting = strict;
	    return this;
	}
    }
}
