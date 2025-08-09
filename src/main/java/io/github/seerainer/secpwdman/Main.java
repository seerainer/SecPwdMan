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
package io.github.seerainer.secpwdman;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.ui.MainWindow;
import io.github.seerainer.secpwdman.util.LogFactory;
import io.github.seerainer.secpwdman.util.SWTUtil;

/**
 * The main class.<br>
 * Initializes the display and starts the main window.
 *
 * @author <a href="mailto:philipp@seerainer.com">Philipp Seerainer</a>
 */
public class Main implements PrimitiveConstants, StringConstants {

    private static final long startTime = System.currentTimeMillis();

    private static final Logger LOG = LogFactory.getLog();

    private static Display display;

    /**
     * The main method that initializes the display and starts the main window. It
     * measures and logs the time taken to start the application and the total
     * execution time.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
	try {
	    Display.setAppName(APP_NAME);
	    Display.setAppVersion(APP_VERS);
	    LogFactory.configureLogging();
	    System.setProperty(useSystemTheme, trueStr);

	    display = new Display();
	    if (SWTUtil.WIN32 && SWTUtil.DARK) {
		setDarkMode(display);
	    }
	    final var mainUI = new MainWindow(args);
	    final var shell = mainUI.open(display);
	    LOG.info(START_TIME, APP_NAME, Long.valueOf(System.currentTimeMillis() - startTime));
	    while (!shell.isDisposed()) {
		if (!display.readAndDispatch()) {
		    display.sleep();
		}
	    }
	} catch (final Exception e) {
	    LOG.error(ERROR, e);
	} finally {
	    if (display != null && !display.isDisposed()) {
		display.dispose();
	    }
	    LOG.info(TOTAL_TIME, APP_NAME, Long.valueOf((System.currentTimeMillis() - startTime) / SECONDS));
	}
    }

    private static void setDarkMode(final Display display) {
	display.setData(darkModeExplorerTheme, Boolean.TRUE);
	display.setData(shellTitleColoring, Boolean.TRUE);
	display.setData(menuBarBackgroundColor, SWTUtil.getColor(MENU_BACK, MENU_BACK, MENU_BACK));
	display.setData(menuBarForegroundColor, SWTUtil.getColor(MENU_FORE, MENU_FORE, MENU_FORE));
	display.setData(menuBarBorderColor, SWTUtil.getColor(MENU_BORD, MENU_BORD, MENU_BORD));
	display.setData(use_WS_BORDER, Boolean.TRUE);
	display.setData(useDarkTheme, Boolean.TRUE);
	display.setData(useDarkThemeIcons, Boolean.TRUE);
    }
}
