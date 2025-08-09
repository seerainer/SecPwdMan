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

import java.io.Serializable;

import io.github.seerainer.secpwdman.util.Util;

/**
 * The class ByteContainer.
 */
public class ByteContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    private final byte[] data;

    /**
     * Instantiates a new ByteContainer.
     *
     * @param data the data
     */
    public ByteContainer(final byte[] data) {
	this.data = data.clone(); // Create defensive copy
    }

    /**
     * Clear the data.
     */
    public void clear() {
	Util.clear(data);
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public byte[] getData() {
	return data.clone(); // Return defensive copy
    }
}
