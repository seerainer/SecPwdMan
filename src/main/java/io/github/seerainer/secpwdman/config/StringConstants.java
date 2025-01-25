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

import static io.github.seerainer.secpwdman.config.Messages.getString;

/**
 * The interface StringConstants.
 */
public interface StringConstants {

	// Application strings
	String APP_NAME = "SecPwdMan";
	String APP_VERS = "1.0.0-rc.2";
	String APP_INFO = getString("APP.Info");

	// Regular expressions for URL validation
	String DOMAIN_PATTERN = "^(https?://)?(([^:@/]*):([^:@/]*)@)?([a-zA-Z0-9.-]+)(:\\d+)?(/[^\\s]*)?$";

	// Links
	String appLink = "<a>SecPwdMan</a>";
	String appAddress = "https://github.com/seerainer/SecPwdMan";
	String slfLink = "<a>slf4j</a>";
	String slfAddress = "https://github.com/qos-ch/slf4j";
	String slfLicense = "https://github.com/qos-ch/slf4j/blob/master/LICENSE.txt";
	String zxcLink = "<a>zxcvbn4j</a>";
	String zxcAddress = "https://github.com/nulab/zxcvbn4j";
	String zxcLicense = "https://github.com/nulab/zxcvbn4j/blob/main/LICENSE.txt";
	String mitLink = "(<a>MIT license</a>)";
	String apaLink = "(<a>Apache 2 license</a>)";
	String apaAddress = "https://www.apache.org/licenses/LICENSE-2.0";
	String csvLink = "<a>sesseltjonna-csv</a>";
	String csvAddress = "https://github.com/skjolber/sesseltjonna-csv";
	String jsnLink = "<a>nanojson</a>";
	String jsnAddress = "https://github.com/mmastrac/nanojson";
	String p4jLink = "<a>password4j</a>";
	String p4jAddress = "https://github.com/Password4j/password4j";
	String swtLink = "<a>eclipse.platform.swt</a>";
	String swtAddress = "https://github.com/eclipse-platform/eclipse.platform.swt";
	String eplLink = "(<a>EPL 2 license</a>)";
	String eplAddress = "https://www.eclipse.org/legal/epl-2.0";
	String owaLink = "<a>Info</a>";
	String owaAddress = "https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet";

	// General strings
	String empty = "";
	String space = "\s";
	String tabul = "\t";
	String comma = "\u002C";
	String quote = "\"";
	String newLine = "\n";
	String nullStr = "\0";
	String lineBrk = "\\R";

	// Title strings
	String titlePH = " - ";
	String titleMD = " - *";

	// Random characters
	String rTextLoC = "abcdefghijklmnopqrstuvwxyz";
	String rTextUpC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String rNumbers = "1234567890";
	String rSpecia1 = "+-=_@#$%^&&";
	String rSpecia2 = ";:,.<>/~\\[](){}?!|*";

	// Configuration values
	String confFile = "config.json";
	String imexExte = "*.csv; *.txt";
	String passExte = "*.aes; *.json";
	String safeFont = "Arial";
	String consFont = "Consolas";
	String tempFold = "%t/";
	String logExten = ".log.%g.txt";

	// Configuration keys
	String appName = "appName";
	String appVers = "appVersion";
	String argon2I = "argon2Iter";
	String argon2M = "argon2Memo";
	String argon2P = "argon2Para";
	String argon2T = "argon2Type";
	String buffLen = "bufferLength";
	String cipALGO = "cipherALGO";
	String clearPw = "clearPassword";
	String coWidth = "columnWidth";
	String divider = "divider";
	String encData = "encryptedData";
	String isArgon = "isArgon2";
	String keyALGO = "keyALGO";
	String pwdMinL = "passwordMinLength";
	String pbkdf2I = "PBKDF2Iter";
	String resizeC = "resizeColumns";
	String shelMax = "shellMax";
	String shellFo = "shellFont";
	String shellPX = "shellPosX";
	String shellPY = "shellPosY";
	String shellSX = "shellSizeX";
	String shellSY = "shellSizeY";
	String shwPass = "showPasswords";
	String tableFo = "tableFont";

	// Messages
	String imexFile = getString("File.ImpExp.Text");
	String passFile = getString("File.Password.Text");
	String menuFile = getString("Menu.File");
	String menuClea = getString("Menu.File.Clear");
	String menuOpen = getString("Menu.File.Open");
	String menuSave = getString("Menu.File.Save");
	String menuChaP = getString("Menu.File.ChangeKey");
	String menuLock = getString("Menu.File.Lock");
	String menuUnlo = getString("Menu.File.Unlock");
	String menuImpo = getString("Menu.File.Import");
	String menuExpo = getString("Menu.File.Export");
	String menuExit = getString("Menu.File.Exit");
	String menuEdit = getString("Menu.Edit");
	String menuNent = getString("Menu.Edit.NewEntry");
	String menuEent = getString("Menu.Edit.EditEntry");
	String menuVent = getString("Menu.Edit.ViewEntry");
	String menuSela = getString("Menu.Edit.SelectAll");
	String menuDels = getString("Menu.Edit.DeleteSelected");
	String menuCurl = getString("Menu.Edit.CopyURL");
	String menuCusr = getString("Menu.Edit.CopyUsername");
	String menuCpwd = getString("Menu.Edit.CopyPassword");
	String menuCnot = getString("Menu.Edit.CopyNotes");
	String menuClCb = getString("Menu.Edit.ClearClipboard");
	String menuOurl = getString("Menu.Edit.OpenURL");
	String menuSear = getString("Menu.Search");
	String menuFind = getString("Menu.Search.Find");
	String menuView = getString("Menu.View");
	String menuReaO = getString("Menu.View.ReadOnly");
	String menuGrou = getString("Menu.View.Group");
	String menuPcol = getString("Menu.View.ResizeColumns");
	String menuSpwd = getString("Menu.View.ShowPassword");
	String menuHpwd = getString("Menu.View.HidePassword");
	String menuFont = getString("Menu.View.Font");
	String menuFoSh = getString("Menu.View.FontShell");
	String menuFoTa = getString("Menu.View.FontTable");
	String menuText = getString("Menu.View.TextView");
	String menuPref = getString("Menu.View.Preferences");
	String menuInfo = getString("Menu.Help");
	String menuAbou = getString("Menu.Help.About");
	String menuSysI = getString("Menu.Help.System");
	String cfgTitle = getString("Dialog.Config.Title");
	String cfgEnTab = getString("Dialog.Config.EncTab");
	String cfgOpTab = getString("Dialog.Config.OptTab");
	String cfgEncry = getString("Dialog.Config.Encryption");
	String cfgAESGC = getString("Dialog.Config.AES");
	String cfgCHA20 = getString("Dialog.Config.ChaCha20");
	String cfgKeyDF = getString("Dialog.Config.KeyDF");
	String cfgRecAr = getString("Dialog.Config.ArgonRec");
	String cfgArgon = getString("Dialog.Config.Argon");
	String cfgBuffL = getString("Dialog.Config.BufferLength");
	String cfgDivid = getString("Dialog.Config.Divider");
	String cfgColWh = getString("Dialog.Config.ColW");
	String cfgClPwd = getString("Dialog.Config.ClearPwd");
	String cfgPIter = getString("Dialog.Config.Iter");
	String cfgMinPl = getString("Dialog.Config.MinPwdLength");
	String cfgLoMin = getString("Dialog.Config.LockOnMin");
	String cfgTestB = getString("Dialog.Config.Test");
	String cfgDWarn = getString("Dialog.Config.Warn");
	String cfgShPwd = getString("Dialog.Config.ShowPass");
	String entrNewe = getString("Dialog.Entry.New");
	String entrEdit = getString("Dialog.Entry.Edit");
	String entrView = getString("Dialog.Entry.View");
	String entrGrou = getString("Dialog.Entry.Group");
	String entrTitl = getString("Dialog.Entry.Title");
	String entrLink = getString("Dialog.Entry.URL");
	String entrUser = getString("Dialog.Entry.Username");
	String entrPass = getString("Dialog.Entry.Password");
	String entrNote = getString("Dialog.Entry.Notes");
	String entrPInd = getString("Dialog.Entry.Indicator");
	String entrRand = getString("Dialog.Entry.Random");
	String entrLgth = getString("Dialog.Entry.Length");
	String entrGene = getString("Dialog.Entry.Generate");
	String entrShow = getString("Dialog.Entry.ShowPass");
	String entrSpac = getString("Dialog.Entry.Space");
	String entrOkay = getString("Dialog.Entry.OK");
	String entrCanc = getString("Dialog.Entry.Cancel");
	String infoDepe = getString("Dialog.Info.Dependencies");
	String passTitl = getString("Dialog.Password.Title");
	String passWord = getString("Dialog.Password.Password");
	String passConf = getString("Dialog.Password.Confirm");
	String passFair = getString("Dialog.Password.Weak");
	String passNoMa = getString("Dialog.Password.NoMatch");
	String passStro = getString("Dialog.Password.Strong");
	String passShor = getString("Dialog.Password.TooShort");
	String passSecu = getString("Dialog.Password.VeryStrong");
	String passWeak = getString("Dialog.Password.VeryWeak");
	String searTitl = getString("Dialog.Search.Title");
	String searText = getString("Dialog.Search.Text");
	String textView = getString("Dialog.TextView");
	String textWarn = getString("Dialog.TextWarning");
	String systInfo = getString("Dialog.SystemInfo");
	String titleErr = getString("MessageBox.Title.Error");
	String titleInf = getString("MessageBox.Title.Info");
	String titleWar = getString("MessageBox.Title.Warning");
	String cfgTestI = getString("MessageBox.Config.Testinfo");
	String errorImp = getString("MessageBox.Error.Import");
	String errorFil = getString("MessageBox.Error.IO");
	String errorLen = getString("MessageBox.Error.Length");
	String errorPwd = getString("MessageBox.Error.Password");
	String errorSev = getString("MessageBox.Error.Severe");
	String infoImpo = getString("MessageBox.Info.Import");
	String searMess = getString("MessageBox.Search.NotFound");
	String warnNewF = getString("MessageBox.Warning.Changes");
	String warnExit = getString("MessageBox.Warning.Exit");
	String warnPass = getString("MessageBox.Warning.ShowPass");
	String warnExpo = getString("MessageBox.Warning.Export");
	String warnUPeq = getString("MessageBox.Warning.UserPassEqual");
	String headerOp = getString("Header.Title.open");
	String listFirs = getString("List.All");

	String[] csvHeader = { "uuid", "group", "title", "url", "user", "password", "notes" };
	String[] tableHeader = { "UUID", "Group", getString("Header.Title.closed"), getString("Header.URL"),
			getString("Header.User"), getString("Header.Pass"), getString("Header.Notes") };
}
