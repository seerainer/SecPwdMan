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

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;

/**
 * The class AutoLockManager manages automatic locking of the application after
 * a period of inactivity. It listens for user activity and locks the
 * application if no activity is detected within a specified timeout.
 */
public class AutoLockManager implements PrimitiveConstants {

    private static AutoLockManager instance;

    private static int LOCK_TIMEOUT_MS;

    private boolean running = true;

    private long lastActivityTime = System.currentTimeMillis();

    private final Display display;

    private final FileAction action;

    private final Listener activityListener = _ -> lastActivityTime = System.currentTimeMillis();

    private final int[] events = { SWT.MouseMove, SWT.KeyDown, SWT.MouseDown, SWT.MouseUp, SWT.MouseWheel };

    private AutoLockManager(final Display display, final FileAction action) {
	this.display = display;
	this.action = action;
	LOCK_TIMEOUT_MS = action.getCData().getAutoLockTime() * AUTOLOCK_MAX * SECONDS;
	addActivityListeners();
	start();
    }

    /**
     * Gets the singleton instance of AutoLockManager. If an instance does not
     * exist, it creates a new one with the provided display and action.
     *
     * @param display the SWT display to monitor for activity
     * @param action  the FileAction for locking the application
     */
    public static synchronized AutoLockManager getInstance(final Display display, final FileAction action) {
	if (Objects.isNull(instance)) {
	    instance = new AutoLockManager(display, action);
	}
	return instance;
    }

    private void addActivityListeners() {
	for (final int event : events) {
	    display.addFilter(event, activityListener);
	}
    }

    private void checkTimeout() {
	if (!running) {
	    return;
	}
	if (System.currentTimeMillis() - lastActivityTime > LOCK_TIMEOUT_MS) {
	    action.setLocked();
	} else {
	    start();
	}
    }

    private void removeActivityListeners() {
	for (final int event : events) {
	    display.removeFilter(event, activityListener);
	}
    }

    private void start() {
	display.timerExec(SECONDS, this::checkTimeout);
    }

    /**
     * Stops the AutoLockManager by removing all activity listeners
     */
    public void stop() {
	running = false;
	removeActivityListeners();
	instance = null;
    }
}
