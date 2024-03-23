/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
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
package io.github.secpwdman.config;

import static io.github.secpwdman.Messages.getString;

import org.eclipse.swt.graphics.Color;

/**
 * The Class ConfData.
 */
public class ConfData {
	public static final String APP_NAME = "SecPwdMan"; //$NON-NLS-1$
	public static final String APP_VERS = "0.7.3"; //$NON-NLS-1$
	public static final String APP_INFO = APP_NAME + "\s" + APP_VERS + getString("APP.Info"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String APP_HEAD = "uuid,group,title,url,user,password,notes"; //$NON-NLS-1$

	public static final boolean WIN32 = System.getProperty("os.name").startsWith("Win"); //$NON-NLS-1$ //$NON-NLS-2$

	private boolean isClearAfterSave = false;
	private boolean isCustomHeader = false;
	private boolean isDarkTheme = false;
	private boolean isExitAfterSave = false;
	private boolean isLocked = false;
	private boolean isModified = false;
	private boolean isReadOnly = false;

	/*
	 * https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet
	 * PBKDF2-HMAC-SHA512: 210,000 iterations
	 */
	private int iterCount = 420000;
	private int clearPasswd = 20;
	private int columnWidth = 167;
	private int pwdMinLength = 8;

	public final char echoChr = '\u25CF';
	public final char nullChr = '\0';

	private Color linkColor;
	private Color textColor;

	private String file = null;
	private String header = null;

	public final String appLink = "<a>www.seerainer.com</a>"; //$NON-NLS-1$
	public final String linkAddress = "https://www.seerainer.com"; //$NON-NLS-1$

	public final String systemThem = "org.eclipse.swt.display.useSystemTheme"; //$NON-NLS-1$
	public final String darkModeTh = "org.eclipse.swt.internal.win32.useDarkModeExplorerTheme"; //$NON-NLS-1$
	public final String shellTitle = "org.eclipse.swt.internal.win32.useShellTitleColoring"; //$NON-NLS-1$
	public final String menuBackgr = "org.eclipse.swt.internal.win32.menuBarBackgroundColor"; //$NON-NLS-1$
	public final String menuForegr = "org.eclipse.swt.internal.win32.menuBarForegroundColor"; //$NON-NLS-1$
	public final String shellBordr = "org.eclipse.swt.internal.win32.all.use_WS_BORDER"; //$NON-NLS-1$

	public final String cCiph = "AES"; //$NON-NLS-1$
	public final String cMode = "AES/GCM/NoPadding"; //$NON-NLS-1$
	public final String keySt = "PBKDF2WithHmacSHA512"; //$NON-NLS-1$

	public final String empty = ""; //$NON-NLS-1$
	public final String space = "\s"; //$NON-NLS-1$
	public final String comma = "\u002C"; //$NON-NLS-1$
	public final String apost = "\u0027"; //$NON-NLS-1$
	public final String grave = "\u0060"; //$NON-NLS-1$
	public final String doubleQ = "\""; //$NON-NLS-1$
	public final String newLine = "\n"; //$NON-NLS-1$
	public final String nullStr = "\0"; //$NON-NLS-1$
	public final String lineBrk = "\\R"; //$NON-NLS-1$

	public final String titlePH = " - "; //$NON-NLS-1$
	public final String titleMD = " - *"; //$NON-NLS-1$

	public final String rTextLoC = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$
	public final String rTextUpC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$
	public final String rNumbers = "1234567890"; //$NON-NLS-1$
	public final String rSpecia1 = "+-=_@#$%^&&"; //$NON-NLS-1$
	public final String rSpecia2 = ";:,.<>/~\\[](){}?!|*"; //$NON-NLS-1$

	public final String imexExte = "*.csv; *.txt"; //$NON-NLS-1$
	public final String passExte = "*.aes"; //$NON-NLS-1$

	public final String imexFile = getString("File.ImpExp.Text"); //$NON-NLS-1$
	public final String passFile = getString("File.Password.Text"); //$NON-NLS-1$
	public final String menuFile = getString("Menu.File"); //$NON-NLS-1$
	public final String menuClea = getString("Menu.File.Clear"); //$NON-NLS-1$
	public final String menuOpen = getString("Menu.File.Open"); //$NON-NLS-1$
	public final String menuSave = getString("Menu.File.Save"); //$NON-NLS-1$
	public final String menuLock = getString("Menu.File.Lock"); //$NON-NLS-1$
	public final String menuUnlo = getString("Menu.File.Unlock"); //$NON-NLS-1$
	public final String menuImpo = getString("Menu.File.Import"); //$NON-NLS-1$
	public final String menuExpo = getString("Menu.File.Export"); //$NON-NLS-1$
	public final String menuExit = getString("Menu.File.Exit"); //$NON-NLS-1$
	public final String menuEdit = getString("Menu.Edit"); //$NON-NLS-1$
	public final String menuNent = getString("Menu.Edit.NewEntry"); //$NON-NLS-1$
	public final String menuEent = getString("Menu.Edit.EditEntry"); //$NON-NLS-1$
	public final String menuVent = getString("Menu.Edit.ViewEntry"); //$NON-NLS-1$
	public final String menuSela = getString("Menu.Edit.SelectAll"); //$NON-NLS-1$
	public final String menuDels = getString("Menu.Edit.DeleteSelected"); //$NON-NLS-1$
	public final String menuCurl = getString("Menu.Edit.CopyURL"); //$NON-NLS-1$
	public final String menuCusr = getString("Menu.Edit.CopyUsername"); //$NON-NLS-1$
	public final String menuCpwd = getString("Menu.Edit.CopyPassword"); //$NON-NLS-1$
	public final String menuCnot = getString("Menu.Edit.CopyNotes"); //$NON-NLS-1$
	public final String menuClCb = getString("Menu.Edit.ClearClipboard"); //$NON-NLS-1$
	public final String menuOurl = getString("Menu.Edit.OpenURL"); //$NON-NLS-1$
	public final String menuView = getString("Menu.View"); //$NON-NLS-1$
	public final String menuReaO = getString("Menu.View.ReadOnly"); //$NON-NLS-1$
	public final String menuPcol = getString("Menu.View.ResizeColumns"); //$NON-NLS-1$
	public final String menuSpwd = getString("Menu.View.ShowPassword"); //$NON-NLS-1$
	public final String menuHpwd = getString("Menu.View.HidePassword"); //$NON-NLS-1$
	public final String menuFont = getString("Menu.View.Font"); //$NON-NLS-1$
	public final String menuText = getString("Menu.View.TextView"); //$NON-NLS-1$
	public final String menuPref = getString("Menu.View.Preferences"); //$NON-NLS-1$
	public final String menuInfo = getString("Menu.Info"); //$NON-NLS-1$
	public final String menuSysI = getString("Menu.InfoSystem"); //$NON-NLS-1$
	public final String cfgTitle = getString("Dialog.Config.Title"); //$NON-NLS-1$
	public final String cfgColWh = getString("Dialog.Config.ColW"); //$NON-NLS-1$
	public final String cfgClPwd = getString("Dialog.Config.ClearPwd"); //$NON-NLS-1$
	public final String cfgPIter = getString("Dialog.Config.Iter"); //$NON-NLS-1$
	public final String cfgMinPl = getString("Dialog.Config.MinPwdLength"); //$NON-NLS-1$
	public final String cfgTestB = getString("Dialog.Config.Test"); //$NON-NLS-1$
	public final String cfgTestI = getString("Dialog.Config.Testinfo"); //$NON-NLS-1$
	public final String entrNewe = getString("Dialog.Entry.New"); //$NON-NLS-1$
	public final String entrEdit = getString("Dialog.Entry.Edit"); //$NON-NLS-1$
	public final String entrView = getString("Dialog.Entry.View"); //$NON-NLS-1$
	public final String entrTitl = getString("Dialog.Entry.Title"); //$NON-NLS-1$
	public final String entrLink = getString("Dialog.Entry.URL"); //$NON-NLS-1$
	public final String entrUser = getString("Dialog.Entry.Username"); //$NON-NLS-1$
	public final String entrPass = getString("Dialog.Entry.Password"); //$NON-NLS-1$
	public final String entrNote = getString("Dialog.Entry.Notes"); //$NON-NLS-1$
	public final String entrPInd = getString("Dialog.Entry.Indicator"); //$NON-NLS-1$
	public final String entrRand = getString("Dialog.Entry.Random"); //$NON-NLS-1$
	public final String entrLgth = getString("Dialog.Entry.Length"); //$NON-NLS-1$
	public final String entrGene = getString("Dialog.Entry.Generate"); //$NON-NLS-1$
	public final String entrShow = getString("Dialog.Entry.ShowPass"); //$NON-NLS-1$
	public final String entrSpac = getString("Dialog.Entry.Space"); //$NON-NLS-1$
	public final String entrOkay = getString("Dialog.Entry.OK"); //$NON-NLS-1$
	public final String entrCanc = getString("Dialog.Entry.Cancel"); //$NON-NLS-1$
	public final String passTitl = getString("Dialog.Password.Title"); //$NON-NLS-1$
	public final String passWord = getString("Dialog.Password.Password"); //$NON-NLS-1$
	public final String passConf = getString("Dialog.Password.Confirm"); //$NON-NLS-1$
	public final String passFair = getString("Dialog.Password.Fair"); //$NON-NLS-1$
	public final String passGood = getString("Dialog.Password.Good"); //$NON-NLS-1$
	public final String passNoMa = getString("Dialog.Password.NoMatch"); //$NON-NLS-1$
	public final String passStro = getString("Dialog.Password.Strong"); //$NON-NLS-1$
	public final String passShor = getString("Dialog.Password.TooShort"); //$NON-NLS-1$
	public final String passSecu = getString("Dialog.Password.VeryStrong"); //$NON-NLS-1$
	public final String passWeak = getString("Dialog.Password.Weak"); //$NON-NLS-1$
	public final String textView = getString("Dialog.TextView"); //$NON-NLS-1$
	public final String textWarn = getString("Dialog.TextWarning"); //$NON-NLS-1$
	public final String systInfo = getString("Dialog.SystemInfo"); //$NON-NLS-1$
	public final String titleErr = getString("MessageBox.Title.Error"); //$NON-NLS-1$
	public final String titleInf = getString("MessageBox.Title.Info"); //$NON-NLS-1$
	public final String titleWar = getString("MessageBox.Title.Warning"); //$NON-NLS-1$
	public final String errorImp = getString("MessageBox.Error.Import"); //$NON-NLS-1$
	public final String errorFil = getString("MessageBox.Error.IO"); //$NON-NLS-1$
	public final String errorLen = getString("MessageBox.Error.Length"); //$NON-NLS-1$
	public final String errorPwd = getString("MessageBox.Error.Password"); //$NON-NLS-1$
	public final String warnNewF = getString("MessageBox.Warning.Changes"); //$NON-NLS-1$
	public final String warnExit = getString("MessageBox.Warning.Exit"); //$NON-NLS-1$
	public final String warnExpo = getString("MessageBox.Warning.Export"); //$NON-NLS-1$
	public final String warnPass = getString("MessageBox.Warning.ShowPass"); //$NON-NLS-1$
	public final String warnUPeq = getString("MessageBox.Warning.UserPassEqual"); //$NON-NLS-1$
	public final String headerOp = getString("Header.Title.open"); //$NON-NLS-1$

	public final String[] defaultHeader = { "UUID", "Group", getString("Header.Title.closed"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			getString("Header.URL"), getString("Header.User"), getString("Header.Pass"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			getString("Header.Notes") }; //$NON-NLS-1$

	/**
	 * Instantiates a new config data.
	 */
	public ConfData() {
	}

	/**
	 * @return the clear passwd
	 */
	public int getClearPasswd() {
		return clearPasswd;
	}

	/**
	 * @return the column width
	 */
	public int getColumnWidth() {
		return columnWidth;
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
	 * @return the iter count
	 */
	public int getIterCount() {
		return iterCount;
	}

	/**
	 * @return the linkColor
	 */
	public Color getLinkColor() {
		return linkColor;
	}

	/**
	 * @return the pwdMinLength
	 */
	public int getPwdMinLength() {
		return pwdMinLength;
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
	 * @return true, if is custom header
	 */
	public boolean isCustomHeader() {
		return isCustomHeader;
	}

	/**
	 * @return true, if is dark theme
	 */
	public boolean isDarkTheme() {
		return isDarkTheme;
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
	 * @param isClearAfterSave the new clear after save
	 */
	public void setClearAfterSave(final boolean isClearAfterSave) {
		this.isClearAfterSave = isClearAfterSave;
	}

	/**
	 * @param clearPasswd the new clear passwd
	 */
	public void setClearPasswd(final int clearPasswd) {
		this.clearPasswd = clearPasswd;
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
	 * @param isDarkTheme the new dark theme
	 */
	public void setDarkTheme(final boolean isDarkTheme) {
		this.isDarkTheme = isDarkTheme;
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
	 * @param iterCount the new iter count
	 */
	public void setIterCount(final int iterCount) {
		this.iterCount = iterCount;
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
	 * @param isModified the new modified
	 */
	public void setModified(final boolean isModified) {
		this.isModified = isModified;
	}

	/**
	 * @param pwdMinLength the pwdMinLength to set
	 */
	public void setPwdMinLength(final int pwdMinLength) {
		this.pwdMinLength = pwdMinLength;
	}

	/**
	 * @param isReadOnly the readonly to set
	 */
	public void setReadOnly(final boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @param textColor the textColor to set
	 */
	public void setTextColor(final Color textColor) {
		this.textColor = textColor;
	}
}
