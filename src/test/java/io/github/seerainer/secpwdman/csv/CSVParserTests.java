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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CSVParserDemo demonstrates basic usage of the CSVParser with various
 * configurations and error handling.
 */
@Tag("integration")
@DisplayName("CSV Parser Integration Tests")
class CSVParserTests {

    @SuppressWarnings("static-method")
    private void demonstrateBasicParsing() {
	// Create configuration
	final var config = CSVConfiguration.builder().delimiter(',').quote('"').initialBufferSize(512)
		.maxFieldSize(1024 * 1024).build();

	final var options = CSVParsingOptions.builder().build();
	final var parser = new CSVParser(config, options);

	// Example 1: Parse a string
	final var csvContent = """
		Name,Age,City,Description
		"John Doe",30,"New York","A software engineer with ""extensive"" experience"
		Jane Smith,25,Boston,Marketing specialist
		"Bob Johnson",35,"San Francisco","Senior developer, team lead"
		""";

	try {
	    System.out.println("=== Parsing String ===");
	    final var records = parser.parseByteArray(csvContent.getBytes());

	    records.forEach((final CSVRecord record) -> {
		System.out.println(
			new StringBuilder().append("Line ").append(record.getLineNumber()).append(":").toString());
		for (var i = 0; i < record.getFieldCount(); i++) {
		    System.out.println(new StringBuilder().append("  Field ").append(i).append(": '")
			    .append(record.getField(i)).append("'").toString());
		}
		System.out.println();
	    });
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}

	// Example 2: Different delimiter
	final var semicolonConfig = CSVConfiguration.builder().delimiter(';').quote('"').build();
	final var semicolonParser = new CSVParser(semicolonConfig, options);
	final var semicolonCsv = "Product;Price;Category\nLaptop;999.99;Electronics\nBook;19.99;Education";

	try {
	    System.out.println("=== Parsing Semicolon Delimited ===");
	    final var records = semicolonParser.parseByteArray(semicolonCsv.getBytes());

	    records.forEach(System.out::println);
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}

	// Example 3: Error handling
	final var malformedCsv = "Name,Age\nJohn,30\n\"Unclosed quote,25";

	try {
	    System.out.println("=== Testing Error Handling ===");
	    parser.parseByteArray(malformedCsv.getBytes());
	} catch (final CSVParseException e) {
	    System.err.println("Expected parse error: " + e.getMessage());
	}
    }

    @SuppressWarnings("static-method")
    private void demonstrateEmptyValueHandling() {
	System.out.println("=== Empty Value Handling ===");

	final var csvWithEmpties = """
		Name,Age,City,Email
		John,30,,john@example.com
		,25,Boston,
		Alice,,New York,alice@example.com
		,,,"
		Bob,35,,bob@example.com
		""";

	final var config = CSVConfiguration.builder().build();

	// Preserve all empty fields
	final var preserveOptions = CSVParsingOptions.builder().skipEmptyLines(false).skipBlankLines(false).build();

	final var parser = new CSVParser(config, preserveOptions);

	try {
	    final var records = parser.parseByteArray(csvWithEmpties.getBytes());

	    records.forEach((final CSVRecord record) -> {
		System.out.printf("Line %d (Empty fields: %d):%n", Integer.valueOf(record.getLineNumber()),
			Integer.valueOf(record.getEmptyFieldCount()));

		for (var i = 0; i < record.getFieldCount(); i++) {
		    final var field = record.getFieldInfo(i);
		    System.out.printf("  Field %d: '%s' (empty=%s, quoted=%s)%n", Integer.valueOf(i), field.getValue(),
			    Boolean.valueOf(field.isEmpty()), Boolean.valueOf(field.wasQuoted()));
		}
		System.out.println();
	    });
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}
    }

    @SuppressWarnings("static-method")
    private void demonstrateErrorTolerance() {
	System.out.println("\n=== Error Tolerance ===");

	final var malformedCsv = """
		Name,Age,City
		John,30,New York
		"Unclosed quote,25,Boston
		Jane,invalid"quote,Chicago
		Bob,35,Seattle
		""";

	final var config = CSVConfiguration.builder().build();

	final var tolerantOptions = CSVParsingOptions.builder().allowUnescapedQuotesInFields(true).strictQuoting(false)
		.build();

	final var parser = new CSVParser(config, tolerantOptions);

	try {
	    final var records = parser.parseByteArray(malformedCsv.getBytes());

	    System.out.printf("Parsed %d records from malformed CSV%n", Integer.valueOf(records.size()));

	    records.forEach((final CSVRecord record) -> {
		System.out.printf("Line %d: %s", Integer.valueOf(record.getLineNumber()),
			String.join(" | ", record.getFields()));

		if (record.hadErrors()) {
		    System.out.printf(" [ERRORS: %s]", String.join("; ", record.getErrors()));
		}
		System.out.println();
	    });
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}
    }

    @SuppressWarnings("static-method")
    private void demonstrateFlexibleQuoting() {
	System.out.println("=== Flexible Quoting ===");

	final var messyCsv = """
		Name,Description,Price
		Product A,"Contains "quotes" and commas, lots of them",29.99
		Product B,Simple description,19.99
		"Product C","Fully quoted",39.99
		Product D,Has "embedded quotes without escaping,49.99
		""";

	final var config = CSVConfiguration.builder().build();

	final var flexibleOptions = CSVParsingOptions.builder().strictQuoting(false).allowUnescapedQuotesInFields(true)
		.build();

	final var parser = new CSVParser(config, flexibleOptions);

	try {
	    final var records = parser.parseByteArray(messyCsv.getBytes());

	    records.forEach((final CSVRecord record) -> {
		System.out.printf("Line %d (Errors: %s):%n", Integer.valueOf(record.getLineNumber()),
			Boolean.valueOf(record.hadErrors()));

		if (record.hadErrors()) {
		    System.out.println("  Errors: " + String.join(", ", record.getErrors()));
		}

		for (var i = 0; i < record.getFieldCount(); i++) {
		    final var field = record.getFieldInfo(i);
		    System.out.printf("  Field %d: '%s' (quoted=%s)%n", Integer.valueOf(i), field.getValue(),
			    Boolean.valueOf(field.wasQuoted()));
		}
		System.out.println();
	    });
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}
    }

    @SuppressWarnings("static-method")
    private void demonstrateLineHandling() {
	System.out.println("=== Line Handling Options ===");

	final var csvWithVariousLines = """
		Name,Age
		John,30

		Jane,25

		Bob,35
		""";

	final var config = CSVConfiguration.builder().build();

	// Test different line handling options
	final CSVParsingOptions[] optionSets = {
		CSVParsingOptions.builder().skipEmptyLines(false).skipBlankLines(false).build(),
		CSVParsingOptions.builder().skipEmptyLines(true).skipBlankLines(false).build(),
		CSVParsingOptions.builder().skipEmptyLines(false).skipBlankLines(true).build() };

	final String[] descriptions = { "Keep all lines", "Skip empty lines only", "Skip blank lines only" };

	for (var i = 0; i < optionSets.length; i++) {
	    System.out.println(new StringBuilder().append("\n").append(descriptions[i]).append(":").toString());
	    final var parser = new CSVParser(config, optionSets[i]);

	    try {
		final var records = parser.parseByteArray(csvWithVariousLines.getBytes());
		System.out.printf("  Parsed %d records%n", Integer.valueOf(records.size()));

		records.forEach((final CSVRecord record) -> System.out.printf("    Line %d: %s%n",
			Integer.valueOf(record.getLineNumber()), String.join(", ", record.getFields())));
	    } catch (final CSVParseException e) {
		System.err.println("  Parse error: " + e.getMessage());
	    }
	}
    }

    @SuppressWarnings("static-method")
    private void demonstrateNullValueHandling() {
	System.out.println("=== Null Value Handling ===");

	final var csvWithNulls = """
		Name,Age,City,Notes
		John,30,NULL,Has experience
		Jane,,Boston,NULL
		Bob,35,,"No notes"
		""";

	final var config = CSVConfiguration.builder().build();

	final var nullOptions = CSVParsingOptions.builder().nullValueRepresentation("NULL").build();

	final var parser = new CSVParser(config, nullOptions);

	try {
	    final var records = parser.parseByteArray(csvWithNulls.getBytes());

	    records.forEach((final CSVRecord record) -> {
		System.out.printf("Line %d (Null fields: %d):%n", Integer.valueOf(record.getLineNumber()),
			Integer.valueOf(record.getNullFieldCount()));

		for (var i = 0; i < record.getFieldCount(); i++) {
		    final var field = record.getFieldInfo(i);
		    System.out.printf("  Field %d: %s (null=%s, empty=%s, pos=%d-%d)%n", Integer.valueOf(i),
			    field.isNull() ? "NULL"
				    : new StringBuilder().append("'").append(field.getValue()).append("'").toString(),
			    Boolean.valueOf(field.isNull()), Boolean.valueOf(field.isEmpty()),
			    Integer.valueOf(field.getStartPosition()), Integer.valueOf(field.getEndPosition()));
		}
		System.out.println();
	    });
	} catch (final CSVParseException e) {
	    System.err.println("Parse error: " + e.getMessage());
	}
    }

    @Test
    @DisplayName("CSV Parser Test")
    void testCSVParsing() {
	demonstrateBasicParsing();
	demonstrateEmptyValueHandling();
	demonstrateNullValueHandling();
	demonstrateFlexibleQuoting();
	demonstrateLineHandling();
	demonstrateErrorTolerance();
    }
}
