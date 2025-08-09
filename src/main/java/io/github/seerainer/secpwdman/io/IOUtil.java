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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.util.Util.isBlank;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.eclipse.swt.SWT;
import org.slf4j.Logger;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class IOUtil.
 */
public class IOUtil implements PrimitiveConstants, StringConstants {

    private static final Logger LOG = LogFactory.getLog();

    private IOUtil() {
    }

    static byte[] deflate(final byte[] input) {
	final var deflater = new Deflater();
	deflater.setInput(input);
	deflater.setLevel(Deflater.BEST_COMPRESSION);
	deflater.finish();

	final var outputStream = new ByteArrayOutputStream();
	final var buffer = new byte[MEMORY_SIZE];

	while (!deflater.finished()) {
	    outputStream.write(buffer, 0, deflater.deflate(buffer));
	}
	deflater.end();
	clear(input);
	return outputStream.toByteArray();
    }

    private static String getConfigFilePath() {
	final var userDir = getPath(System.getProperty(userHome), fstop + APP_NAME);
	if (!Files.exists(userDir)) {
	    try {
		Files.createDirectory(userDir);
	    } catch (final IOException e) {
		LOG.error(ERROR, e);
		return confFile;
	    }
	}
	return userDir.resolve(confFile).toAbsolutePath().toString();
    }

    /**
     * Get the absolute pathname.
     *
     * @param file the string file
     * @return absolutePath
     */
    public static String getFilePath(final String file) {
	return getPath(file).toAbsolutePath().toString();
    }

    /**
     * Get the path from the file.
     *
     * @param file the file
     * @return the path
     */
    public static Path getPath(final String... file) {
	return switch (file.length) {
	case 1 -> Path.of(file[0]);
	default -> Path.of(file[0], file[1]);
	};
    }

    static byte[] inflate(final byte[] input) {
	final var inflater = new Inflater();
	inflater.setInput(input);

	final var outputStream = new ByteArrayOutputStream();
	final var buffer = new byte[MEMORY_SIZE];
	try {
	    while (!inflater.finished()) {
		outputStream.write(buffer, 0, inflater.inflate(buffer));
	    }
	    return outputStream.toByteArray();
	} catch (final DataFormatException e) {
	    LOG.error(ERROR, e);
	    return input;
	} finally {
	    inflater.end();
	    clear(input);
	}
    }

    /**
     * Checks if a file is not empty and readable.
     *
     * @param filePath the file path
     * @return true if the file is ready, false otherwise
     */
    public static boolean isFileReady(final String filePath) {
	return !isBlank(filePath) && isReadable(filePath);
    }

    /**
     * Opens the file to test if it's a password file.
     *
     * @param file the file
     * @return true if the file has the correct format
     */
    public static boolean isPasswordFile(final String file) {
	try (final var is = IO.open(file)) {
	    return JsonUtil.hasCorrectFileFormat(is);
	} catch (final IllegalArgumentException | IOException | JsonParserException e) {
	    LOG.warn(WARN, e);
	    return false;
	}
    }

    /**
     * Checks if a file is readable.
     *
     * @param filePath the file path
     * @return true if the file is readable, false otherwise
     */
    public static boolean isReadable(final String filePath) {
	final var f = getPath(filePath);
	return Files.exists(f) && Files.isReadable(f) && Files.isRegularFile(f) && Files.isWritable(f);
    }

    /**
     * Opens the configuration file.
     *
     * @param action the action
     */
    public static void openConfig(final Action action) {
	final var conf = getConfigFilePath();
	if (!isReadable(conf)) {
	    LOG.info(NO_SETTINGS_FILE);
	    return;
	}
	var exMsg = empty;
	try (final var is = IO.open(conf)) {
	    JsonUtil.setJsonConfig(action, is);
	    return;
	} catch (final IOException e) {
	    LOG.error(ERROR, e);
	    exMsg = errorInp.formatted(conf);
	} catch (final JsonParserException e) {
	    LOG.warn(WARN, e);
	    exMsg = errorImp.formatted(conf);
	}
	msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, exMsg);
    }

    /**
     * Saves the configuration file.
     *
     * @param action the action
     */
    public static void saveConfig(final Action action) {
	try {
	    IO.save(getConfigFilePath(), JsonUtil.getJsonConfig(action));
	} catch (final IOException e) {
	    LOG.error(ERROR, e);
	    msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, errorInp.formatted(confFile));
	}
    }
}
