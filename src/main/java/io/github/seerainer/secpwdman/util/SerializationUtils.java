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
package io.github.seerainer.secpwdman.util;

import static io.github.seerainer.secpwdman.config.StringConstants.DATA_NOT_NULL;
import static io.github.seerainer.secpwdman.config.StringConstants.DESERIAL_FAILED;
import static io.github.seerainer.secpwdman.config.StringConstants.SERIAL_FAILED;
import static io.github.seerainer.secpwdman.config.StringConstants.SERIAL_OBJ_FAILED;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import javax.crypto.SealedObject;

import org.slf4j.Logger;

import io.github.seerainer.secpwdman.io.ByteContainer;

/**
 * The class SerializationUtils - provides secure serialization utilities. This
 * class handles serialization and deserialization of objects with proper memory
 * cleanup and security considerations.
 */
public class SerializationUtils {

    private static final Logger LOG = LogFactory.getLog(SerializationUtils.class);

    /**
     * Exact classes that may appear in a deserialized graph: the sealed vault
     * payload ({@code SealedObject} of {@code ByteContainer}) plus the
     * {@code String} and {@code byte[]} fields those two declare. Everything else —
     * including serializable JDK gadgets such as collections — is rejected.
     */
    private static final Set<Class<?>> ALLOWED_CLASSES = Set.of(ByteContainer.class, SealedObject.class, String.class,
	    byte[].class);

    /**
     * Stream bounds (depth, array length, total bytes, references) sized above the
     * maximum vault size so legitimate payloads never trip them.
     */
    private static final ObjectInputFilter LIMITS = ObjectInputFilter.Config
	    .createFilter("maxdepth=16;maxarray=17825792;maxbytes=18874368;maxrefs=1024");

    private static final ObjectInputFilter ALLOW = ObjectInputFilter.allowFilter(ALLOWED_CLASSES::contains,
	    ObjectInputFilter.Status.REJECTED);

    private SerializationUtils() {
    }

    private static ObjectInputFilter.Status checkInput(final ObjectInputFilter.FilterInfo info) {
	// Limits first: an oversized array must be rejected even when its
	// class is allowlisted; classes then resolve against the exact list.
	final var limited = LIMITS.checkInput(info);
	return limited != ObjectInputFilter.Status.UNDECIDED ? limited : ALLOW.checkInput(info);
    }

    /**
     * Deserializes an object from a byte array with an exact-class allowlist plus
     * stream limits. Only {@code ByteContainer}, {@code javax.crypto.SealedObject},
     * {@code String} and {@code byte[]} are permitted.
     *
     * @param data the serialized data
     * @return the deserialized object
     * @throws IllegalArgumentException if data is null
     */
    public static Object deserialize(final byte[] data) {
	if (isNull(data)) {
	    throw new IllegalArgumentException(DATA_NOT_NULL);
	}

	return SecureMemory.withSecretMemory(data.clone(), dataSegment -> {
	    byte[] secureData = null;
	    try {
		secureData = SecureMemory.readFromNative(dataSegment);
		try (final var bais = new ByteArrayInputStream(secureData);
			final var ois = new ObjectInputStream(bais)) {
		    ois.setObjectInputFilter(SerializationUtils::checkInput);
		    return ois.readObject();
		}
	    } catch (final Exception e) {
		LOG.error(DESERIAL_FAILED, e);
		throw new RuntimeException(DESERIAL_FAILED, e);
	    } finally {
		Util.clear(data);
		Util.clear(secureData);
	    }
	});
    }

    /**
     * Serializes an object to a byte array using secure memory handling. The object
     * must implement Serializable.
     *
     * @param obj the object to serialize
     * @return the serialized data as byte array
     * @throws IOException              if an I/O error occurs
     * @throws IllegalArgumentException if the object is null or not serializable
     */
    public static byte[] serialize(final Serializable obj) throws IOException {
	if (isNull(obj)) {
	    throw new IllegalArgumentException(DATA_NOT_NULL);
	}

	try (final var baos = new ByteArrayOutputStream(); final var oos = new ObjectOutputStream(baos)) {
	    oos.writeObject(obj);
	    oos.flush();

	    final var data = baos.toByteArray();

	    return SecureMemory.withSecretMemory(data, dataSegment -> {
		final var secureData = SecureMemory.readFromNative(dataSegment);

		try {
		    return secureData.clone();
		} finally {
		    Util.clear(data);
		    Util.clear(secureData);
		}
	    });
	} catch (final Exception e) {
	    LOG.error(SERIAL_OBJ_FAILED, obj.getClass().getName(), e);
	    throw new IOException(SERIAL_FAILED, e);
	}
    }
}
