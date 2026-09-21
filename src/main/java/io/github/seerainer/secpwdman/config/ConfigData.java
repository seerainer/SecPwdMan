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
package io.github.seerainer.secpwdman.config;

import static io.github.seerainer.secpwdman.config.PrimitiveConstants.AUTOLOCK_MAX;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.AUTOLOCK_MIN;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.BUFFER_MAX;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.BUFFER_MIN;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.CLEAR_PWD_MAX;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.CLEAR_PWD_MIN;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.COL_MAX_WIDTH;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.COL_MIN_WIDTH;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.CR;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.DELIMITER;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LF;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PWD_MAX_LENGTH;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PWD_MIN_LENGTH;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.QUOTE_CHAR;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import io.github.seerainer.secpwdman.crypto.CryptoConfig;

/**
 * The class ConfigData.
 */
public class ConfigData {

    private boolean isClearAfterSave = false;
    private boolean isCompress = true;
    private boolean isCustomHeader = false;
    private boolean isExitAfterSave = false;
    private boolean isImport = false;
    private boolean isLocked = false;
    private boolean isMaximized = false;
    private boolean isModified = false;
    private boolean isReadOnly = false;
    private boolean isResizeCol = false;

    private char divider = DELIMITER;

    private int autoLockTime = 5;
    private int clearPassword = 10;
    private int columnWidth = 150;
    private int passwordMinLength = 8;
    private int bufferLength = 1024;

    private Color linkColor;
    private Color textColor;

    private HashMap<String, Integer> columnMap;

    private Point shellLocation;
    private Point shellSize;

    private final CryptoConfig cryptoConfig;
    private final transient SensitiveData sensitiveData;

    private String file = null;
    private String header = null;
    private String shellFont = null;
    private String tableFont = null;
    private String tempFile = null;

    /**
     * Instantiates a new config data.
     */
    public ConfigData() {
	this.cryptoConfig = new CryptoConfig();
	this.sensitiveData = new SensitiveData();
    }

    /**
     * @return the autoLockTime
     */
    public int getAutoLockTime() {
	return autoLockTime;
    }

    /**
     * @return the bufferLength
     */
    public int getBufferLength() {
	return bufferLength;
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
     * @return the cryptoConfig
     */
    public CryptoConfig getCryptoConfig() {
	return cryptoConfig;
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
     * @return the sensitiveData
     */
    public SensitiveData getSensitiveData() {
	return sensitiveData;
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
     * @return the tempFile
     */
    public String getTempFile() {
	return tempFile;
    }

    /**
     * @return the textColor
     */
    public Color getTextColor() {
	return textColor;
    }

    /**
     * @return true, if is clear after save
     */
    public boolean isClearAfterSave() {
	return isClearAfterSave;
    }

    /**
     * @return the isCompress
     */
    public boolean isCompress() {
	return isCompress;
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
     * @return the isImport
     */
    public boolean isImport() {
	return isImport;
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
     * @param autoLockTime the autoLockTime to set (clamped 1..60 minutes)
     */
    public void setAutoLockTime(final int autoLockTime) {
	this.autoLockTime = Math.min(AUTOLOCK_MAX, Math.max(AUTOLOCK_MIN, autoLockTime));
    }

    /**
     * @param bufferLength the bufferLength to set (clamped 64..1MiB)
     */
    public void setBufferLength(final int bufferLength) {
	this.bufferLength = Math.min(BUFFER_MAX, Math.max(BUFFER_MIN, bufferLength));
    }

    /**
     * @param isClearAfterSave the new clear after save
     */
    public void setClearAfterSave(final boolean isClearAfterSave) {
	this.isClearAfterSave = isClearAfterSave;
    }

    /**
     * @param clearPassword the new clear passwd (clamped 5..300 seconds)
     */
    public void setClearPassword(final int clearPassword) {
	this.clearPassword = Math.min(CLEAR_PWD_MAX, Math.max(CLEAR_PWD_MIN, clearPassword));
    }

    /**
     * @param columnMap the columnMap to set
     */
    public void setColumnMap(final HashMap<String, Integer> columnMap) {
	this.columnMap = columnMap;
    }

    /**
     * @param columnWidth the new column width (clamped 10..5000)
     */
    public void setColumnWidth(final int columnWidth) {
	this.columnWidth = Math.min(COL_MAX_WIDTH, Math.max(COL_MIN_WIDTH, columnWidth));
    }

    /**
     * @param isCompress the isCompress to set
     */
    public void setCompress(final boolean isCompress) {
	this.isCompress = isCompress;
    }

    /**
     * @param isCustomHeader the new custom header
     */
    public void setCustomHeader(final boolean isCustomHeader) {
	this.isCustomHeader = isCustomHeader;
    }

    /**
     * @param divider the divider to set (falls back to {@code DELIMITER} for
     *                double-quote, line breaks, and control characters, which would
     *                corrupt the CSV round-trip; TAB is allowed)
     */
    public void setDivider(final char divider) {
	if (divider == QUOTE_CHAR || divider == CR || divider == LF
		|| (Character.isISOControl(divider) && divider != '\t')) {
	    this.divider = DELIMITER;
	    return;
	}
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
     * @param isImport the isImport to set
     */
    public void setImport(final boolean isImport) {
	this.isImport = isImport;
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
     * @param passwordMinLength the passwordMinLength to set (clamped 8..64)
     */
    public void setPasswordMinLength(final int passwordMinLength) {
	this.passwordMinLength = Math.min(PWD_MAX_LENGTH, Math.max(PWD_MIN_LENGTH, passwordMinLength));
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
     * @param tableFont the tableFont to set
     */
    public void setTableFont(final String tableFont) {
	this.tableFont = tableFont;
    }

    /**
     * @param tempFile the tempFile to set
     */
    public void setTempFile(final String tempFile) {
	this.tempFile = tempFile;
    }

    /**
     * @param textColor the textColor to set
     */
    public void setTextColor(final Color textColor) {
	this.textColor = textColor;
    }
}
