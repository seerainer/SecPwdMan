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

import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.getColor;

import org.eclipse.swt.widgets.Display;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.ui.MainWindow;
import io.github.seerainer.secpwdman.util.LogFactory;
import io.github.seerainer.secpwdman.util.SingleInstanceManager;

/**
 * The main class.<br>
 * Initializes the display and starts the main window.
 *
 * @author <a href="mailto:philipp@seerainer.com">Philipp Seerainer</a>
 */
class Main implements PrimitiveConstants, StringConstants {

    private static final long startTime = System.currentTimeMillis();

    private static Display display;

    private static Display getDisplay() {
	display = Display.getDefault();
	if (!WIN32 || !DARK) {
	    return display;
	}
	display.setData(darkModeExplorerTheme, Boolean.TRUE);
	display.setData(shellTitleColoring, Boolean.TRUE);
	display.setData(menuBarBackgroundColor, getColor(MENU_BACK, MENU_BACK, MENU_BACK));
	display.setData(menuBarForegroundColor, getColor(MENU_FORE, MENU_FORE, MENU_FORE));
	display.setData(menuBarBorderColor, getColor(MENU_BORD, MENU_BORD, MENU_BORD));
	display.setData(use_WS_BORDER, Boolean.TRUE);
	display.setData(useDarkTheme, Boolean.TRUE);
	display.setData(useDarkThemeIcons, Boolean.TRUE);
	return display;
    }

    private static long getTime() {
	return System.currentTimeMillis() - startTime;
    }

    /**
     * The main method that initializes the display and starts the main window. It
     * measures and logs the time taken to start the application and the total
     * execution time.
     *
     * @param args The command line arguments.
     */
    static void main(final String[] args) {
	if (!SingleInstanceManager.acquire(APP_NAME)) {
	    System.exit(1);
	}

	try {
	    System.setProperty(useSystemTheme, trueStr);
	    Display.setAppName(APP_NAME);
	    Display.setAppVersion(APP_VERS);
	    LogFactory.configureLogging();

	    display = getDisplay();
	    final var mainUI = new MainWindow(display, args);
	    LogFactory.getLog().info(START_TIME, APP_NAME, Long.valueOf(getTime()));
	    while (!mainUI.getShell().isDisposed()) {
		if (!display.readAndDispatch()) {
		    display.sleep();
		}
	    }
	} catch (final Exception e) {
	    LogFactory.getLog().error(ERROR, e);
	} finally {
	    if (display != null && !display.isDisposed()) {
		display.dispose();
	    }
	    LogFactory.getLog().info(TOTAL_TIME, APP_NAME, Long.valueOf(getTime() / SECONDS));
	}
    }
}
