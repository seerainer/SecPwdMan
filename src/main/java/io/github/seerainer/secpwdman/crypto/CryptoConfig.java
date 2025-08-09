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
package io.github.seerainer.secpwdman.crypto;

import com.password4j.types.Argon2;
import com.password4j.types.Hmac;

/**
 * The class CryptoConfig.
 */
public class CryptoConfig implements CryptoConstants {

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
     * @param argon2Iter the argon2Iter to set
     */
    public void setArgon2Iter(final int argon2Iter) {
	this.argon2Iter = argon2Iter;
    }

    /**
     * @param argon2Memo the argon2Memo to set
     */
    public void setArgon2Memo(final int argon2Memo) {
	this.argon2Memo = argon2Memo;
    }

    /**
     * @param argon2Para the argon2Para to set
     */
    public void setArgon2Para(final int argon2Para) {
	this.argon2Para = argon2Para;
    }

    /**
     * @param argon2Type the argon2Type to set
     */
    public void setArgon2Type(final Argon2 argon2Type) {
	this.argon2Type = argon2Type;
    }

    /**
     * @param cipherALGO the cipherALGO to set
     */
    public void setCipherALGO(final String cipherALGO) {
	this.encALGO = cipherALGO;
    }

    /**
     * @param hmac the hmac to set
     */
    public void setHmac(final Hmac hmac) {
	this.hmac = hmac;
    }

    /**
     * @param keyALGO the keyALGO to set
     */
    public void setKeyALGO(final String keyALGO) {
	this.keyALGO = keyALGO;
    }

    /**
     * @param keyDerivation the keyDerivation to set
     */
    public void setKeyDerivation(final KDF keyDerivation) {
	this.keyDerivation = keyDerivation;
    }

    /**
     * @param pbkdf2Iter the new iter for pbkdf2
     */
    public void setPBKDF2Iter(final int pbkdf2Iter) {
	this.pbkdf2Iter = pbkdf2Iter;
    }

    /**
     * @param scryptN the scryptN to set
     */
    public void setScryptN(final int scryptN) {
	this.scryptN = scryptN;
    }

    /**
     * @param scryptP the scryptP to set
     */
    public void setScryptP(final int scryptP) {
	this.scryptP = scryptP;
    }

    /**
     * @param scryptR the scryptR to set
     */
    public void setScryptR(final int scryptR) {
	this.scryptR = scryptR;
    }

    public enum KDF {
	Argon2, PBKDF2, scrypt
    }
}
