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

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class URLUtil.
 */
public final class URLUtil implements PrimitiveConstants, StringConstants {

	/**
	 * Checks if is url.
	 *
	 * @param cData the cData
	 * @param table the table
	 * @return true, if is url
	 */
	public static boolean isUrl(final ConfigData cData, final Table table) {
		return isUrl(table.getSelection()[0].getText(cData.getColumnMap().get(csvHeader[3]).intValue()));
	}

	/**
	 * Checks if is url.
	 *
	 * @param url the string url
	 * @return true, if is url
	 */
	public static boolean isUrl(final String url) {
		if (url == null || url.length() > MAX_URL_LENGTH) {
			return false;
		}
		return Pattern.compile(DOMAIN_PATTERN).matcher(url).matches();
	}

	private URLUtil() {
	}
}
