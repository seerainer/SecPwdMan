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
package io.github.seerainer.secpwdman.util;

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LOG_FILES;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LOG_FILE_SIZE;
import static io.github.seerainer.secpwdman.config.StringConstants.ERROR;
import static io.github.seerainer.secpwdman.config.StringConstants.empty;
import static io.github.seerainer.secpwdman.config.StringConstants.logFileP;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class LogFactory.
 */
public class LogFactory {

    private static final Logger logger = LoggerFactory.getLogger(LogFactory.class.getName());

    private LogFactory() {
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
	    logger.error(ERROR, e);
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
}
