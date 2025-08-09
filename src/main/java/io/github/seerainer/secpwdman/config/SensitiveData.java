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

/**
 * The class SensitiveData.
 */
public class SensitiveData {

    private transient byte[] dataKey;
    private transient byte[] keyStoreData;
    private transient byte[] sealedData;
    private transient char[] keyStorePassword;

    SensitiveData() {
    }

    /**
     * @return the dataKey
     */
    public byte[] getDataKey() {
	return dataKey;
    }

    /**
     * @return the keyStoreData
     */
    public byte[] getKeyStoreData() {
	return keyStoreData;
    }

    /**
     * @return the keyStorePassword
     */
    public char[] getKeyStorePassword() {
	return keyStorePassword;
    }

    /**
     * @return the sealedData
     */
    public byte[] getSealedData() {
	return sealedData;
    }

    /**
     * @param dataKey the dataKey to set
     */
    public void setDataKey(final byte[] dataKey) {
	this.dataKey = dataKey;
    }

    /**
     * @param keyStoreData the keyStoreData to set
     */
    public void setKeyStoreData(final byte[] keyStoreData) {
	this.keyStoreData = keyStoreData;
    }

    /**
     * @param keyStorePassword the keyStorePassword to set
     */
    public void setKeyStorePassword(final char[] keyStorePassword) {
	this.keyStorePassword = keyStorePassword;
    }

    /**
     * @param sealedData the sealedData to set
     */
    public void setSealedData(final byte[] sealedData) {
	this.sealedData = sealedData;
    }
}
