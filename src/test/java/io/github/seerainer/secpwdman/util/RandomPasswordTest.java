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

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.KEYSTORE_PWD_LENGTH;
import static io.github.seerainer.secpwdman.config.StringConstants.rNumbers;
import static io.github.seerainer.secpwdman.config.StringConstants.rSpecia1;
import static io.github.seerainer.secpwdman.config.StringConstants.rSpecia2;
import static io.github.seerainer.secpwdman.config.StringConstants.rTextLoC;
import static io.github.seerainer.secpwdman.config.StringConstants.rTextUpC;
import static io.github.seerainer.secpwdman.config.StringConstants.space;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Headless unit tests for keystore password generation. The dialog-driven
 * generator needs SWT widgets and stays untestable in headless CI.
 */
@Tag("unit")
@DisplayName("Random Password Unit Tests")
class RandomPasswordTest {

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Keystore password honors length, pool and randomness contract")
    void keyStorePasswordContract() {
	final var pool = new HashSet<Character>();
	for (final char c : (new StringBuilder().append(rTextLoC).append(rTextUpC).append(rNumbers).append(rSpecia1)
		.append(rSpecia2).append(space).toString()).toCharArray()) {
	    pool.add(Character.valueOf(c));
	}
	final var first = RandomPassword.generateKeyStorePassword();
	final var second = RandomPassword.generateKeyStorePassword();
	assertThat(first).hasSize(KEYSTORE_PWD_LENGTH);
	for (final char c : first) {
	    assertThat(pool).contains(Character.valueOf(c));
	}
	assertThat(second).isNotEqualTo(first);
	Util.clear(first);
	Util.clear(second);
    }
}
