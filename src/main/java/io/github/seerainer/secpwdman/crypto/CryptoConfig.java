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

import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_ITER;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_ITER_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_ITER_MIN;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_MEMO;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_MEMO_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_MEMO_MIN;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_PARA_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.ARGON2_PARA_MIN;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.PBKDF2_ITER;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.PBKDF2_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.PBKDF2_MIN_SHA256;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.PBKDF2_MIN_SHA512;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_N;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_P_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_P_MIN;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_R;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_R_MAX;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.SCRYPT_R_MIN;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.VAULT_FORMAT_LEGACY;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.cipherAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.keyAES;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.legacyCipherAES;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

/**
 * The class CryptoConfig.
 */
public class CryptoConfig {

    private int argon2Memo = ARGON2_MEMO;
    private int argon2Iter = ARGON2_ITER;
    private int argon2Para = ARGON2_PARA_MIN;
    private int pbkdf2Iter = PBKDF2_ITER;
    private int scryptN = SCRYPT_N[4];
    private int scryptR = SCRYPT_R;
    private int scryptP = SCRYPT_P_MIN;
    private Argon2 argon2Type = Argon2.ID;
    private Hmac hmac = Hmac.SHA256;
    private KDF keyDerivation = KDF.Argon2;
    private String encALGO = cipherAES;
    private String keyALGO = keyAES;
    /**
     * Vault format version of the currently open file ({@code 0} = legacy,
     * pre-AAD). Validity is enforced at the file boundary ({@code JsonUtil}); only
     * freshness matters here.
     */
    private int vaultFormatVersion = VAULT_FORMAT_LEGACY;
    private boolean compress = true;

    /**
     * Instantiates a new CryptoConfig.
     */
    public CryptoConfig() {
    }

    /**
     * @return the argon2Iter
     */
    public int getArgon2Iter() {
	return argon2Iter;
    }

    /**
     * @return the argon2Memo
     */
    public int getArgon2Memo() {
	return argon2Memo;
    }

    /**
     * @return the argon2Para
     */
    public int getArgon2Para() {
	return argon2Para;
    }

    /**
     * @return the argon2Type
     */
    public Argon2 getArgon2Type() {
	return argon2Type;
    }

    /**
     * @return the cipherALGO
     */
    public String getCipherALGO() {
	return encALGO;
    }

    /**
     * @return the hmac
     */
    public Hmac getHmac() {
	return hmac;
    }

    /**
     * @return the keyALGO
     */
    public String getKeyALGO() {
	return keyALGO;
    }

    /**
     * @return the keyDerivation
     */
    public KDF getKeyDerivation() {
	return keyDerivation;
    }

    /**
     * @return the iter of pbkdf2
     */
    public int getPBKDF2Iter() {
	return pbkdf2Iter;
    }

    /**
     * @return the vault format version of the currently open file
     */
    public int getVaultFormatVersion() {
	return vaultFormatVersion;
    }

    public boolean isCompress() {
	return compress;
    }

    /**
     * @return the scryptN
     */
    public int getScryptN() {
	return scryptN;
    }

    /**
     * @return the scryptP
     */
    public int getScryptP() {
	return scryptP;
    }

    /**
     * @return the scryptR
     */
    public int getScryptR() {
	return scryptR;
    }

    /**
     * @param argon2Iter the argon2Iter to set (clamped to ARGON2_ITER_MIN..MAX)
     */
    public void setArgon2Iter(final int argon2Iter) {
	this.argon2Iter = Math.min(ARGON2_ITER_MAX, Math.max(ARGON2_ITER_MIN, argon2Iter));
    }

    /**
     * @param argon2Memo the argon2Memo to set (clamped to ARGON2_MEMO_MIN..MAX)
     */
    public void setArgon2Memo(final int argon2Memo) {
	this.argon2Memo = Math.min(ARGON2_MEMO_MAX, Math.max(ARGON2_MEMO_MIN, argon2Memo));
    }

    /**
     * @param argon2Para the argon2Para to set (clamped to ARGON2_PARA_MIN..MAX)
     */
    public void setArgon2Para(final int argon2Para) {
	this.argon2Para = Math.min(ARGON2_PARA_MAX, Math.max(ARGON2_PARA_MIN, argon2Para));
    }

    /**
     * @param argon2Type the argon2Type to set (null rejected)
     */
    public void setArgon2Type(final Argon2 argon2Type) {
	if (argon2Type == null) {
	    throw new IllegalArgumentException("argon2Type is null.");
	}
	this.argon2Type = argon2Type;
    }

    /**
     * @param cipherALGO the cipherALGO to set (null/blank rejected; the pre-1.x
     *                   spelling {@code AES_256/GCM/NOPADDING} is normalized to the
     *                   canonical {@code AES/GCM/NoPadding})
     */
    public void setCipherALGO(final String cipherALGO) {
	if (cipherALGO == null || cipherALGO.isBlank()) {
	    throw new IllegalArgumentException("cipherALGO is null or blank.");
	}
	this.encALGO = legacyCipherAES.equalsIgnoreCase(cipherALGO) ? cipherAES : cipherALGO;
    }

    /**
     * @param hmac the hmac to set (null rejected)
     */
    public void setHmac(final Hmac hmac) {
	if (hmac == null) {
	    throw new IllegalArgumentException("hmac is null.");
	}
	this.hmac = hmac;
    }

    /**
     * @param keyALGO the keyALGO to set (null/blank rejected)
     */
    public void setKeyALGO(final String keyALGO) {
	if (keyALGO == null || keyALGO.isBlank()) {
	    throw new IllegalArgumentException("keyALGO is null or blank.");
	}
	this.keyALGO = keyALGO;
    }

    /**
     * @param keyDerivation the keyDerivation to set (null rejected)
     */
    public void setKeyDerivation(final KDF keyDerivation) {
	if (keyDerivation == null) {
	    throw new IllegalArgumentException("keyDerivation is null.");
	}
	this.keyDerivation = keyDerivation;
    }

    /**
     * @param pbkdf2Iter the new iter for pbkdf2 (clamped to OWASP minimum..MAX)
     */
    public void setPBKDF2Iter(final int pbkdf2Iter) {
	final var min = hmac == Hmac.SHA512 ? PBKDF2_MIN_SHA512 : PBKDF2_MIN_SHA256;
	this.pbkdf2Iter = Math.min(PBKDF2_MAX, Math.max(min, pbkdf2Iter));
    }

    /**
     * @param scryptN the scryptN to set (snapped to allowed set, minimum 128 =
     *                OWASP N=131072)
     */
    public void setScryptN(final int scryptN) {
	this.scryptN = normalizeScryptN(scryptN);
    }

    /**
     * @param scryptP the scryptP to set (clamped to SCRYPT_P_MIN..MAX)
     */
    public void setScryptP(final int scryptP) {
	this.scryptP = Math.min(SCRYPT_P_MAX, Math.max(SCRYPT_P_MIN, scryptP));
    }

    /**
     * @param scryptR the scryptR to set (clamped to SCRYPT_R_MIN..MAX, hardened
     *                default 8)
     */
    public void setScryptR(final int scryptR) {
	this.scryptR = Math.min(SCRYPT_R_MAX, Math.max(SCRYPT_R_MIN, scryptR));
    }

    public void setCompress(final boolean compress) {
	this.compress = compress;
    }

    /**
     * @param vaultFormatVersion the vault format version to set (no clamping:
     *                           unknown versions must fail closed at the file
     *                           boundary, not silently coerce here)
     */
    public void setVaultFormatVersion(final int vaultFormatVersion) {
	this.vaultFormatVersion = vaultFormatVersion;
    }

    private static int normalizeScryptN(final int scryptN) {
	final var owaspMin = 128;
	if (scryptN <= owaspMin) {
	    return owaspMin;
	}
	for (final var allowed : SCRYPT_N) {
	    if (allowed >= scryptN) {
		return allowed;
	    }
	}
	return SCRYPT_N[SCRYPT_N.length - 1];
    }

    public enum KDF {
	Argon2, PBKDF2, scrypt
    }
}
