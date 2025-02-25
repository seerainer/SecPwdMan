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

import static java.lang.Boolean.valueOf;
import static java.lang.Integer.valueOf;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
import io.github.seerainer.secpwdman.util.SWTUtil;
import io.github.seerainer.secpwdman.util.Util;

/**
 * The class JsonUtil.
 */
class JsonUtil implements CryptoConstants, PrimitiveConstants, StringConstants {

	//@formatter:off
	private static JsonStringWriter getEncryptionValues(final ConfigData cData) {
		return JsonWriter.indent(tabul).string()
			.object()
				.value(appName, APP_NAME)
				.value(appVers, APP_VERS)
				.value(keyALGO, cData.getKeyALGO())
				.value(cipALGO, cData.getCipherALGO())
				.value(isArgon, valueOf(cData.isArgon2()))
				.value(argon2T, cData.getArgon2Type() == Argon2.D ? argon2d : argon2id)
				.value(argon2M, valueOf(cData.getArgon2Memo()))
				.value(argon2I, valueOf(cData.getArgon2Iter()))
				.value(argon2P, valueOf(cData.getArgon2Para()))
				.value(pbkdf2I, valueOf(cData.getPBKDF2Iter()));
	}

	private static String getFontDataString(final Control control) {
		return control.getFont().getFontData()[0].toString();
	}

	static byte[] getJsonConfig(final Action action) throws UnsupportedEncodingException {
		final var cData = action.getCData();
		final var shell = action.getShell();
		final var size = shell.getSize();
		final var pos = shell.getLocation();

		return getEncryptionValues(cData)
				.value(base64E, valueOf(cData.isBase64()))
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
		.done().getBytes(UTF8);
	}
	//@formatter:on

	static byte[] getJsonFile(final ConfigData cData, final byte[] bytes) throws UnsupportedEncodingException {
		final var encStr = new String(Util.getBase64Encode(bytes), StandardCharsets.UTF_8);
		return getEncryptionValues(cData).value(encData, encStr).end().done().getBytes(UTF8);
	}

	private static JsonObject getJsonObject(final InputStream is) throws JsonParserException {
		return JsonParser.object().from(is);
	}

	static boolean hasCorrectFileFormat(final InputStream is) throws JsonParserException, UnsupportedEncodingException {
		final var obj = getJsonObject(is);
		return APP_NAME.equals(obj.getString(appName))
				&& Util.getBase64Decode(obj.getString(encData).getBytes(UTF8)) != null;
	}

	private static JsonObject setEncryptionValues(final ConfigData cData, final InputStream is)
			throws JsonParserException {
		final var obj = getJsonObject(is);
		cData.setKeyALGO(obj.getString(keyALGO, cData.getKeyALGO()));
		cData.setCipherALGO(obj.getString(cipALGO, cData.getCipherALGO()));
		cData.setArgon2(obj.getBoolean(isArgon, valueOf(cData.isArgon2())));
		cData.setArgon2Type(argon2d.equals(obj.getString(argon2T, argon2d)) ? Argon2.D : Argon2.ID);
		cData.setArgon2Memo(obj.getInt(argon2M, cData.getArgon2Memo()));
		cData.setArgon2Iter(obj.getInt(argon2I, cData.getArgon2Iter()));
		cData.setArgon2Para(obj.getInt(argon2P, cData.getArgon2Para()));
		cData.setPBKDF2Iter(obj.getInt(pbkdf2I, cData.getPBKDF2Iter()));
		return obj;
	}

	static void setJsonConfig(final Action action, final InputStream is) throws JsonParserException {
		final var cData = action.getCData();
		final var obj = setEncryptionValues(cData, is);
		final var preferredSizeX = SWTUtil.getPrefSize(action.getShell()).x;
		final var fontString = new FontData(safeFont, 10, SWT.NORMAL).toString();
		cData.setBase64(obj.getBoolean(base64E, valueOf(cData.isBase64())));
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

	static byte[] setJsonFile(final ConfigData cData, final InputStream is)
			throws JsonParserException, UnsupportedEncodingException {
		final var dataStr = Util.getBase64Decode(setEncryptionValues(cData, is).getString(encData).getBytes(UTF8));
		return dataStr == null ? new byte[0] : dataStr;
	}

	private JsonUtil() {
	}
}
