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
package io.github.seerainer.secpwdman;

import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.gui.MainWindow;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The main class.<br>
 * Initializes the display and starts the main window.
 *
 * @author <a href="mailto:philipp@seerainer.com">Philipp Seerainer</a>
 */
public final class Main implements PrimitiveConstants, StringConstants {

	private static Color menuBarBackgroundColor;
	private static Color menuBarForegroundColor;
	private static Logger logger;

	private static final long startTime = System.currentTimeMillis();

	private static Display getDisplay() {
		final var currentDisplay = Display.getCurrent();
		final var display = currentDisplay != null ? currentDisplay : Display.getDefault();
		if (DARK && WIN32) {
			setDarkMode(display);
		}
		return display;
	}

	private static Logger getLogger() {
		if (logger == null) {
			logger = LogFactory.getLog();
		}
		return logger;
	}

	/**
	 * The main method that initializes the display and starts the main window. It
	 * measures and logs the time taken to start the application and the total
	 * execution time.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(final String[] args) {
		Display.setAppName(APP_NAME);
		Display.setAppVersion(APP_VERS);
		LogFactory.configureLogging();
		System.setProperty("org.eclipse.swt.display.useSystemTheme", "true");

		final var display = getDisplay();
		final var shell = new MainWindow(args).open(display);
		getLogger().info("{} - Time to start: {} ms", APP_NAME, Long.valueOf(System.currentTimeMillis() - startTime));
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		getLogger().info("{} - Execution time: {} seconds", APP_NAME,
				Long.valueOf((System.currentTimeMillis() - startTime) / SECONDS));
		if (menuBarBackgroundColor != null) {
			menuBarBackgroundColor.dispose();
		}
		if (menuBarForegroundColor != null) {
			menuBarForegroundColor.dispose();
		}
		display.dispose();
	}

	private static void setDarkMode(final Display display) {
		menuBarBackgroundColor = new Color(display, 0x32, 0x32, 0x32);
		menuBarForegroundColor = new Color(display, 0xF8, 0xF8, 0xF8);
		display.setData("org.eclipse.swt.internal.win32.useDarkModeExplorerTheme", Boolean.TRUE);
		display.setData("org.eclipse.swt.internal.win32.useShellTitleColoring", Boolean.TRUE);
		display.setData("org.eclipse.swt.internal.win32.menuBarBackgroundColor", menuBarBackgroundColor);
		display.setData("org.eclipse.swt.internal.win32.menuBarForegroundColor", menuBarForegroundColor);
		display.setData("org.eclipse.swt.internal.win32.all.use_WS_BORDER", Boolean.TRUE);
		display.setData("org.eclipse.swt.internal.win32.Combo.useDarkTheme", Boolean.TRUE);
		display.setData("org.eclipse.swt.internal.win32.Text.useDarkThemeIcons", Boolean.TRUE);
	}
}
