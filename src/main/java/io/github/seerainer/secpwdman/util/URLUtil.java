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

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.config.ConfData;

/**
 * The Class URLUtil.
 */
public class URLUtil {

	/**
	 * Checks if is url.
	 *
	 * @param cData the cData
	 * @param table the table
	 * @return true, if is url
	 */
	public static boolean isUrl(final ConfData cData, final Table table) {
		final var index = cData.getColumnMap().get(cData.csvHeader[3]).intValue();
		return isUrl(table.getSelection()[0].getText(index));
	}

	/**
	 * Checks if is url.
	 *
	 * @param url the string url
	 * @return true, if is url
	 */
	public static boolean isUrl(final String url) {
		return UrlValidator.getInstance().isValid(url);
	}

	private URLUtil() {
	}
}
