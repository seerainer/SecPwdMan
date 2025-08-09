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
package io.github.seerainer.secpwdman.io;

import static java.lang.Boolean.valueOf;
import static java.lang.Integer.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.util.Objects;

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
import com.password4j.types.Hmac;

import io.github.seerainer.secpwdman.action.Action;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConfig;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.util.SWTUtil;
import io.github.seerainer.secpwdman.util.Util;

/**
 * The class JsonUtil.
 */
class JsonUtil implements CryptoConstants, PrimitiveConstants, StringConstants {

    private JsonUtil() {
    }

    //@formatter:off
    private static JsonStringWriter getEncryptionValues(final ConfigData cData) {
    	final var cConf = cData.getCryptoConfig();
    	final var jsw = JsonWriter.indent(tabul).string().object()
    			.value(appName, APP_NAME)
    			.value(appVers, APP_VERS)
    			.value(keyALGO, cConf.getKeyALGO())
    			.value(cipALGO, cConf.getCipherALGO());
    	return getKeyDerivationValues(cConf, jsw);
    }

    private static String getFontDataString(final Control control) {
    	final var font = control.getFont();
    	final var fontData = font.getFontData()[0];
    	return fontData.toString();
    }

    static byte[] getJsonConfig(final Action action) {
    	final var cData = action.getCData();
    	final var shell = action.getShell();
    	final var size = shell.getSize();
    	final var pos = shell.getLocation();

    	return getEncryptionValues(cData)
    			.value(autoLoc, valueOf(cData.getAutoLockTime()))
    			.value(buffLen, valueOf(cData.getBufferLength()))
    			.value(clearPw, valueOf(cData.getClearPassword()))
    			.value(coWidth, valueOf(cData.getColumnWidth()))
    			.value(deflate, valueOf(cData.isCompress()))
    			.value(divider, String.valueOf(cData.getDivider()))
    			.value(pwdMinL, valueOf(cData.getPasswordMinLength()))
    			.value(resizeC, valueOf(cData.isResizeCol()))
    			.value(shellFo, getFontDataString(shell))
    			.value(tableFo, getFontDataString(action.getTable()))
    			.value(shelMax, valueOf(shell.getMaximized()))
    			.value(shellSX, valueOf(size.x))
    			.value(shellSY, valueOf(size.y))
    			.value(shellPX, valueOf(pos.x))
    			.value(shellPY, valueOf(pos.y))
    		.end()
    	.done().getBytes(UTF_8);
    }

    static byte[] getJsonFile(final ConfigData cData, final byte[] bytes) {
    	final var encStr = new String(Util.getBase64Encode(bytes), UTF_8);
    	return getEncryptionValues(cData).value(encData, encStr).end().done().getBytes(UTF_8);
    }

    private static JsonObject getJsonObject(final InputStream is) throws JsonParserException {
    	return JsonParser.object().from(is);
    }

    private static CryptoConfig.KDF getKeyDerivation(final JsonObject obj) {
    	return switch (obj.getString(keyderf, argon2)) {
    	case pbkdf2 -> CryptoConfig.KDF.PBKDF2;
    	case SCRYPT -> CryptoConfig.KDF.scrypt;
    	default -> CryptoConfig.KDF.Argon2;
    	};
    }

    private static JsonStringWriter getKeyDerivationValues(final CryptoConfig cConf, final JsonStringWriter jsw) {
    	return switch (cConf.getKeyDerivation()) {
    	case CryptoConfig.KDF.PBKDF2 -> jsw
    			.value(keyderf, pbkdf2)
    			.value(hmacSHA, cConf.getHmac().toString())
    			.value(pbkdf2I, cConf.getPBKDF2Iter());
    	case CryptoConfig.KDF.scrypt -> jsw
    			.value(keyderf, SCRYPT)
    			.value(scryptN, cConf.getScryptN())
    			.value(scryptR, cConf.getScryptR())
    			.value(scryptP, cConf.getScryptP());
    	default -> jsw
    			.value(keyderf, argon2)
    			.value(argon2T, cConf.getArgon2Type() == Argon2.D ? argon2d : argon2id)
    			.value(argon2M, cConf.getArgon2Memo())
    			.value(argon2I, cConf.getArgon2Iter())
    			.value(argon2P, cConf.getArgon2Para());
    	};
    }
    //@formatter:on

    static boolean hasCorrectFileFormat(final InputStream is) throws JsonParserException {
	final var obj = getJsonObject(is);
	final var data = obj.getString(encData);
	if (Util.isBlank(data)) {
	    return false;
	}
	return APP_NAME.equals(obj.getString(appName)) && Objects.nonNull(Util.getBase64Decode(data.getBytes(UTF_8)));
    }

    private static JsonObject setEncryptionValues(final ConfigData cData, final InputStream is)
	    throws JsonParserException {
	final var cConf = cData.getCryptoConfig();
	final var obj = getJsonObject(is);
	final var sha2 = Hmac.SHA256.toString();
	cConf.setKeyALGO(obj.getString(keyALGO, cConf.getKeyALGO()));
	cConf.setCipherALGO(obj.getString(cipALGO, cConf.getCipherALGO()));
	cConf.setKeyDerivation(getKeyDerivation(obj));
	cConf.setArgon2Type(argon2d.equals(obj.getString(argon2T, argon2d)) ? Argon2.D : Argon2.ID);
	cConf.setArgon2Memo(obj.getInt(argon2M, cConf.getArgon2Memo()));
	cConf.setArgon2Iter(obj.getInt(argon2I, cConf.getArgon2Iter()));
	cConf.setArgon2Para(obj.getInt(argon2P, cConf.getArgon2Para()));
	cConf.setHmac(sha2.equals(obj.getString(hmacSHA, sha2)) ? Hmac.SHA256 : Hmac.SHA512);
	cConf.setPBKDF2Iter(obj.getInt(pbkdf2I, cConf.getPBKDF2Iter()));
	cConf.setScryptN(obj.getInt(scryptN, cConf.getScryptN()));
	cConf.setScryptR(obj.getInt(scryptR, cConf.getScryptR()));
	cConf.setScryptP(obj.getInt(scryptP, cConf.getScryptP()));
	return obj;
    }

    static void setJsonConfig(final Action action, final InputStream is) throws JsonParserException {
	final var cData = action.getCData();
	final var obj = setEncryptionValues(cData, is);
	final var preferredSizeX = SWTUtil.getPrefSize(action.getShell()).x;
	final var fontString = new FontData(safeFont, 10, SWT.NORMAL).toString();
	cData.setAutoLockTime(obj.getInt(autoLoc, cData.getAutoLockTime()));
	cData.setBufferLength(obj.getInt(buffLen, cData.getBufferLength()));
	cData.setClearPassword(obj.getInt(clearPw, cData.getClearPassword()));
	cData.setColumnWidth(obj.getInt(coWidth, cData.getColumnWidth()));
	cData.setCompress(obj.getBoolean(deflate, valueOf(cData.isCompress())));
	cData.setDivider(obj.getString(divider, String.valueOf(cData.getDivider())).charAt(0));
	cData.setMaximized(obj.getBoolean(shelMax, valueOf(cData.isMaximized())));
	cData.setPasswordMinLength(obj.getInt(pwdMinL, cData.getPasswordMinLength()));
	cData.setResizeCol(obj.getBoolean(resizeC, valueOf(cData.isResizeCol())));
	cData.setShellFont(obj.getString(shellFo, fontString));
	cData.setShellLocation(new Point(obj.getInt(shellPX, PREF_POS_XY), obj.getInt(shellPY, PREF_POS_XY)));
	cData.setShellSize(new Point(obj.getInt(shellSX, preferredSizeX), obj.getInt(shellSY, PREF_SIZE_Y)));
	cData.setTableFont(obj.getString(tableFo, fontString));
    }

    static byte[] setJsonFile(final ConfigData cData, final InputStream is) throws JsonParserException {
	final var dataStr = Util.getBase64Decode(setEncryptionValues(cData, is).getString(encData).getBytes(UTF_8));
	return Objects.isNull(dataStr) ? new byte[0] : dataStr;
    }
}
