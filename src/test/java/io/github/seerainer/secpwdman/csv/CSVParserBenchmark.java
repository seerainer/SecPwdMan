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

import java.security.SecureRandom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CSVParserBenchmark is a class for benchmarking the CSV parser performance. It
 * generates a large CSV dataset and measures the parsing time and throughput.
 */
@Tag("integration")
@DisplayName("CSV Parser Benchmark Test")
class CSVParserBenchmark {

    @Test
    @DisplayName("CSV Parser Benchmark")
    void benchmarkParser() {
	final var config = CSVConfiguration.builder().delimiter(',').quote('"').initialBufferSize(2048).build();

	final var options = CSVParsingOptions.builder().build();
	final var parser = new CSVParser(config, options);

	// Generate test data
	final var testData = generateTestData(10000, 10);

	System.out.println(new StringBuilder().append("Generated CSV with ~").append(testData.length())
		.append(" characters").toString());

	// Warm up JVM
	for (var i = 0; i < 5; i++) {
	    try {
		parser.parseByteArray(testData.getBytes());
	    } catch (final CSVParseException e) {
		e.printStackTrace();
	    }
	}

	// Benchmark
	final var startTime = System.nanoTime();
	final var iterations = 100;

	for (var i = 0; i < iterations; i++) {
	    try {
		final var records = parser.parseByteArray(testData.getBytes());
		if (i == 0) {
		    System.out.println(
			    new StringBuilder().append("Parsed ").append(records.size()).append(" records").toString());
		}
	    } catch (final CSVParseException e) {
		e.printStackTrace();
	    }
	}

	final var endTime = System.nanoTime();
	final var totalTime = endTime - startTime;
	final var avgTime = totalTime / (double) iterations / 1_000_000; // Convert to milliseconds

	System.out.printf("Average parsing time: %.2f ms%n", Double.valueOf(avgTime));
	System.out.printf("Throughput: %.2f MB/s%n",
		Double.valueOf((testData.length() / 1024.0 / 1024.0) / (avgTime / 1000.0)));
    }

    @SuppressWarnings("static-method")
    private String generateTestData(final int rows, final int columns) {
	final var sb = new StringBuilder();
	final var random = new SecureRandom();
	random.setSeed(42); // Fixed seed for reproducibility

	// Header
	for (var j = 0; j < columns; j++) {
	    if (j > 0) {
		sb.append(',');
	    }
	    sb.append("Column").append(j + 1);
	}
	sb.append('\n');

	// Data rows
	for (var i = 0; i < rows; i++) {
	    for (var j = 0; j < columns; j++) {
		if (j > 0) {
		    sb.append(',');
		}

		if (random.nextBoolean()) {
		    // Sometimes add quotes
		    sb.append('"').append("Value_").append(i).append('_').append(j);
		    if (random.nextInt(10) == 0) {
			sb.append(" with \"\"quotes\"\"");
		    }
		    sb.append('"');
		} else {
		    sb.append("Value_").append(i).append('_').append(j);
		}
	    }
	    sb.append('\n');
	}

	return sb.toString();
    }
}
