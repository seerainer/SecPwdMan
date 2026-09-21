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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
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
	    restrictLogPermissions();
	} catch (final IOException e) {
	    getLog(LogFactory.class).error(ERROR, e);
	}
    }

    private static void restrictLogPermissions() {
	try {
	    final var logPath = Path
		    .of(logFileP.replace("%h", System.getProperty("user.home")).replace("%g", "0").replace("%u", "0"));
	    final var dir = logPath.getParent();
	    if (dir != null && Files.exists(dir)) {
		try {
		    Files.setPosixFilePermissions(dir, PosixFilePermissions.fromString("rwx------"));
		} catch (final UnsupportedOperationException _) {
		    final var f = dir.toFile();
		    f.setReadable(false, false);
		    f.setWritable(false, false);
		    f.setExecutable(false, false);
		    f.setReadable(true, true);
		    f.setWritable(true, true);
		    f.setExecutable(true, true);
		}
	    }
	} catch (final Exception _) {
	    // Best effort only; logging must never fail startup.
	}
    }

    /**
     * Gets the logger of the given class. Call sites pass their own class so log
     * records carry class context instead of a single shared name.
     *
     * @param clazz the class requesting the logger
     * @return the logger
     */
    public static Logger getLog(final Class<?> clazz) {
	return LoggerFactory.getLogger(clazz.getName());
    }
}
