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

import static io.github.seerainer.secpwdman.config.Messages.getString;

/**
 * The class StringConstants.
 */
public final class StringConstants {

    // Application strings
    public static final String APP_NAME = "SecPwdMan";
    public static final String APP_VERS = "1.3.0";
    public static final String APP_INFO = getString("APP.Info");
    // Regular expressions for URL validation
    public static final String DOMAIN_PATTERN = """
    	^(https?:\\/\\/)\
    	(([a-zA-Z0-9_-]+):([a-zA-Z0-9_-]+)@)?\
    	([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\
    	(:\\d+)?\
    	(\\/[^\\s]*)?\
    	$""";
    // SWT
    public static final String useSystemTheme = "org.eclipse.swt.display.useSystemTheme";
    public static final String darkModeExplorerTheme = "org.eclipse.swt.internal.win32.useDarkModeExplorerTheme";
    public static final String shellTitleColoring = "org.eclipse.swt.internal.win32.useShellTitleColoring";
    public static final String menuBarBackgroundColor = "org.eclipse.swt.internal.win32.menuBarBackgroundColor";
    public static final String menuBarForegroundColor = "org.eclipse.swt.internal.win32.menuBarForegroundColor";
    public static final String menuBarBorderColor = "org.eclipse.swt.internal.win32.menuBarBorderColor";
    public static final String use_WS_BORDER = "org.eclipse.swt.internal.win32.all.use_WS_BORDER";
    public static final String useDarkTheme = "org.eclipse.swt.internal.win32.Combo.useDarkTheme";
    public static final String useDarkThemeIcons = "org.eclipse.swt.internal.win32.Text.useDarkThemeIcons";
    // Links
    public static final String appLink = "<a>SecPwdMan</a>";
    public static final String appAddress = "https://github.com/seerainer/SecPwdMan";
    public static final String slfLink = "<a>slf4j</a>";
    public static final String slfAddress = "https://github.com/qos-ch/slf4j";
    public static final String slfLicense = "https://github.com/qos-ch/slf4j/blob/master/LICENSE.txt";
    public static final String zxcLink = "<a>zxcvbn4j</a>";
    public static final String zxcAddress = "https://github.com/nulab/zxcvbn4j";
    public static final String zxcLicense = "https://github.com/nulab/zxcvbn4j/blob/main/LICENSE.txt";
    public static final String mitLink = "(<a>MIT license</a>)";
    public static final String apaLink = "(<a>Apache 2 license</a>)";
    public static final String apaAddress = "https://www.apache.org/licenses/LICENSE-2.0";
    public static final String jsnLink = "<a>nanojson</a>";
    public static final String jsnAddress = "https://github.com/mmastrac/nanojson";
    public static final String jsnLicense = "https://github.com/mmastrac/nanojson?tab=readme-ov-file#license";
    public static final String p4jLink = "<a>password4j</a>";
    public static final String p4jAddress = "https://github.com/Password4j/password4j";
    public static final String swtLink = "<a>eclipse.platform.swt</a>";
    public static final String swtAddress = "https://github.com/eclipse-platform/eclipse.platform.swt";
    public static final String eplLink = "(<a>EPL 2 license</a>)";
    public static final String eplAddress = "https://www.eclipse.org/legal/epl-2.0";
    // General strings
    public static final String empty = "";
    public static final String minus = "-";
    public static final String quote = "\"";
    public static final String space = "\s";
    public static final String tabul = "\t";
    public static final String redot = "\\.";
    public static final String fstop = "\u002E";
    public static final String newLine = "\n";
    public static final String nullStr = "\0";
    public static final String lineBrk = "\\R";
    public static final String logical = "&&";
    public static final String replus = "\\+";
    // Title strings
    public static final String titlePH = " - ";
    public static final String titleMD = " - *";
    // Random characters
    public static final String rTextLoC = "abcdefghijklmnopqrstuvwxyz";
    public static final String rTextUpC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String rNumbers = "1234567890";
    public static final String rSpecia1 = "+-=_@#$%^&&";
    public static final String rSpecia2 = ";:,.<>/~\\[](){}?!|*";
    // Log messages
    public static final String ERROR = "Error occurred";
    public static final String WARN = "Warning occurred";
    public static final String AFFINITY_FAILED = "Failed to set window display affinity";
    public static final String CUSTOM_HEADER = "Custom header created";
    public static final String DATA_NOT_NULL = "Data must not be null";
    public static final String FILE_ERR = "File error: {}{}{}";
    public static final String FILE_NOT_NULL = "File must not be null";
    public static final String FILE_TOO_LARGE = "File too large: {}";
    public static final String MAX_ENTRY = "Data exceeds 100.000 entries";
    public static final String MISSING_RESOURCE = "Missing resource for key: {}";
    public static final String NO_SETTINGS_FILE = "No settings file found, using default settings";
    public static final String TIME_CRYPTO = "Cipher: {}, KDF: {}\nEncrypted: {} ms, Decrypted: {} ms";
    public static final String TIME_TO_OPEN = "Time to open: {} ms";
    public static final String TIME_TO_SAVE = "Time to save: {} ms";
    public static final String TIME_TO_SHRED = "Time to shred file: {} ms";
    public static final String TIME_TO_SORT = "Time to sort: {} ms";
    public static final String START_TIME = "{} - Time to start: {} ms";
    public static final String TOTAL_TIME = "{} - Execution time: {} seconds";
    public static final String DESERIAL_FAILED = "Deserialization failed";
    public static final String SERIAL_FAILED = "Serialization failed";
    public static final String SERIAL_OBJ_FAILED = "Secure serialization failed for object: {}";
    // SecureMemory strings
    public static final String ERR_SECRET_NULL_OR_EMPTY = "Secret data must not be null or empty";
    public static final String ERR_SECURE_MEMORY_OP = "Error in secure memory operation";
    public static final String ERR_SECURE_MEMORY_FAILED = "Secure memory operation failed";
    public static final String WARN_ZERO_NATIVE_MEMORY = "Failed to zero native memory";
    public static final String SECURE_CHARSET_CONVERSION_FAILED = "Secure charset conversion failed";
    // CSV strings
    public static final String csvException = "Line %d, Position %d: %s";
    public static final String fieldSizeMax = "Field size exceeds maximum, max allowed: ";
    public static final String unexpectedQuote = "Unexpected quote in unquoted field at position ";
    public static final String invalidParserState = "Invalid parser state";
    public static final String invalidCharAfterQuote = "Invalid character after quoted field at position ";
    public static final String invalidCharAfterClose = "Invalid character after closing quote at position ";
    public static final String fieldIndex = "Field index ";
    public static final String outOfBounds = " out of bounds";
    public static final String csvField = "CSVField{value='%s', quoted=%s, empty=%s, null=%s, pos=%d-%d, col=%d}";
    public static final String csvRecord = "CSVRecord{fields=%d, line=%d, length=%d, errors=%s}";
    // System information
    public static final String securityProvider = "Security provider";
    public static final String systemEnvi = "System environment variables";
    public static final String systemProp = "System properties";
    // Configuration values
    public static final String CLASS_NOT_INSTANTIABLE = "Class not instantiable";
    public static final String BUNDLE_NAME = "messages";
    public static final String confFile = "config.json";
    public static final String allFExte = "*.*";
    public static final String imexExte = "*.csv; *.txt";
    public static final String passExte = "*.aes; *.json";
    public static final String safeFont = "Arial";
    public static final String consFont = "Courier New";
    public static final String logFileP = new StringBuilder().append("%h/.").append(APP_NAME).append("/")
	    .append(APP_NAME).append(".log.%g.txt").toString();
    public static final String userHome = "user.home";
    public static final String linuxGTK = "gtk";
    public static final String macCocoa = "cocoa";
    public static final String windows = "win32";
    public static final String fileMode = "rws";
    public static final String trueStr = "true";
    public static final String user32 = "user32";
    public static final String setAffinity = "my_SetWindowDisplayAffinity";
    public static final String handle = "handle";
    public static final String MAJOR_VERSION = "1.0.0";
    // Configuration keys
    public static final String appName = "appName";
    public static final String appVers = "appVersion";
    public static final String argon2I = "argon2Iter";
    public static final String argon2M = "argon2Memo";
    public static final String argon2P = "argon2Para";
    public static final String argon2T = "argon2Type";
    public static final String autoLoc = "autoLockTime";
    public static final String buffLen = "bufferLength";
    public static final String cipALGO = "cipherALGO";
    public static final String clearPw = "clearPassword";
    public static final String coWidth = "columnWidth";
    public static final String deflate = "deflate";
    public static final String divider = "divider";
    public static final String encData = "encryptedData";
    public static final String hmacSHA = "HmacSHA2";
    public static final String keyALGO = "keyALGO";
    public static final String keyderf = "keydf";
    public static final String pwdMinL = "passwordMinLength";
    public static final String pbkdf2I = "PBKDF2Iter";
    public static final String resizeC = "resizeColumns";
    public static final String scryptN = "scryptN";
    public static final String scryptP = "scryptP";
    public static final String scryptR = "scryptR";
    public static final String shelMax = "shellMax";
    public static final String shellFo = "shellFont";
    public static final String shellPX = "shellPosX";
    public static final String shellPY = "shellPosY";
    public static final String shellSX = "shellSizeX";
    public static final String shellSY = "shellSizeY";
    public static final String tableFo = "tableFont";

    // Envelope encryption keys (DEK wrapped by KEK)
    public static final String encDek = "encryptedDEK";
    public static final String dekSalt = "dekSalt";
    public static final String formatVersion = "formatVersion";
    // Messages
    public static final String allFiles = getString("File.All.Text");
    public static final String imexFile = getString("File.ImpExp.Text");
    public static final String passFile = getString("File.Password.Text");
    public static final String menuFile = getString("Menu.File");
    public static final String menuClea = getString("Menu.File.Clear");
    public static final String menuOpen = getString("Menu.File.Open");
    public static final String menuSave = getString("Menu.File.Save");
    public static final String menuClos = getString("Menu.File.Close");
    public static final String menuChaP = getString("Menu.File.ChangeKey");
    public static final String menuLock = getString("Menu.File.Lock");
    public static final String menuUnlo = getString("Menu.File.Unlock");
    public static final String menuImpo = getString("Menu.File.Import");
    public static final String menuExpo = getString("Menu.File.Export");
    public static final String menuExit = getString("Menu.File.Exit");
    public static final String menuEdit = getString("Menu.Edit");
    public static final String menuNent = getString("Menu.Edit.NewEntry");
    public static final String menuEent = getString("Menu.Edit.EditEntry");
    public static final String menuVent = getString("Menu.Edit.ViewEntry");
    public static final String menuSela = getString("Menu.Edit.SelectAll");
    public static final String menuDels = getString("Menu.Edit.DeleteSelected");
    public static final String menuCurl = getString("Menu.Edit.CopyURL");
    public static final String menuCusr = getString("Menu.Edit.CopyUsername");
    public static final String menuCpwd = getString("Menu.Edit.CopyPassword");
    public static final String menuCnot = getString("Menu.Edit.CopyNotes");
    public static final String menuClCb = getString("Menu.Edit.ClearClipboard");
    public static final String menuOurl = getString("Menu.Edit.OpenURL");
    public static final String menuSear = getString("Menu.Search");
    public static final String menuFind = getString("Menu.Search.Find");
    public static final String menuView = getString("Menu.View");
    public static final String menuReaO = getString("Menu.View.ReadOnly");
    public static final String menuGrou = getString("Menu.View.Group");
    public static final String menuPcol = getString("Menu.View.ResizeColumns");
    public static final String menuSpwd = getString("Menu.View.ShowPassword");
    public static final String menuHpwd = getString("Menu.View.HidePassword");
    public static final String menuFont = getString("Menu.View.Font");
    public static final String menuFoSh = getString("Menu.View.FontShell");
    public static final String menuFoTa = getString("Menu.View.FontTable");
    public static final String menuText = getString("Menu.View.TextView");
    public static final String menuTool = getString("Menu.Tool");
    public static final String menuPGen = getString("Menu.Tool.PasswordGenerator");
    public static final String menuSecD = getString("Menu.Tool.SecureDelete");
    public static final String menuPref = getString("Menu.Tool.Preferences");
    public static final String menuHelp = getString("Menu.Help");
    public static final String menuAbou = getString("Menu.Help.About");
    public static final String menuSysI = getString("Menu.Help.System");
    public static final String diaCancl = getString("Dialog.Cancel");
    public static final String diaClose = getString("Dialog.Close");
    public static final String dialOkay = getString("Dialog.OK");
    public static final String cfgTitle = getString("Dialog.Config.Title");
    public static final String cfgEnTab = getString("Dialog.Config.EncTab");
    public static final String cfgOpTab = getString("Dialog.Config.OptTab");
    public static final String cfgEncry = getString("Dialog.Config.Encryption");
    public static final String cfgAESGC = getString("Dialog.Config.AES");
    public static final String cfgCHA20 = getString("Dialog.Config.ChaCha20");
    public static final String cfgRecAr = getString("Dialog.Config.ArgonRec");
    public static final String cfgArgon = getString("Dialog.Config.Argon");
    public static final String cfgScryp = getString("Dialog.Config.Scrypt");
    public static final String cfgBuffL = getString("Dialog.Config.BufferLength");
    public static final String cfgDivid = getString("Dialog.Config.Divider");
    public static final String cfgColWh = getString("Dialog.Config.ColW");
    public static final String cfgAutoL = getString("Dialog.Config.AutoLock");
    public static final String cfgClPwd = getString("Dialog.Config.ClearPwd");
    public static final String cfgPIter = getString("Dialog.Config.Iter");
    public static final String cfgMinPl = getString("Dialog.Config.MinPwdLength");
    public static final String cfgLoMin = getString("Dialog.Config.LockOnMin");
    public static final String cfgTestB = getString("Dialog.Config.Test");
    public static final String cfgDefla = getString("Dialog.Config.Deflate");
    public static final String entrNewe = getString("Dialog.Entry.New");
    public static final String entrEdit = getString("Dialog.Entry.Edit");
    public static final String entrView = getString("Dialog.Entry.View");
    public static final String entrGrou = getString("Dialog.Entry.Group");
    public static final String entrTitl = getString("Dialog.Entry.Title");
    public static final String entrLink = getString("Dialog.Entry.URL");
    public static final String entrUser = getString("Dialog.Entry.Username");
    public static final String entrPass = getString("Dialog.Entry.Password");
    public static final String entrNote = getString("Dialog.Entry.Notes");
    public static final String entrPInd = getString("Dialog.Entry.Indicator");
    public static final String entrRand = getString("Dialog.Entry.Random");
    public static final String entrLgth = getString("Dialog.Entry.Length");
    public static final String entrGene = getString("Dialog.Entry.Generate");
    public static final String entrShow = getString("Dialog.Entry.ShowPass");
    public static final String entrSpac = getString("Dialog.Entry.Space");
    public static final String entrCust = getString("Dialog.Entry.CustomValues");
    public static final String infoDepe = getString("Dialog.Info.Dependencies");
    public static final String passTitl = getString("Dialog.Password.Title");
    public static final String passWord = getString("Dialog.Password.Password");
    public static final String passConf = getString("Dialog.Password.Confirm");
    public static final String passFair = getString("Dialog.Password.Weak");
    public static final String passNoMa = getString("Dialog.Password.NoMatch");
    public static final String passStro = getString("Dialog.Password.Strong");
    public static final String passShor = getString("Dialog.Password.TooShort");
    public static final String passSecu = getString("Dialog.Password.VeryStrong");
    public static final String passWeak = getString("Dialog.Password.VeryWeak");
    public static final String passCopy = getString("Dialog.PasswordGenerator.Copy");
    public static final String passCoun = getString("Dialog.PasswordGenerator.Count");
    public static final String passEmpt = getString("Dialog.PasswordGenerator.Empty");
    public static final String searTitl = getString("Dialog.Search.Title");
    public static final String searText = getString("Dialog.Search.Text");
    public static final String textView = getString("Dialog.TextView");
    public static final String textWarn = getString("Dialog.TextWarning");
    public static final String toolPGen = getString("Dialog.Tool.PasswordGenerator");
    public static final String shredFil = getString("Dialog.Tool.ShredFile");
    public static final String systInfo = getString("Dialog.SystemInfo");
    public static final String titleErr = getString("MessageBox.Title.Error");
    public static final String titleInf = getString("MessageBox.Title.Info");
    public static final String titleWar = getString("MessageBox.Title.Warning");
    public static final String cfgTestI = getString("MessageBox.Config.Testinfo");
    public static final String errorFil = getString("MessageBox.Error.FileTooLarge");
    public static final String errorImp = getString("MessageBox.Error.Import");
    public static final String errorInp = getString("MessageBox.Error.Input");
    public static final String errorLen = getString("MessageBox.Error.Length");
    public static final String errorOut = getString("MessageBox.Error.Output");
    public static final String errorPwd = getString("MessageBox.Error.Password");
    public static final String errorSev = getString("MessageBox.Error.Severe");
    public static final String errorShr = getString("MessageBox.Error.Shred");
    public static final String infoImpo = getString("MessageBox.Info.Import");
    public static final String infoNewF = getString("MessageBox.Info.NewFile");
    public static final String searMess = getString("MessageBox.Search.NotFound");
    public static final String warnNewF = getString("MessageBox.Warning.Changes");
    public static final String warnExit = getString("MessageBox.Warning.Exit");
    public static final String warnMaxE = getString("MessageBox.Warning.MaxEntries");
    public static final String warnShre = getString("MessageBox.Warning.Shred");
    public static final String warnUPeq = getString("MessageBox.Warning.UserPassEqual");
    public static final String headerOp = getString("Header.Title.open");
    public static final String listFirs = getString("List.All");
    public static final String[] csvHeader = { "uuid", "group", "title", "url", "user", "password", "notes" };
    public static final String[] tableHeader = { "UUID", "Group", getString("Header.Title.closed"),
	    getString("Header.URL"), getString("Header.User"), getString("Header.Pass"), getString("Header.Notes") };

    private StringConstants() {
	throw new UnsupportedOperationException("Class not instantiable");
    }
}
