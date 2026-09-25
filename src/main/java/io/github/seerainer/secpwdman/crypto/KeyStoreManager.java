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
package io.github.seerainer.secpwdman.crypto;

import static io.github.seerainer.secpwdman.config.StringConstants.ERROR;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.alias;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.keyAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.noEntryFound;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.pkcs12;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;

import io.github.seerainer.secpwdman.util.LogFactory;
import io.github.seerainer.secpwdman.util.SecureMemory;
import io.github.seerainer.secpwdman.util.Util;

/**
 * The class KeyStoreManager.
 */
public class KeyStoreManager {

    private static final Logger LOG = LogFactory.getLog(KeyStoreManager.class);

    private KeyStoreManager() {
    }

    private static void destroyProtParam(final PasswordProtection protParam) {
	try {
	    protParam.destroy();
	} catch (final DestroyFailedException e) {
	    LOG.error(ERROR, e);
	}
    }

    /**
     * Gets the password from the key store using secure native memory.
     *
     * @param keyStorePassword the key store password
     * @param keyStoreData     the key store data
     * @return the password from key store
     */
    public static byte[] getPasswordFromKeyStore(final char[] keyStorePassword, final byte[] keyStoreData) {
	return SecureMemory.withSecretMemory(keyStoreData.clone(), dataSegment -> {
	    byte[] data = null;
	    final var protParam = new KeyStore.PasswordProtection(keyStorePassword);
	    try {
		data = SecureMemory.readFromNative(dataSegment);
		final var bais = new ByteArrayInputStream(data);
		final var keyStoreInstance = KeyStore.getInstance(pkcs12);
		keyStoreInstance.load(bais, keyStorePassword);
		final var entry = keyStoreInstance.getEntry(alias, protParam);
		if (entry instanceof KeyStore.SecretKeyEntry) {
		    return ((KeyStore.SecretKeyEntry) entry).getSecretKey().getEncoded();
		}
		throw new KeyStoreException(noEntryFound);
	    } catch (final Exception e) {
		LOG.error(ERROR, e);
		return null;
	    } finally {
		Util.clear(data);
		destroyProtParam(protParam);
	    }
	});
    }

    /**
     * Puts the password in the key store using secure native memory.
     *
     * @param keyStorePassword the key store password
     * @param passwordToStore  the password to store
     * @return the byte array of the key store
     */
    public static byte[] putPasswordInKeyStore(final char[] keyStorePassword, final byte[] passwordToStore) {
	return SecureMemory.withSecretMemory(passwordToStore.clone(), passwordSegment -> {
	    byte[] password = null;
	    final var protParam = new KeyStore.PasswordProtection(keyStorePassword);
	    try {
		password = SecureMemory.readFromNative(passwordSegment);
		final var keyStoreInstance = KeyStore.getInstance(pkcs12);
		keyStoreInstance.load(null, keyStorePassword);
		final var secretKey = Crypto.getSecretKey(password, keyAES);
		final var entry = new KeyStore.SecretKeyEntry(secretKey);
		keyStoreInstance.setEntry(alias, entry, protParam);
		final var baos = new ByteArrayOutputStream();
		keyStoreInstance.store(baos, keyStorePassword);
		return baos.toByteArray();
	    } catch (final Exception e) {
		LOG.error(ERROR, e);
		return null;
	    } finally {
		Util.clear(password);
		destroyProtParam(protParam);
	    }
	});
    }
}