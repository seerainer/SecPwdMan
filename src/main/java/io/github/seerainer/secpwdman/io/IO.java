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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.crypto.CryptoFactory.crypto;
import static io.github.seerainer.secpwdman.crypto.KeyStoreManager.putPasswordInKeyStore;
import static io.github.seerainer.secpwdman.util.RandomPassword.generateKeyStorePassword;
import static io.github.seerainer.secpwdman.util.Util.clear;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;

import java.io.IOException;
import java.io.InputStream;
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
import org.slf4j.Logger;

import com.grack.nanojson.JsonParserException;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class IO.
 */
public class IO implements StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	static InputStream open(final String filePath) throws IOException {
		return Files.newInputStream(Path.of(filePath));
	}

	static void save(final String filePath, final byte[] fileBytes) throws IOException {
		Files.write(Path.of(filePath), fileBytes);
	}

	private static void savePassword(final byte[] password, final ConfigData cData) {
		final var sensitiveData = cData.getSensitiveData();
		sensitiveData.setKeyStorePassword(generateKeyStorePassword());
		sensitiveData.setKeyStoreData(putPasswordInKeyStore(sensitiveData.getKeyStorePassword(), password));
	}

	private final Action action;

	/**
	 * Instantiates a new IO.
	 *
	 * @param action the action
	 */
	public IO(final Action action) {
		this.action = action;
	}

	/**
	 * Opens the file.
	 *
	 * @param password the password
	 * @param file     the file
	 * @return true, if successful
	 */
	public boolean openFile(final byte[] password, final String file) {
		if (file == null) {
			throw new IllegalArgumentException(fileNull);
		}
		final var startTime = System.currentTimeMillis();
		final var pwdIsNotNull = (password != null) && (password.length > 0);
		final var cData = action.getCData();
		var exMsg = empty;
		try (final var is = open(file)) {
			byte[] encText = null;
			if (pwdIsNotNull) {
				savePassword(password, cData);
				encText = JsonUtil.setJsonFile(cData, is);
			}
			action.fillTable(true, pwdIsNotNull ? crypto(cData).decrypt(encText, password) : is.readAllBytes());
			LOG.info(timeOpen, Long.valueOf(System.currentTimeMillis() - startTime));
			return true;
		} catch (final BadPaddingException e) {
			LOG.warn(warning, e);
			exMsg = errorPwd;
		} catch (final IOException e) {
			LOG.warn(warning, e);
			exMsg = errorFil.formatted(file);
		} catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException | JsonParserException e) {
			LOG.warn(warning, e);
			exMsg = errorImp.formatted(IOUtil.getFilePath(file));
		} catch (final IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException
				| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | OutOfMemoryError e) {
			LOG.error(error, e);
			exMsg = errorSev;
		}
		msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, exMsg);
		return false;
	}

	/**
	 * Saves the file.
	 *
	 * @param password the password
	 * @param file     the file
	 * @return true, if successful
	 */
	public boolean saveFile(final byte[] password, final String file) {
		if (file == null) {
			throw new IllegalArgumentException(fileNull);
		}
		final var startTime = System.currentTimeMillis();
		final var pwdIsNotNull = (password != null) && (password.length > 0);
		final var cData = action.getCData();
		action.resetGroupList();
		final var bytes = action.extractData();
		try {
			byte[] encText = null;
			if (pwdIsNotNull) {
				savePassword(password, cData);
				encText = crypto(cData).encrypt(bytes, password);
			}
			save(file, pwdIsNotNull ? JsonUtil.getJsonFile(cData, encText) : bytes);
			LOG.info(timeSave, Long.valueOf(System.currentTimeMillis() - startTime));
			return true;
		} catch (final BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException
				| InvalidKeyException | InvalidKeySpecException | IOException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			LOG.error(error, e);
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, titleErr, errorSev);
			return false;
		} finally {
			clear(bytes);
		}
	}
}
