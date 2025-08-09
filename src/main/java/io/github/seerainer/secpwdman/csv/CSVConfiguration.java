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

import io.github.seerainer.secpwdman.config.PrimitiveConstants;

/**
 * CSVConfiguration is a configuration class for CSV parsing and writing. It
 * allows customization of various parameters such as delimiter, quote
 * character, escape character, and buffer sizes.
 */
public class CSVConfiguration implements PrimitiveConstants {

    private final char delimiter;
    private final char quote;
    private final char escape;
    private final int initialBufferSize;
    private final int maxFieldSize;

    public CSVConfiguration(final Builder builder) {
	this.delimiter = builder.delimiter;
	this.escape = builder.escape;
	this.initialBufferSize = builder.initialBufferSize;
	this.maxFieldSize = builder.maxFieldSize;
	this.quote = builder.quote;
    }

    public static Builder builder() {
	return new Builder();
    }

    public char getDelimiter() {
	return delimiter;
    }

    public char getEscape() {
	return escape;
    }

    public int getInitialBufferSize() {
	return initialBufferSize;
    }

    public int getMaxFieldSize() {
	return maxFieldSize;
    }

    public char getQuote() {
	return quote;
    }

    public static class Builder {
	private char delimiter = DELIMITER;
	private char quote = QUOTE_CHAR;
	private char escape = ESCAPE_CHAR;
	private int initialBufferSize = MEMORY_SIZE;
	private int maxFieldSize = MAX_FIELD_SIZE;

	public CSVConfiguration build() {
	    return new CSVConfiguration(this);
	}

	public Builder delimiter(final char c) {
	    this.delimiter = c;
	    return this;
	}

	public Builder escape(final char c) {
	    this.escape = c;
	    return this;
	}

	public Builder initialBufferSize(final int size) {
	    this.initialBufferSize = size;
	    return this;
	}

	public Builder maxFieldSize(final int size) {
	    this.maxFieldSize = size;
	    return this;
	}

	public Builder quote(final char c) {
	    this.quote = c;
	    return this;
	}
    }
}
