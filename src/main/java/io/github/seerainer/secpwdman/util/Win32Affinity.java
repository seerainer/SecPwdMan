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
package io.github.seerainer.secpwdman.util;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;

import org.eclipse.swt.widgets.Shell;
import org.graalvm.nativeimage.ImageInfo;

import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * This class provides a method to set the display affinity of a window on
 * Windows.
 *
 * <p>
 * The {@link #setWindowDisplayAffinity(long)} method sets the display affinity
 * of a window to exclude it from screen capture.
 * </p>
 *
 * <p>
 * This is useful for preventing sensitive information from being captured by
 * screen recording software.
 * </p>
 */
public class Win32Affinity implements PrimitiveConstants, StringConstants {

    private static final Arena ARENA = Arena.global();
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup().or(LINKER.defaultLookup());

    private Win32Affinity() {
    }

    /**
     * SetWindowDisplayAffinity function <a href=
     * "https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowdisplayaffinity">MSDN</a>
     *
     * @param shell The shell whose display affinity is to be set.
     * @return true if the display affinity was set successfully, false otherwise.
     */
    public static boolean setWindowDisplayAffinity(final Shell shell) {
	if (!SWTUtil.WIN32) {
	    return false;
	}
	try {
	    final var isNative = ImageInfo.inImageCode();
	    final var loader = isNative ? SYMBOL_LOOKUP : SymbolLookup.libraryLookup(user32, ARENA);
	    final var address = loader.findOrThrow(isNative ? setAffinity : setAffinity.substring(3));
	    final var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT);
	    final var mh = LINKER.downcallHandle(address, fd);
	    return (int) mh.invokeExact(shell.handle, WDA_EXCLUDEFROMCAPTURE) != 0;
	} catch (final Throwable t) {
	    LogFactory.getLog().error(AFFINITY_FAILED, t);
	    return false;
	}
    }
}
