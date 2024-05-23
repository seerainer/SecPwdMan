/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.widgets.Widgets.msg;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import io.github.seerainer.secpwdman.config.ConfData;

/**
 * The Class SWTUtil.
 */
public class SWTUtil {

	public static final boolean DARK = Display.isSystemDarkTheme();

	/**
	 * Gets the image.
	 *
	 * @param display the display
	 * @param image   the image
	 * @return the image
	 */
	public static Image getImage(final Display display, final String image) {
		final var img = new Image(display, new ByteArrayInputStream(Base64.getMimeDecoder().decode(image)));
		img.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		return img;
	}

	/**
	 * Asks to show passwords in cleartext.
	 *
	 * @param cData the cData
	 * @param shell the shell
	 * @return true, if is yes
	 */
	public static boolean msgShowPasswords(final ConfData cData, final Shell shell) {
		return msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO, cData.titleWar, cData.warnPass) == SWT.YES;
	}

	/**
	 * Center the shell.
	 *
	 * @param shell the shell
	 */
	public static void setCenter(final Shell shell) {
		final var r = shell.getDisplay().getBounds();
		final var s = shell.getBounds();
		shell.setLocation(new Point((r.width - s.width) / 2, ((r.height - s.height) * 2) / 5));
	}

	private SWTUtil() {
	}
}
