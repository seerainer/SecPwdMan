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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.slf4j.Logger;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class IOUtil.
 */
public class IOUtil implements StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	private static String getConfigFilePath() {
		final var appDir = new File(System.getProperty(userHome), fstop + APP_NAME);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		return appDir.getAbsolutePath() + File.separator + confFile;
	}

	/**
	 * Get the absolute pathname.
	 *
	 * @param file the string file
	 * @return absolutePath
	 */
	public static String getFilePath(final String file) {
		return new File(file).getAbsolutePath();
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
			LOG.warn(warning, e);
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
		final var file = new File(filePath);
		return (file.exists() && file.canRead() && file.canWrite() && file.isFile());
	}

	/**
	 * Opens the configuration file.
	 *
	 * @param action the action
	 */
	public static void openConfig(final Action action) {
		final var conf = getConfigFilePath();
		if (!isReadable(conf)) {
			LOG.info(noSettingsFile);
			return;
		}
		var exMsg = empty;
		try (final var is = IO.open(conf)) {
			JsonUtil.setJsonConfig(action, is);
			return;
		} catch (final IOException e) {
			LOG.error(error, e);
			exMsg = errorFil.formatted(conf);
		} catch (final JsonParserException e) {
			LOG.warn(warning, e);
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
			LOG.error(error, e);
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, errorFil.formatted(confFile));
		}
	}

	private IOUtil() {
	}
}
