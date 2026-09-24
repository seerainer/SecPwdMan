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
package io.github.seerainer.secpwdman.io;

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.DELIMITER;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PREF_POS_XY;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PREF_SIZE_Y;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_NAME;
import static io.github.seerainer.secpwdman.config.StringConstants.APP_VERS;
import static io.github.seerainer.secpwdman.config.StringConstants.MAJOR_VERSION;
import static io.github.seerainer.secpwdman.config.StringConstants.appName;
import static io.github.seerainer.secpwdman.config.StringConstants.appVers;
import static io.github.seerainer.secpwdman.config.StringConstants.argon2I;
import static io.github.seerainer.secpwdman.config.StringConstants.argon2M;
import static io.github.seerainer.secpwdman.config.StringConstants.argon2P;
import static io.github.seerainer.secpwdman.config.StringConstants.argon2T;
import static io.github.seerainer.secpwdman.config.StringConstants.autoLoc;
import static io.github.seerainer.secpwdman.config.StringConstants.buffLen;
import static io.github.seerainer.secpwdman.config.StringConstants.cipALGO;
import static io.github.seerainer.secpwdman.config.StringConstants.clearPw;
import static io.github.seerainer.secpwdman.config.StringConstants.coWidth;
import static io.github.seerainer.secpwdman.config.StringConstants.deflate;
import static io.github.seerainer.secpwdman.config.StringConstants.dekSalt;
import static io.github.seerainer.secpwdman.config.StringConstants.divider;
import static io.github.seerainer.secpwdman.config.StringConstants.encData;
import static io.github.seerainer.secpwdman.config.StringConstants.encDek;
import static io.github.seerainer.secpwdman.config.StringConstants.formatVersion;
import static io.github.seerainer.secpwdman.config.StringConstants.hmacSHA;
import static io.github.seerainer.secpwdman.config.StringConstants.keyALGO;
import static io.github.seerainer.secpwdman.config.StringConstants.keyderf;
import static io.github.seerainer.secpwdman.config.StringConstants.pbkdf2I;
import static io.github.seerainer.secpwdman.config.StringConstants.pwdMinL;
import static io.github.seerainer.secpwdman.config.StringConstants.resizeC;
import static io.github.seerainer.secpwdman.config.StringConstants.safeFont;
import static io.github.seerainer.secpwdman.config.StringConstants.scryptN;
import static io.github.seerainer.secpwdman.config.StringConstants.scryptP;
import static io.github.seerainer.secpwdman.config.StringConstants.scryptR;
import static io.github.seerainer.secpwdman.config.StringConstants.shelMax;
import static io.github.seerainer.secpwdman.config.StringConstants.shellFo;
import static io.github.seerainer.secpwdman.config.StringConstants.shellPX;
import static io.github.seerainer.secpwdman.config.StringConstants.shellPY;
import static io.github.seerainer.secpwdman.config.StringConstants.shellSX;
import static io.github.seerainer.secpwdman.config.StringConstants.shellSY;
import static io.github.seerainer.secpwdman.config.StringConstants.tableFo;
import static io.github.seerainer.secpwdman.config.StringConstants.tabul;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.VAULT_FORMAT_LEGACY;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.VAULT_FORMAT_VERSION;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.argon2;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.argon2d;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.argon2id;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.compressionMissing;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.dekMissing;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.pbkdf2;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.unsupportedFormat;
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
import io.github.seerainer.secpwdman.crypto.CryptoConfig;
import io.github.seerainer.secpwdman.util.SWTUtil;
import io.github.seerainer.secpwdman.util.Util;

/**
 * The class JsonUtil.
 */
class JsonUtil {

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

    /**
     * Serializes the vault file JSON.
     *
     * <p>Layout:</p>
     * <pre>
     * {
     *   "appName": "SecPwdMan",
     *   "appVersion": "1.2.0",
     *   "keyALGO": "AES",
     *   "cipherALGO": "AES/GCM/NoPadding",
     *   ...KDF params...,
     *   "encryptedData": "&lt;base64 ciphertext&gt;",
     *   "encryptedDEK":  "&lt;base64 wrapped DEK&gt;",
     *   "dekSalt":       "envelope",
     *   "deflate":       true,
     *   "formatVersion": 2
     * }
     * </pre>
     *
     * @param cData      runtime configuration (its crypto config carries the
     *                   format version the blobs were sealed with)
     * @param ciphertext encrypted vault data (DEK layer)
     * @param wrappedDek encrypted DEK (KEK layer)
     * @return JSON bytes
     */
    static byte[] getJsonFile(final ConfigData cData, final byte[] ciphertext, final byte[] wrappedDek) {
	final var encStr    = new String(Util.getBase64Encode(ciphertext), UTF_8);
	final var dekStr    = new String(Util.getBase64Encode(wrappedDek), UTF_8);
	return getEncryptionValues(cData)
			.value(encData, encStr)
 			.value(encDek,  dekStr)
 			.value(dekSalt, "envelope")
 			.value(deflate, valueOf(cData.getCryptoConfig().isCompress()))
 			.value(formatVersion, cData.getCryptoConfig().getVaultFormatVersion())
		.end()
    	.done().getBytes(UTF_8);
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

    /**
     * Validates that the stream contains a well-formed SecPwdMan vault file.
     * Accepts both new-format files (with {@code encryptedDEK}) and legacy files
     * (without it, for backward compatibility during transition).
     */
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
	cData.setImport(Util.isOldVersion(obj.getString(appVers, MAJOR_VERSION)));
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
	cConf.setCompress(obj.getBoolean(deflate, valueOf(cData.isCompress())));
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
	final var dividerStr = obj.getString(divider, String.valueOf(cData.getDivider()));
	if (dividerStr == null || dividerStr.isEmpty()) {
	    cData.setDivider(DELIMITER);
	} else {
	    cData.setDivider(dividerStr.charAt(0));
	}
	cData.setMaximized(obj.getBoolean(shelMax, valueOf(cData.isMaximized())));
	cData.setPasswordMinLength(obj.getInt(pwdMinL, cData.getPasswordMinLength()));
	cData.setResizeCol(obj.getBoolean(resizeC, valueOf(cData.isResizeCol())));
	cData.setShellFont(obj.getString(shellFo, fontString));
	cData.setShellLocation(new Point(obj.getInt(shellPX, PREF_POS_XY), obj.getInt(shellPY, PREF_POS_XY)));
	cData.setShellSize(new Point(obj.getInt(shellSX, preferredSizeX), obj.getInt(shellSY, PREF_SIZE_Y)));
	cData.setTableFont(obj.getString(tableFo, fontString));
    }

    /**
     * Reads vault file metadata and returns the two envelope-encryption artefacts.
     *
     * <p>
     * New format: both {@code encryptedData} and {@code encryptedDEK} fields are
     * present and base64-encoded.
     * </p>
     *
     * @param cData ConfigData to populate with crypto settings
     * @param is    stream pointing at the vault JSON
     * @return {@link EncryptedFile} containing ciphertext and wrapped DEK
     * @throws JsonParserException      if the JSON is malformed
     * @throws IllegalArgumentException if {@code encryptedDEK} is absent
     */
    static EncryptedFile setJsonFile(final ConfigData cData, final InputStream is) throws JsonParserException {
	final var obj = setEncryptionValues(cData, is);

	final var version = obj.getInt(formatVersion, VAULT_FORMAT_LEGACY);
	if (version < VAULT_FORMAT_LEGACY || version > VAULT_FORMAT_VERSION) {
	    throw new IllegalArgumentException(
		    new StringBuilder().append(unsupportedFormat).append(" ").append(version).toString());
	}
	if (version >= VAULT_FORMAT_VERSION) {
	    if (!obj.has(deflate) || !obj.isBoolean(deflate)) {
		throw new IllegalArgumentException(compressionMissing);
	    }
	    cData.getCryptoConfig().setCompress(obj.getBoolean(deflate, Boolean.FALSE));
	} else {
	    cData.getCryptoConfig().setCompress(cData.isCompress());
	}
	cData.getCryptoConfig().setVaultFormatVersion(version);

	final var dataStr = obj.getString(encData);
	final var dekStr = obj.getString(encDek);

	if (Util.isBlank(dekStr)) {
	    throw new IllegalArgumentException(dekMissing);
	}

	final var ciphertext = Util.getBase64Decode(dataStr != null ? dataStr.getBytes(UTF_8) : new byte[0]);
	final var wrappedDek = Util.getBase64Decode(dekStr.getBytes(UTF_8));

	return new EncryptedFile(Objects.isNull(ciphertext) ? new byte[0] : ciphertext,
		Objects.isNull(wrappedDek) ? new byte[0] : wrappedDek);
    }

    /**
     * Carries the two envelope-encryption artefacts read from a vault file: the raw
     * ciphertext and the wrapped DEK blob.
     */
    record EncryptedFile(byte[] encryptedData, byte[] wrappedDek) {
    }
}
