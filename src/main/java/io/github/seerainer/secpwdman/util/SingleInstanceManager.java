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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for ensuring only one instance of an application runs at a
 * time. Uses file locking mechanism that is automatically released when the JVM
 * exits.
 */
public class SingleInstanceManager {

    private final String applicationName;
    private final Path lockFilePath;
    private FileChannel channel;
    private FileLock lock;
    private RandomAccessFile randomAccessFile;
    private boolean locked = false;

    private SingleInstanceManager(final String applicationName) {
	this.applicationName = sanitizeFileName(applicationName);
	this.lockFilePath = createLockFilePath();
    }

    /**
     * Acquire a lock for the given application name. If the lock is already held by
     * another instance, this method returns false.
     *
     * @param applicationName Name of the application
     * @return true if the lock was acquired, false otherwise
     */
    public static boolean acquire(final String applicationName) {
	final var manager = new SingleInstanceManager(applicationName);
	return manager.tryLock();
    }

    private static String getProcessId() {
	try {
	    return String.valueOf(ProcessHandle.current().pid());
	} catch (final Exception e) {
	    final var jvmName = ManagementFactory.getRuntimeMXBean().getName();
	    return jvmName.split("@")[0];
	}
    }

    private static String sanitizeFileName(final String name) {
	// Remove invalid characters for file names
	return name.replaceAll("[<>:\"/\\\\|?*]", "_").trim();
    }

    private void closeResources() {
	try {
	    if (channel != null) {
		channel.close();
	    }
	} catch (final IOException e) {
	    System.err.println("Error closing file channel: " + e.getMessage());
	}

	try {
	    if (randomAccessFile != null) {
		randomAccessFile.close();
	    }
	} catch (final IOException e) {
	    System.err.println("Error closing random access file: " + e.getMessage());
	}

	channel = null;
	randomAccessFile = null;
	lock = null;
    }

    private Path createLockFilePath() {
	final var userHome = System.getProperty("user.home");
	final var os = System.getProperty("os.name").toLowerCase();

	if (os.contains("windows")) {
	    // Windows: Use %LOCALAPPDATA%\AppName\
	    final var appData = System.getenv("LOCALAPPDATA");
	    if (appData != null) {
		return Paths.get(appData, applicationName, applicationName + ".lock");
	    }
	    // Fallback to temp directory
	    return Paths.get(System.getProperty("java.io.tmpdir"), applicationName + ".lock");

	}
	if (os.contains("mac")) {
	    // macOS: Use ~/Library/Application Support/AppName/
	    return Paths.get(userHome, "Library", "Application Support", applicationName, applicationName + ".lock");

	}
	// Linux/Unix: Use ~/.local/share/AppName/ or XDG_DATA_HOME
	final var xdgDataHome = System.getenv("XDG_DATA_HOME");
	if (xdgDataHome != null) {
	    return Paths.get(xdgDataHome, applicationName, applicationName + ".lock");
	}
	return Paths.get(userHome, ".local", "share", applicationName, applicationName + ".lock");
    }

    private void releaseLock() {
	if (!locked) {
	    return;
	}

	try {
	    if (lock != null && lock.isValid()) {
		lock.release();
	    }
	} catch (final IOException e) {
	    System.err.println("Error releasing lock: " + e.getMessage());
	}

	closeResources();

	try {
	    if (Files.exists(lockFilePath)) {
		Files.delete(lockFilePath);
	    }
	} catch (final IOException e) {
	    System.err.println("Could not delete lock file: " + e.getMessage());
	}

	locked = false;
    }

    private boolean tryLock() {
	if (locked) {
	    return true;
	}

	try {
	    Files.createDirectories(lockFilePath.getParent());
	    randomAccessFile = new RandomAccessFile(lockFilePath.toFile(), "rw");
	    channel = randomAccessFile.getChannel();
	    lock = channel.tryLock();

	    if (lock == null) {
		closeResources();
		return false;
	    }

	    writeApplicationInfo();

	    Runtime.getRuntime().addShutdownHook(new Thread(this::releaseLock));

	    locked = true;
	    return true;
	} catch (final OverlappingFileLockException e) {
	    closeResources();
	    return false;
	} catch (final IOException e) {
	    System.err.println("Error acquiring single instance lock: " + e.getMessage());
	    closeResources();
	    return false;
	}
    }

    private void writeApplicationInfo() throws IOException {
	final var info = """
		Application: %s%n\
		PID: %s%n\
		Started: %s%n\
		Java Version: %s%n\
		User: %s%n""".formatted(applicationName, getProcessId(),
		LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
		System.getProperty("java.version"), System.getProperty("user.name"));

	channel.truncate(0);
	channel.write(ByteBuffer.wrap(info.getBytes()));
	channel.force(true);
    }
}
