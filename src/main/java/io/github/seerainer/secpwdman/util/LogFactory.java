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
package io.github.seerainer.secpwdman.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class LogFactory.
 */
public class LogFactory implements PrimitiveConstants, StringConstants {

	private static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(LogFactory.class.getName());
	}

	/**
	 * Configures the logging.
	 */
	public static void configureLogging() {
		final var rootLogger = LogManager.getLogManager().getLogger(empty);
		rootLogger.setLevel(Level.INFO);

		try {
			final var fileHandler = new FileHandler(logFileP, LOG_FILE_SIZE, LOG_FILES, true);
			fileHandler.setFormatter(new SimpleFormatter());
			rootLogger.addHandler(fileHandler);
		} catch (final IOException e) {
			logger.error(error, e);
		}
	}

	/**
	 * Gets the logger of the calling class.
	 *
	 * @return the logger
	 */
	public static Logger getLog() {
		return logger;
	}

	private LogFactory() {
	}
}
