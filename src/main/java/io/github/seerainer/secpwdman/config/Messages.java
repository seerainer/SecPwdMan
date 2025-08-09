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
package io.github.seerainer.secpwdman.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class Messages.
 */
class Messages implements StringConstants {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    static String getString(final String key) {
	try {
	    return RESOURCE_BUNDLE.getString(key);
	} catch (final MissingResourceException e) {
	    LogFactory.getLog().error(MISSING_RESOURCE, key);
	    return new StringBuilder().append('!').append(key).append('!').toString();
	}
    }
}
