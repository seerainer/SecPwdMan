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

import static io.github.seerainer.secpwdman.ui.DialogFactory.createProgressDialog;
import static io.github.seerainer.secpwdman.ui.Widgets.msg;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.io.IOUtil;
import io.github.seerainer.secpwdman.ui.ProgressDialog;

/**
 * The class FileShredder.
 */
public class FileShredder implements PrimitiveConstants, StringConstants {

    private static final Logger LOG = LogFactory.getLog();

    private FileShredder() {
    }

    /**
     * Shreds the file.
     *
     * @param action the action
     * @param shell  the shell
     * @param file   the file
     */
    public static void shredFile(final Action action, final Shell shell, final String file) {
	final var path = IOUtil.getPath(file);
	final var progressDialog = new Shell[1];

	try {
	    final var fileSize = Files.size(path);
	    if (fileSize == 0) {
		Files.delete(path);
		return;
	    }
	    if (fileSize >= MAX_FILE_SIZE) {
		LOG.warn(WARN, FILE_TOO_LARGE);
		msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorFil.formatted(file));
		return;
	    }

	    final var startTime = System.currentTimeMillis();
	    final var fileName = path.getFileName().toString();
	    final var progressMax = (int) ((fileSize + RAND_BUFFER_SIZE - 1) / RAND_BUFFER_SIZE);
	    progressDialog[0] = createProgressDialog(action, shredFil + fileName, progressMax);

	    final var buffer = new byte[RAND_BUFFER_SIZE];
	    try (var raf = new RandomAccessFile(path.toFile(), fileMode)) {
		final var secu = Crypto.getSecureRandom();
		for (int progressCount = 0, i = 0; i < fileSize; i += buffer.length) {
		    secu.nextBytes(buffer);
		    raf.seek(i);
		    raf.write(buffer, 0, (int) Math.min(buffer.length, fileSize - i));
		    ProgressDialog.updateProgressDialog(progressDialog[0], progressCount++);
		}
	    }

	    Files.delete(path);
	    LOG.info(TIME_TO_SHRED, Long.valueOf(System.currentTimeMillis() - startTime));
	} catch (final IOException e) {
	    LOG.error(ERROR, e);
	    msg(shell, SWT.ICON_ERROR | SWT.OK, titleErr, errorShr.formatted(file));
	} finally {
	    final var dialog = progressDialog[0];
	    if (dialog != null && !dialog.isDisposed()) {
		dialog.close();
	    }
	}
    }
}
