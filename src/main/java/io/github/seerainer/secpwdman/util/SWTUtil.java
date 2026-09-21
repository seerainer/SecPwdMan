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

import static io.github.seerainer.secpwdman.config.StringConstants.linuxGTK;
import static io.github.seerainer.secpwdman.config.StringConstants.macCocoa;
import static io.github.seerainer.secpwdman.config.StringConstants.titleWar;
import static io.github.seerainer.secpwdman.config.StringConstants.windows;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import io.github.seerainer.secpwdman.config.ConfigData;

/**
 * The class SWTUtil.
 */
public class SWTUtil {

    public static final boolean DARK = Display.isSystemDarkTheme();
    public static final boolean LINUX = linuxGTK.equals(SWT.getPlatform());
    public static final boolean MACOS = macCocoa.equals(SWT.getPlatform());
    public static final boolean WIN32 = windows.equals(SWT.getPlatform());

    private static final String OWNED_FONT_KEY = "SecPwdMan.ownedFont";
    private static final String OWNED_FONT_ARMED = "SecPwdMan.ownedFontArmed";

    private SWTUtil() {
    }

    /**
     * Disposes the resource if non-null and not already disposed. SWT {@code Color}
     * objects do not require disposal since 2020-06, but {@code Font} and
     * {@code Image} still do.
     *
     * @param resource the resource to dispose, may be null
     */
    public static void safeDispose(final Resource resource) {
	if (resource == null || resource.isDisposed()) {
	    return;
	}
	resource.dispose();
    }

    /**
     * Disposes the widget if non-null and not already disposed. Child widgets are
     * disposed automatically with their parent; this is for top-level resources
     * such as {@code TrayItem} that have no SWT parent.
     *
     * @param widget the widget to dispose, may be null
     */
    public static void safeDispose(final Widget widget) {
	if (widget == null || widget.isDisposed()) {
	    return;
	}
	widget.dispose();
    }

    /**
     * Disposes the drop target if non-null and not already disposed.
     *
     * @param dropTarget the drop target to dispose, may be null
     */
    public static void safeDispose(final DropTarget dropTarget) {
	if (dropTarget == null || dropTarget.isDisposed()) {
	    return;
	}
	dropTarget.dispose();
    }

    /**
     * Arranges for the given resources to be disposed when the owner widget is
     * disposed. All disposals are guarded, so calling this alongside an explicit
     * disposal in an exit path is safe.
     *
     * @param owner     the widget owning the resources
     * @param resources the resources to dispose with the owner
     */
    public static void disposeOnExit(final Widget owner, final Resource... resources) {
	if (owner == null) {
	    return;
	}
	if (owner.isDisposed()) {
	    for (final var resource : resources) {
		safeDispose(resource);
	    }
	    return;
	}
	owner.addDisposeListener(_ -> {
	    for (final var resource : resources) {
		safeDispose(resource);
	    }
	});
    }

    /**
     * Returns the custom font previously installed via
     * {@link #setOwnedFont(Control, Font)}, or null if the control still uses its
     * inherited/system font.
     *
     * @param control the control to query
     * @return the owned font or null
     */
    public static Font getOwnedFont(final Control control) {
	if (control == null || control.isDisposed()) {
	    return null;
	}
	final var data = control.getData(OWNED_FONT_KEY);
	return data instanceof final Font font ? font : null;
    }

    /**
     * Sets a custom font on the control, disposing the previously owned font (if
     * any) and arranging disposal of the new font when the control is disposed. The
     * inherited/system font is never disposed.
     *
     * @param control the control to set the font on
     * @param font    the new custom font (takes ownership)
     */
    public static void setOwnedFont(final Control control, final Font font) {
	final var old = getOwnedFont(control);
	if (old != null && old != font) {
	    safeDispose(old);
	}
	control.setFont(font);
	control.setData(OWNED_FONT_KEY, font);
	if (control.getData(OWNED_FONT_ARMED) != null) {
	    return;
	}
	control.setData(OWNED_FONT_ARMED, Boolean.TRUE);
	control.addDisposeListener(_ -> {
	    final var data = control.getData(OWNED_FONT_KEY);
	    if (data instanceof final Font owned) {
		safeDispose(owned);
	    }
	});
    }

    /**
     * Gets the color.
     *
     * @param r the red
     * @param g the green
     * @param b the blue
     * @return Color
     */
    public static Color getColor(final int r, final int g, final int b) {
	return new Color(r, g, b);
    }

    /**
     * Gets the font.
     *
     * @param display the display
     * @param font    the font
     * @return Font
     */
    public static Font getFont(final Display display, final String font) {
	return new Font(display, new FontData(font));
    }

    /**
     * Gets the font.
     *
     * @param display the display
     * @param font    the font
     * @param size    the size
     * @param style   the style
     * @return Font
     */
    public static Font getFont(final Display display, final String font, final int size, final int style) {
	return new Font(display, new FontData(font, size, style));
    }

    /**
     * Gets the grid data.
     *
     * @param values the values
     * @return GridData
     */
    public static GridData getGridData(final int... values) {
	return switch (values.length) {
	case 4 -> new GridData(values[0], values[1], values[2] == 1, values[3] == 1);
	case 6 -> new GridData(values[0], values[1], values[2] == 1, values[3] == 1, values[4], values[5]);
	default -> new GridData(SWT.FILL, SWT.FILL);
	};
    }

    /**
     * Gets the image.
     *
     * @param display the display
     * @param image   the image
     * @return the image
     * @throws UnsupportedEncodingException
     */
    public static Image getImage(final Display display, final String image) {
	final var bytes = Base64.getMimeDecoder().decode(image.getBytes(UTF_8));
	final var bais = new ByteArrayInputStream(bytes);
	final var img = new Image(display, bais);
	img.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	return img;
    }

    /**
     * Gets the default layout.
     *
     * @return GridLayout
     */
    public static GridLayout getLayout() {
	final var layout = new GridLayout();
	layout.horizontalSpacing = 0;
	layout.marginHeight = 0;
	layout.marginWidth = 0;
	layout.verticalSpacing = 0;
	return layout;
    }

    /**
     * Gets the layout.
     *
     * @param numColumns   the num columns
     * @param hSpacing     the h spacing
     * @param vSpacing     the v spacing
     * @param marginBottom the margin bottom
     * @param marginLeft   the margin left
     * @param marginRight  the margin right
     * @param marginTop    the margin top
     * @return GridLayout the layout
     */
    public static GridLayout getLayout(final int numColumns, final int hSpacing, final int vSpacing,
	    final int marginBottom, final int marginLeft, final int marginRight, final int marginTop) {
	final var layout = new GridLayout(numColumns, false);
	layout.horizontalSpacing = hSpacing;
	layout.marginBottom = marginBottom;
	layout.marginLeft = marginLeft;
	layout.marginRight = marginRight;
	layout.marginTop = marginTop;
	layout.verticalSpacing = vSpacing;
	return layout;
    }

    /**
     * Returns the preferred size of the shell.
     *
     * @param control the shell
     * @return Point
     */
    public static Point getPrefSize(final Control control) {
	return control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    /**
     * Asks yes or no.
     *
     * @param cData the cData
     * @param shell the shell
     * @param txt   the txt
     * @return true, if is yes
     */
    public static boolean msgYesNo(final ConfigData cData, final Shell shell, final String txt) {
	return msg(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO, titleWar, txt) == SWT.YES;
    }

    /**
     * Center the shell.
     *
     * @param shell the shell
     */
    public static void setCenter(final Shell shell) {
	final var display = shell.getDisplay();
	final var primary = display.getPrimaryMonitor();
	final var bounds = primary.getBounds();
	final var shellBounds = shell.getBounds();
	final var x = bounds.x + (bounds.width - shellBounds.width) / 2;
	final var y = bounds.y + (bounds.height - shellBounds.height) / 2;
	shell.setLocation(x, y);
    }
}
