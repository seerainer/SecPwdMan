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

import java.util.ArrayList;
import java.util.List;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.CharsetUtil;

/**
 * CSVParser is a utility class for parsing CSV data from byte arrays or
 * character arrays. It supports various configurations and options for handling
 * CSV fields, records, and line endings.
 */
public class CSVParser implements StringConstants, PrimitiveConstants {

    private final CSVConfiguration config;

    private final CSVParsingOptions options;

    private char[] charBuffer;

    private int charBufferPosition;

    /**
     * Constructs a CSVParser with the specified configuration and parsing options.
     *
     * @param config  the CSV configuration
     * @param options the parsing options
     */
    public CSVParser(final CSVConfiguration config, final CSVParsingOptions options) {
	this.config = config;
	this.options = options;
	this.charBuffer = new char[config.getInitialBufferSize()];
	this.charBufferPosition = 0;
    }

    private void addCurrentField(final List<CSVFieldInfo> fields, final boolean wasQuoted, final int startPos,
	    final int endPos, final int columnIndex) {
	var fieldValue = new String(charBuffer, 0, charBufferPosition);

	final var isEmpty = fieldValue.isEmpty();
	var isNull = false;

	// Handle null value representation
	if ((options.getNullValueRepresentation() != null && options.getNullValueRepresentation().equals(fieldValue))
		|| isEmpty) {
	    isNull = true;
	    fieldValue = empty;
	}

	final var fieldInfo = new CSVFieldInfo(fieldValue, wasQuoted, isEmpty, isNull, startPos, endPos, columnIndex);

	fields.add(fieldInfo);
	resetCharBuffer();
    }

    private void appendToCharBuffer(final char ch) throws CSVParseException {
	if (charBufferPosition >= config.getMaxFieldSize()) {
	    throw new CSVParseException(fieldSizeMax + config.getMaxFieldSize(), -1, -1);
	}
	if (charBufferPosition >= charBuffer.length) {
	    expandCharBuffer();
	}
	charBuffer[charBufferPosition++] = ch;
    }

    private void expandCharBuffer() {
	final var newSize = Math.min(charBuffer.length * 2, config.getMaxFieldSize());
	final var newBuffer = new char[newSize];
	System.arraycopy(charBuffer, 0, newBuffer, 0, charBufferPosition);
	charBuffer = newBuffer;
    }

    private boolean isLineEnding(final char ch) {
	if (options.getCustomLineEndings() == null) {
	    return ch == LF || ch == CR;
	}
	for (final char ending : options.getCustomLineEndings()) {
	    if (ch == ending) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Parses a byte array containing CSV data into a list of CSVRecord objects.
     *
     * @param data the byte array containing CSV data
     * @return a list of CSVRecord objects parsed from the byte array
     * @throws CSVParseException if there is an error during parsing
     */
    public List<CSVRecord> parseByteArray(final byte[] data) throws CSVParseException {
	final var chars = CharsetUtil.toChars(data);
	final List<CSVRecord> records = new ArrayList<>();

	var lineNumber = 1;
	var position = 0;
	while (position < chars.length) {
	    final var result = parseRecord(chars, position, lineNumber);

	    // Handle different line types based on options
	    if (result.record == null) {
		position = result.nextPosition;
		lineNumber = result.nextLineNumber;
		continue;
	    }

	    // Add record if it meets criteria
	    if (result.record != null) {
		records.add(result.record);
	    }

	    position = result.nextPosition;
	    lineNumber = result.nextLineNumber;
	}

	return records;
    }

    private FieldParseResult parseFieldCharacter(final char currentChar, final int position, final ParseState state,
	    final boolean wasQuoted, final int lineNumber) throws CSVParseException {
	switch (state) {
	case FIELD_START:
	    if (currentChar == config.getQuote()) {
		return new FieldParseResult(ParseState.IN_QUOTED_FIELD, true, position + 1, false, null);
	    } else if (currentChar == config.getDelimiter()) {
		return new FieldParseResult(ParseState.FIELD_START, false, position + 1, true, null);
	    } else if (Character.isWhitespace(currentChar)) {
		return new FieldParseResult(ParseState.FIELD_START, false, position + 1, false, null);
	    } else {
		appendToCharBuffer(currentChar);
		return new FieldParseResult(ParseState.IN_FIELD, false, position + 1, false, null);
	    }
	case IN_FIELD:
	    if (currentChar == config.getDelimiter()) {
		return new FieldParseResult(ParseState.FIELD_START, wasQuoted, position + 1, true, null);
	    } else if (currentChar == config.getQuote()) {
		if (options.isAllowUnescapedQuotesInFields()) {
		    appendToCharBuffer(currentChar);
		    return new FieldParseResult(ParseState.IN_FIELD, wasQuoted, position + 1, false, null);
		}
		return new FieldParseResult(state, wasQuoted, position + 1, false, unexpectedQuote + position);
	    } else {
		appendToCharBuffer(currentChar);
		return new FieldParseResult(ParseState.IN_FIELD, wasQuoted, position + 1, false, null);
	    }
	case IN_QUOTED_FIELD:
	    if (currentChar == config.getQuote()) {
		return new FieldParseResult(ParseState.QUOTE_IN_QUOTED_FIELD, wasQuoted, position + 1, false, null);
	    }
	    appendToCharBuffer(currentChar);
	    return new FieldParseResult(ParseState.IN_QUOTED_FIELD, wasQuoted, position + 1, false, null);
	case QUOTE_IN_QUOTED_FIELD:
	    if (currentChar == config.getQuote() && config.getEscape() == config.getQuote()) {
		// Escaped quote
		appendToCharBuffer(currentChar);
		return new FieldParseResult(ParseState.IN_QUOTED_FIELD, wasQuoted, position + 1, false, null);
	    } else if (currentChar == config.getDelimiter()) {
		return new FieldParseResult(ParseState.FIELD_START, wasQuoted, position + 1, true, null);
	    } else if (Character.isWhitespace(currentChar)) {
		return new FieldParseResult(ParseState.FIELD_END, wasQuoted, position + 1, false, null);
	    } else if (options.isStrictQuoting()) {
		return new FieldParseResult(state, wasQuoted, position + 1, false, invalidCharAfterClose + position);
	    } else {
		// Allow characters after quotes in non-strict mode
		appendToCharBuffer(config.getQuote()); // Add the closing quote
		appendToCharBuffer(currentChar);
		return new FieldParseResult(ParseState.IN_FIELD, wasQuoted, position + 1, false, null);
	    }
	case FIELD_END:
	    if (currentChar == config.getDelimiter()) {
		return new FieldParseResult(ParseState.FIELD_START, wasQuoted, position + 1, true, null);
	    } else if (Character.isWhitespace(currentChar)) {
		return new FieldParseResult(ParseState.FIELD_END, wasQuoted, position + 1, false, null);
	    } else {
		return new FieldParseResult(state, wasQuoted, position + 1, false, invalidCharAfterQuote + position);
	    }
	default:
	    throw new CSVParseException(invalidParserState, lineNumber, position);
	}
    }

    private RecordParseResult parseRecord(final char[] chars, final int startPosition, final int lineNumber)
	    throws CSVParseException {
	final List<CSVFieldInfo> fields = new ArrayList<>();
	final List<String> errors = new ArrayList<>();

	resetCharBuffer();
	var state = ParseState.FIELD_START;
	var position = startPosition;
	var fieldStartPos = startPosition;
	var columnIndex = 0;
	var wasQuoted = false;

	while (position < chars.length) {
	    final var currentChar = chars[position];

	    // Check for line endings
	    if (isLineEnding(currentChar)) {
		// Handle end of record
		if (state == ParseState.IN_QUOTED_FIELD) {
		    // Multi-line field - continue parsing
		    appendToCharBuffer(currentChar);
		    position++;
		    if (currentChar == CR && position < chars.length && chars[position] == LF) {
			appendToCharBuffer(chars[position]);
			position++;
		    }
		    continue;
		}
		// End of record
		if (state != ParseState.FIELD_START || charBufferPosition > 0) {
		    addCurrentField(fields, wasQuoted, fieldStartPos, position, columnIndex);
		}

		// Skip line ending characters
		position++;
		if (currentChar == CR && position < chars.length && chars[position] == LF) {
		    position++;
		}

		break;
	    }

	    try {
		final var fieldResult = parseFieldCharacter(currentChar, position, state, wasQuoted, lineNumber);

		state = fieldResult.newState;
		wasQuoted = fieldResult.wasQuoted;
		position = fieldResult.nextPosition;

		if (fieldResult.fieldComplete) {
		    addCurrentField(fields, wasQuoted, fieldStartPos, position, columnIndex);
		    columnIndex++;
		    fieldStartPos = position;
		    wasQuoted = false;
		    state = ParseState.FIELD_START;
		}

		if (fieldResult.err != null) {
		    errors.add(fieldResult.err);
		}

	    } catch (final CSVParseException e) {
		errors.add(e.getMessage());
		// Try to recover
		position++;
		state = ParseState.FIELD_START;
	    }
	}

	// Create record
	CSVRecord record = null;
	if (!fields.isEmpty()) {
	    record = new CSVRecord(fields.toArray(new CSVFieldInfo[0]), lineNumber, position - startPosition,
		    !errors.isEmpty(), errors.toArray(new String[0]));
	}

	return new RecordParseResult(record, position, lineNumber + 1);
    }

    private void resetCharBuffer() {
	charBufferPosition = 0;
    }

    private enum ParseState {
	FIELD_START, IN_FIELD, IN_QUOTED_FIELD, QUOTE_IN_QUOTED_FIELD, FIELD_END, RECORD_END
    }

    private static class FieldParseResult {
	final ParseState newState;
	final boolean wasQuoted;
	final int nextPosition;
	final boolean fieldComplete;
	final String err;

	FieldParseResult(final ParseState newState, final boolean wasQuoted, final int nextPosition,
		final boolean fieldComplete, final String error) {
	    this.newState = newState;
	    this.wasQuoted = wasQuoted;
	    this.nextPosition = nextPosition;
	    this.fieldComplete = fieldComplete;
	    this.err = error;
	}
    }

    private static class RecordParseResult {
	final CSVRecord record;
	final int nextPosition;
	final int nextLineNumber;

	RecordParseResult(final CSVRecord record, final int nextPosition, final int nextLineNumber) {
	    this.record = record;
	    this.nextPosition = nextPosition;
	    this.nextLineNumber = nextLineNumber;
	}
    }
}
