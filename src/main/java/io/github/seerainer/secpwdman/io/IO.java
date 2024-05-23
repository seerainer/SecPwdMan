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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.util.JsonUtil.readConfig;
import static io.github.seerainer.secpwdman.util.JsonUtil.saveConfig;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.SWT;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.crypto.Crypto;

/**
 * The Class IO.
 */
public class IO {

	private final Action action;

	/**
	 * Instantiates a new io.
	 *
	 * @param action the action
	 */
	public IO(final Action action) {
		this.action = action;
	}

	/**
	 * Open file.
	 *
	 * @param pwd  the password
	 * @param file the file
	 * @return true, if successful
	 */
	public boolean openFile(final byte[] pwd, final String file) {
		final var cData = action.getCData();
		var exMsg = cData.empty;

		byte[] data;

		try (final var is = Files.newInputStream(Path.of(file))) {
			if (pwd != null) {
				try {
					data = readConfig(cData, is);
				} catch (final JsonParserException e) {
					data = Files.readAllBytes(Path.of(file));
				}

				action.fillTable(true, new Crypto(cData).decrypt(data, pwd));
			} else
				action.fillTable(true, is.readAllBytes());

			return true;
		} catch (final BadPaddingException e) {
			exMsg = cData.errorPwd;
		} catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException | IOException e) {
			exMsg = cData.errorImp + cData.newLine + cData.newLine + e.fillInStackTrace().toString();
		} catch (final IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException
				| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | OutOfMemoryError e) {
			exMsg = e.fillInStackTrace().toString();
		} finally {
			if (pwd != null)
				clear(pwd);
		}

		msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, exMsg);

		return false;
	}

	/**
	 * Save file.
	 *
	 * @param pwd  the password
	 * @param file the file
	 * @return true, if successful
	 */
	public boolean saveFile(final byte[] pwd, final String file) {
		final var cData = action.getCData();

		action.resetGroupList();

		var fileBytes = action.extractData();

		try {
			if (pwd != null)
				fileBytes = saveConfig(cData, new Crypto(cData).encrypt(fileBytes, pwd));

			Files.write(Path.of(file), fileBytes);

			return true;
		} catch (final BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException
				| InvalidKeySpecException | IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		} finally {
			if (pwd != null)
				clear(pwd);

			fileBytes = null;
		}

		return false;
	}
}
