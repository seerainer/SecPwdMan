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
package io.github.seerainer.secpwdman.config;

import java.util.HashMap;

import javax.crypto.SealedObject;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import com.password4j.types.Argon2;

import io.github.seerainer.secpwdman.crypto.CryptoConstants;

/**
 * The Class ConfData.
 */
public final class ConfigData implements CryptoConstants {

	private boolean isArgon2 = true;
	private boolean isClearAfterSave = false;
	private boolean isCustomHeader = false;
	private boolean isExitAfterSave = false;
	private boolean isLocked = false;
	private boolean isMaximized = false;
	private boolean isModified = false;
	private boolean isReadOnly = false;
	private boolean isResizeCol = false;
	private boolean isShowPassword = false;

	private transient byte[] dataKey;
	private transient byte[] keyStoreData;
	private transient byte[] keyStorePassword;

	private char divider = '\u002C';

	private int argonMemo = 32;
	private int argonIter = 10;
	private int argonPara = 1;
	private int pbkdfIter = 600000;
	private int clearPassword = 10;
	private int columnWidth = 150;
	private int passwordMinLength = 8;
	private int bufferLength = 1024;

	private Argon2 argon2Type = Argon2.D;

	private Color linkColor;
	private Color textColor;

	private HashMap<String, Integer> columnMap;

	private Point shellLocation;
	private Point shellSize;

	private transient SealedObject sealedData;

	private String file = null;
	private String header = null;
	private String shellFont = null;
	private String tableFont = null;

	private String encALGO = cipherAES;
	private String keyALGO = keyAES;

	/**
	 * Instantiates a new config data.
	 */
	public ConfigData() {
	}

	/**
	 * @return the argonIter
	 */
	public int getArgonIter() {
		return argonIter;
	}

	/**
	 * @return the argonMemo
	 */
	public int getArgonMemo() {
		return argonMemo;
	}

	/**
	 * @return the argonPara
	 */
	public int getArgonPara() {
		return argonPara;
	}

	/**
	 * @return the argonType
	 */
	public Argon2 getArgonType() {
		return argon2Type;
	}

	/**
	 * @return the bufferLength
	 */
	public int getBufferLength() {
		return bufferLength;
	}

	/**
	 * @return the cipherALGO
	 */
	public String getCipherALGO() {
		return encALGO;
	}

	/**
	 * @return the clear password
	 */
	public int getClearPassword() {
		return clearPassword;
	}

	/**
	 * @return the columnMap
	 */
	public HashMap<String, Integer> getColumnMap() {
		return columnMap;
	}

	/**
	 * @return the column width
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * @return the dataKey
	 */
	public byte[] getDataKey() {
		return dataKey;
	}

	/**
	 * @return the divider
	 */
	public char getDivider() {
		return divider;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @return the keyALGO
	 */
	public String getKeyALGO() {
		return keyALGO;
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
	public byte[] getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @return the linkColor
	 */
	public Color getLinkColor() {
		return linkColor;
	}

	/**
	 * @return the passwordMinLength
	 */
	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	/**
	 * @return the iter of pbkdf2
	 */
	public int getPBKDFIter() {
		return pbkdfIter;
	}

	/**
	 * @return the sealedData
	 */
	public SealedObject getSealedData() {
		return sealedData;
	}

	/**
	 * @return the shellFont
	 */
	public String getShellFont() {
		return shellFont;
	}

	/**
	 * @return the shellLocation
	 */
	public Point getShellLocation() {
		return shellLocation;
	}

	/**
	 * @return the shellSize
	 */
	public Point getShellSize() {
		return shellSize;
	}

	/**
	 * @return the tableFont
	 */
	public String getTableFont() {
		return tableFont;
	}

	/**
	 * @return the textColor
	 */
	public Color getTextColor() {
		return textColor;
	}

	/**
	 * @return the isArgon2
	 */
	public boolean isArgon2() {
		return isArgon2;
	}

	/**
	 * @return true, if is clear after save
	 */
	public boolean isClearAfterSave() {
		return isClearAfterSave;
	}

	/**
	 * @return true, if is custom header
	 */
	public boolean isCustomHeader() {
		return isCustomHeader;
	}

	/**
	 * @return true, if is exit after save
	 */
	public boolean isExitAfterSave() {
		return isExitAfterSave;
	}

	/**
	 * @return true, if is locked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * @return the isMaximized
	 */
	public boolean isMaximized() {
		return isMaximized;
	}

	/**
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * @return the isResizeCol
	 */
	public boolean isResizeCol() {
		return isResizeCol;
	}

	/**
	 * @return the isShowPassword
	 */
	public boolean isShowPassword() {
		return isShowPassword;
	}

	/**
	 * @param isArgon2 the isArgon2 to set
	 */
	public void setArgon2(final boolean isArgon2) {
		this.isArgon2 = isArgon2;
	}

	/**
	 * @param argonIter the argonIter to set
	 */
	public void setArgonIter(final int argonIter) {
		this.argonIter = argonIter;
	}

	/**
	 * @param argonMemo the argonMemo to set
	 */
	public void setArgonMemo(final int argonMemo) {
		this.argonMemo = argonMemo;
	}

	/**
	 * @param argonPara the argonPara to set
	 */
	public void setArgonPara(final int argonPara) {
		this.argonPara = argonPara;
	}

	/**
	 * @param argonType the argonType to set
	 */
	public void setArgonType(final Argon2 argonType) {
		this.argon2Type = argonType;
	}

	/**
	 * @param bufferLength the bufferLength to set
	 */
	public void setBufferLength(final int bufferLength) {
		this.bufferLength = bufferLength;
	}

	/**
	 * @param cipherALGO the cipherALGO to set
	 */
	public void setCipherALGO(final String cipherALGO) {
		this.encALGO = cipherALGO;
	}

	/**
	 * @param isClearAfterSave the new clear after save
	 */
	public void setClearAfterSave(final boolean isClearAfterSave) {
		this.isClearAfterSave = isClearAfterSave;
	}

	/**
	 * @param clearPassword the new clear passwd
	 */
	public void setClearPassword(final int clearPassword) {
		this.clearPassword = clearPassword;
	}

	/**
	 * @param columnMap the columnMap to set
	 */
	public void setColumnMap(final HashMap<String, Integer> columnMap) {
		this.columnMap = columnMap;
	}

	/**
	 * @param columnWidth the new column width
	 */
	public void setColumnWidth(final int columnWidth) {
		this.columnWidth = columnWidth;
	}

	/**
	 * @param isCustomHeader the new custom header
	 */
	public void setCustomHeader(final boolean isCustomHeader) {
		this.isCustomHeader = isCustomHeader;
	}

	/**
	 * @param dataKey the dataKey to set
	 */
	public void setDataKey(final byte[] dataKey) {
		this.dataKey = dataKey;
	}

	/**
	 * @param divider the divider to set
	 */
	public void setDivider(final char divider) {
		this.divider = divider;
	}

	/**
	 * @param isExitAfterSave the new exit after save
	 */
	public void setExitAfterSave(final boolean isExitAfterSave) {
		this.isExitAfterSave = isExitAfterSave;
	}

	/**
	 * @param file the new file
	 */
	public void setFile(final String file) {
		this.file = file;
	}

	/**
	 * @param header the new header
	 */
	public void setHeader(final String header) {
		this.header = header;
	}

	/**
	 * @param keyALGO the keyALGO to set
	 */
	public void setKeyALGO(final String keyALGO) {
		this.keyALGO = keyALGO;
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
	public void setKeyStorePassword(final byte[] keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	/**
	 * @param linkColor the linkColor to set
	 */
	public void setLinkColor(final Color linkColor) {
		this.linkColor = linkColor;
	}

	/**
	 * @param isLocked the new locked
	 */
	public void setLocked(final boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * @param isMaximized the isMaximized to set
	 */
	public void setMaximized(final boolean isMaximized) {
		this.isMaximized = isMaximized;
	}

	/**
	 * @param isModified the new modified
	 */
	public void setModified(final boolean isModified) {
		this.isModified = isModified;
	}

	/**
	 * @param passwordMinLength the passwordMinLength to set
	 */
	public void setPasswordMinLength(final int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	/**
	 * @param pbkdfIter the new iter for pbkdf2
	 */
	public void setPBKDFIter(final int pbkdfIter) {
		this.pbkdfIter = pbkdfIter;
	}

	/**
	 * @param isReadOnly the readonly to set
	 */
	public void setReadOnly(final boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @param isResizeCol the isResize to set
	 */
	public void setResizeCol(final boolean isResizeCol) {
		this.isResizeCol = isResizeCol;
	}

	/**
	 * @param sealedData the sealedData to set
	 */
	public void setSealedData(final SealedObject sealedData) {
		this.sealedData = sealedData;
	}

	/**
	 * @param shellFont the shellFont to set
	 */
	public void setShellFont(final String shellFont) {
		this.shellFont = shellFont;
	}

	/**
	 * @param shellLocation the shellLocation to set
	 */
	public void setShellLocation(final Point shellLocation) {
		this.shellLocation = shellLocation;
	}

	/**
	 * @param shellSize the shellSize to set
	 */
	public void setShellSize(final Point shellSize) {
		this.shellSize = shellSize;
	}

	/**
	 * @param isShowPassword the isShowPassword to set
	 */
	public void setShowPassword(final boolean isShowPassword) {
		this.isShowPassword = isShowPassword;
	}

	/**
	 * @param tableFont the tableFont to set
	 */
	public void setTableFont(final String tableFont) {
		this.tableFont = tableFont;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(final Color textColor) {
		this.textColor = textColor;
	}
}
