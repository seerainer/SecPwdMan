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

import static io.github.seerainer.secpwdman.config.StringConstants.passFair;
import static io.github.seerainer.secpwdman.config.StringConstants.passSecu;
import static io.github.seerainer.secpwdman.config.StringConstants.passStro;
import static io.github.seerainer.secpwdman.config.StringConstants.passWeak;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Headless unit tests for the zxcvbn score-to-text mapping. The full evaluation
 * needs an SWT label and stays untestable in headless CI.
 */
@Tag("unit")
@DisplayName("Password Strength Unit Tests")
class PasswordStrengthTest {

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Score mapping covers all zxcvbn scores")
    void scoreMapping() {
	assertThat(PasswordStrength.strengthText(0)).isEqualTo(passWeak);
	assertThat(PasswordStrength.strengthText(1)).isEqualTo(passWeak);
	assertThat(PasswordStrength.strengthText(2)).isEqualTo(passFair);
	assertThat(PasswordStrength.strengthText(3)).isEqualTo(passStro);
	assertThat(PasswordStrength.strengthText(4)).isEqualTo(passSecu);
    }

    @SuppressWarnings("static-method")
    @Test
    @DisplayName("Out-of-range scores fall back to weak")
    void outOfRangeScoresAreWeak() {
	assertThat(PasswordStrength.strengthText(-1)).isEqualTo(passWeak);
	assertThat(PasswordStrength.strengthText(5)).isEqualTo(passWeak);
    }
}
