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
package io.github.seerainer.secpwdman.util;

import static java.lang.Boolean.valueOf;
import static java.lang.Integer.valueOf;

import java.io.InputStream;
import java.util.Base64;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonStringWriter;
import com.grack.nanojson.JsonWriter;
import com.password4j.types.Argon2;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;

/**
 * The class JsonUtil.
 */
public final class JsonUtil implements CryptoConstants, PrimitiveConstants, StringConstants {

	private static byte[] getBase64Data(final String dataStr) {
		if (dataStr == null) {
			return null;
		}
		return Base64.getDecoder().decode(dataStr.getBytes());
	}

	//@formatter:off
	private static JsonStringWriter getEncryptionValues(final ConfigData cData) {
		return JsonWriter.indent(tabul).string()
			.object()
				.value(appName, APP_NAME)
				.value(appVers, APP_VERS)
				.value(keyALGO, cData.getKeyALGO())
				.value(cipALGO, cData.getCipherALGO())
				.value(isArgon, valueOf(cData.isArgon2()))
				.value(argon2T, cData.getArgonType() == Argon2.D ? argon2d : argon2id)
				.value(argon2M, valueOf(cData.getArgonMemo()))
				.value(argon2I, valueOf(cData.getArgonIter()))
				.value(argon2P, valueOf(cData.getArgonPara()))
				.value(pbkdf2I, valueOf(cData.getPBKDFIter()));
	}

	private static String getFontDataString(final Control control) {
		return control.getFont().getFontData()[0].toString();
	}

	/**
	 * Gets the cData config.
	 *
	 * @param action the Action
	 * @return byte array json object
	 */
	public static byte[] getJsonConfig(final Action action) {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var size = shell.getSize();
		final var pos = shell.getLocation();

		return getEncryptionValues(cData)
				.value(buffLen, valueOf(cData.getBufferLength()))
				.value(clearPw, valueOf(cData.getClearPassword()))
				.value(coWidth, valueOf(cData.getColumnWidth()))
				.value(divider, String.valueOf(cData.getDivider()))
				.value(pwdMinL, valueOf(cData.getPasswordMinLength()))
				.value(resizeC, valueOf(cData.isResizeCol()))
				.value(shwPass, valueOf(cData.isShowPassword()))
				.value(shellFo, getFontDataString(shell))
				.value(tableFo, getFontDataString(action.getTable()))
				.value(shelMax, valueOf(shell.getMaximized()))
				.value(shellSX, valueOf(size.x))
				.value(shellSY, valueOf(size.y))
				.value(shellPX, valueOf(pos.x))
				.value(shellPY, valueOf(pos.y))
			.end()
		.done().getBytes();
	}
	//@formatter:on

	/**
	 * Gets configuration and the encrypted data.
	 *
	 * @param cData the ConfData
	 * @param b     the encrypted byte array
	 * @return byte array json object
	 */
	public static byte[] getJsonFile(final ConfigData cData, final byte[] bytes) {
		final var encStr = new String(Base64.getEncoder().encode(bytes));
		return getEncryptionValues(cData).value(encData, encStr).end().done().getBytes();
	}

	private static JsonObject getJsonObject(final InputStream is) throws JsonParserException {
		return JsonParser.object().from(is);
	}

	/**
	 * Checks if the file has the correct format.
	 *
	 * @param is the InputStream
	 * @return true if the file has the correct format
	 * @throws JsonParserException
	 */
	public static boolean hasCorrectFileFormat(final InputStream is) throws JsonParserException {
		final var obj = getJsonObject(is);
		return APP_NAME.equals(obj.getString(appName)) && getBase64Data(obj.getString(encData)) != null;
	}

	private static JsonObject setEncryptionValues(final ConfigData cData, final InputStream is) throws JsonParserException {
		final var obj = getJsonObject(is);
		cData.setKeyALGO(obj.getString(keyALGO, cData.getKeyALGO()));
		cData.setCipherALGO(obj.getString(cipALGO, cData.getCipherALGO()));
		cData.setArgon2(obj.getBoolean(isArgon, valueOf(cData.isArgon2())));
		cData.setArgonType(argon2d.equals(obj.getString(argon2T, argon2d)) ? Argon2.D : Argon2.ID);
		cData.setArgonMemo(obj.getInt(argon2M, cData.getArgonMemo()));
		cData.setArgonIter(obj.getInt(argon2I, cData.getArgonIter()));
		cData.setArgonPara(obj.getInt(argon2P, cData.getArgonPara()));
		cData.setPBKDFIter(obj.getInt(pbkdf2I, cData.getPBKDFIter()));

		return obj;
	}

	/**
	 * Sets the cData config.
	 *
	 * @param action the Action
	 * @param is     the InputStream
	 * @throws JsonParserException
	 */
	public static void setJsonConfig(final Action action, final InputStream is) throws JsonParserException {
		final var cData = action.getCData();
		final var obj = setEncryptionValues(cData, is);
		final var preferredSizeX = SWTUtil.getPrefSize(action.getShell()).x;
		final var fontString = new FontData(safeFont, 9, SWT.NORMAL).toString();

		cData.setBufferLength(obj.getInt(buffLen, cData.getBufferLength()));
		cData.setClearPassword(obj.getInt(clearPw, cData.getClearPassword()));
		cData.setColumnWidth(obj.getInt(coWidth, cData.getColumnWidth()));
		cData.setDivider(obj.getString(divider, String.valueOf(cData.getDivider())).charAt(0));
		cData.setMaximized(obj.getBoolean(shelMax, valueOf(cData.isMaximized())));
		cData.setPasswordMinLength(obj.getInt(pwdMinL, cData.getPasswordMinLength()));
		cData.setResizeCol(obj.getBoolean(resizeC, valueOf(cData.isResizeCol())));
		cData.setShellFont(obj.getString(shellFo, fontString));
		cData.setShellLocation(new Point(obj.getInt(shellPX, PREF_POS_XY), obj.getInt(shellPY, PREF_POS_XY)));
		cData.setShellSize(new Point(obj.getInt(shellSX, preferredSizeX), obj.getInt(shellSY, PREF_SIZE_Y)));
		cData.setShowPassword(obj.getBoolean(shwPass, valueOf(cData.isShowPassword())));
		cData.setTableFont(obj.getString(tableFo, fontString));
	}

	/**
	 * Sets config and the encrypted data.
	 *
	 * @param cData the ConfData
	 * @param is    the InputStream
	 * @return byte array the encrypted data
	 * @throws JsonParserException
	 */
	public static byte[] setJsonFile(final ConfigData cData, final InputStream is) throws JsonParserException {
		final var dataStr = getBase64Data(setEncryptionValues(cData, is).getString(encData));
		return dataStr == null ? new byte[0] : dataStr;
	}

	private JsonUtil() {
	}
}
