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
package io.github.seerainer.secpwdman.util;

import static io.github.seerainer.secpwdman.util.Util.valueOf;

import java.io.InputStream;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.grack.nanojson.JsonWriter;

import io.github.seerainer.secpwdman.config.ConfData;

/**
 * The Class JsonUtil.
 */
public class JsonUtil {

	private static final String appName = "appName"; //$NON-NLS-1$
	private static final String appVers = "appVers"; //$NON-NLS-1$
	private static final String isArgon = "isArgon2id"; //$NON-NLS-1$
	private static final String argonM = "argon2Memo"; //$NON-NLS-1$
	private static final String argonI = "argon2Iter"; //$NON-NLS-1$
	private static final String argonP = "argon2Para"; //$NON-NLS-1$
	private static final String pbkdfI = "pbkdf2Iter"; //$NON-NLS-1$
	private static final String bufferLength = "bufferLength"; //$NON-NLS-1$
	private static final String clearPassword = "clearPassword"; //$NON-NLS-1$
	private static final String columnWidth = "columnWidth"; //$NON-NLS-1$
	private static final String data = "data"; //$NON-NLS-1$

	/**
	 * Read and set config.
	 *
	 * @param cData the ConfData
	 * @param is    the InputStream
	 * @return byte[] the encrypted data
	 * @throws JsonParserException
	 */
	public static byte[] readConfig(final ConfData cData, final InputStream is) throws JsonParserException {
		final var obj = JsonParser.object().from(is);

		cData.setArgon2id(obj.getBoolean(isArgon));
		cData.setArgonMemo(obj.getInt(argonM));
		cData.setArgonIter(obj.getInt(argonI));
		cData.setArgonPara(obj.getInt(argonP));
		cData.setPBKDFIter(obj.getInt(pbkdfI));
		cData.setBufferLength(obj.getInt(bufferLength));
		cData.setClearPassword(obj.getInt(clearPassword));
		cData.setColumnWidth(obj.getInt(columnWidth));

		return obj.getString(data).getBytes();
	}

	/**
	 * Save config.
	 *
	 * @param cData the ConfData
	 * @param b     the encrypted byte[]
	 * @return byte[] json object
	 */
	public static byte[] saveConfig(final ConfData cData, final byte[] bytes) {
		final var jo = new JsonObject();

		jo.put(appName, ConfData.APP_NAME);
		jo.put(appVers, ConfData.APP_VERS);
		jo.put(isArgon, Boolean.valueOf(cData.isArgon2id()));
		jo.put(argonM, valueOf(cData.getArgonMemo()));
		jo.put(argonI, valueOf(cData.getArgonIter()));
		jo.put(argonP, valueOf(cData.getArgonPara()));
		jo.put(pbkdfI, valueOf(cData.getPBKDFIter()));
		jo.put(bufferLength, valueOf(cData.getBufferLength()));
		jo.put(clearPassword, valueOf(cData.getClearPassword()));
		jo.put(columnWidth, valueOf(cData.getColumnWidth()));
		jo.put(data, new String(bytes));

		return JsonWriter.string(jo).getBytes();
	}
}
