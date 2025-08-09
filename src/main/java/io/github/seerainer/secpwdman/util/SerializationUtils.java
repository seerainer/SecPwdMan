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
package io.github.seerainer.secpwdman.util;

import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;

import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class SerializationUtils - provides secure serialization utilities. This
 * class handles serialization and deserialization of objects with proper memory
 * cleanup and security considerations.
 */
public class SerializationUtils implements StringConstants {

    private static final Logger LOG = LogFactory.getLog();

    private SerializationUtils() {
    }

    /**
     * Deserializes an object from a byte array without type checking.
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
	    try {
		final var secureData = SecureMemory.readFromNative(dataSegment);

		try (final var bais = new ByteArrayInputStream(secureData);
			final var ois = new ObjectInputStream(bais)) {
		    final var obj = ois.readObject();
		    Util.clear(data);
		    return obj;
		} finally {
		    Util.clear(secureData);
		}
	    } catch (final Exception e) {
		LOG.error(DESERIAL_FAILED, e);
		throw new RuntimeException(DESERIAL_FAILED, e);
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
		Util.clear(data);
		return secureData.clone();
	    });
	} catch (final Exception e) {
	    LOG.error(SERIAL_OBJ_FAILED, obj.getClass().getName(), e);
	    throw new IOException(SERIAL_FAILED, e);
	}
    }
}
