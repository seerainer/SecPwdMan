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
package io.github.seerainer.secpwdman.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.security.auth.DestroyFailedException;

import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.util.LogFactory;

/**
 * The class KeyStoreManager.
 */
public class KeyStoreManager implements CryptoConstants, StringConstants {

	private static final Logger LOG = LogFactory.getLog();

	private static void destroyProtParam(final PasswordProtection protParam) {
		try {
			protParam.destroy();
		} catch (final DestroyFailedException e) {
			LOG.error(error, e);
		}
	}

	private static KeyStore getInstance() throws KeyStoreException {
		return KeyStore.getInstance(pkcs12);
	}

	/**
	 * Gets the password from the key store.
	 *
	 * @param keyStorePassword the key store password
	 * @param keyStoreData     the key store data
	 * @return the password from key store
	 */
	public static byte[] getPasswordFromKeyStore(final char[] keyStorePassword, final byte[] keyStoreData) {
		if (keyStorePassword == null || keyStoreData == null) {
			throw new IllegalArgumentException(keyStoreNull);
		}
		final var protParam = getPasswordProtection(keyStorePassword);
		try (var bais = new ByteArrayInputStream(keyStoreData)) {
			final var keyStoreInstance = getInstance();
			keyStoreInstance.load(bais, keyStorePassword);
			final var entry = keyStoreInstance.getEntry(alias, protParam);
			if (entry instanceof KeyStore.SecretKeyEntry) {
				return ((KeyStore.SecretKeyEntry) entry).getSecretKey().getEncoded();
			}
			throw new KeyStoreException(noEntryFound);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
				| UnrecoverableEntryException e) {
			LOG.error(error, e);
			return null;
		} finally {
			destroyProtParam(protParam);
		}
	}

	private static PasswordProtection getPasswordProtection(final char[] keyStorePassword) {
		return new KeyStore.PasswordProtection(keyStorePassword);
	}

	/**
	 * Puts the password in the key store.
	 *
	 * @param keyStorePassword the key store password
	 * @param passwordToStore  the password to store
	 * @return the byte array of the key store
	 */
	public static byte[] putPasswordInKeyStore(final char[] keyStorePassword, final byte[] passwordToStore) {
		final var protParam = getPasswordProtection(keyStorePassword);
		try {
			final var keyStoreInstance = getInstance();
			keyStoreInstance.load(null, keyStorePassword);
			final var secretKey = Crypto.getSecretKey(passwordToStore, keyAES);
			final var entry = new KeyStore.SecretKeyEntry(secretKey);
			keyStoreInstance.setEntry(alias, entry, protParam);
			final var baos = new ByteArrayOutputStream();
			keyStoreInstance.store(baos, keyStorePassword);
			return baos.toByteArray();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			LOG.error(error, e);
			return null;
		} finally {
			destroyProtParam(protParam);
		}
	}

	private KeyStoreManager() {
	}
}
