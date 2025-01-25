# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-rc.2] - 2025-01-XX

### Added

- Regex pattern for URL validation

### Changed

- Upgrade: Gradle from 8.12 to 8.12.1
- All dialog classes converted to records
- Implemented the Strategy Pattern for key derivation functions
- Extracted confidetial data variables from ConfigData in a new SensitiveData class
- Refactoring the Main class and the MainWindow class

### Removed

- Apache Commons Validator

### Fixed

- Logging messages were fixed
- Some small bugs were fixed

## [1.0.0-rc.1] - 2025-01-10

### Added

- Added drag-and-drop functionality
- Added support for the ChaCha20-Poly1305 encryption algorithm
- Added an option to change the Argon2 type
- Added an option that prevents the password from having to be confirmed every time you save
- Added an option to change the CSV divider (use with caution)
- Added an option to change the font of the table or dialogs, not both
- Window size or if maximized will now be saved
- The settings are now saved in file "config.json"
- ConfigDialog: A TabFolder (CTabFolder) has been added
- Native-image: reachability-metadata.json
- A simple logging feature has been added

### Changed

- Upgrade: Gradle from 8.10.1 to 8.12
- Upgrade dependencies: Eclipse SWT 4.34 (3.128.0)
- The encryption and decryption logic has been completely reworked
- Changed the default Argon2 type from Argon2id to Argon2d (side-channel attacks are unimportant here)
- Changed the trayItem listener from SWT.DefaultSelection to SWT.Selection
- EntryDialog: The entry dialog can now be maximized
- The function for sorting columns has been optimized
- Changed PBKDF2-HMAC-SHA512 to PBKDF2-HMAC-SHA256 and iterations to 600000
- Insecure options (Export, Show Passwords & Text View) can now only be enabled in the configuration dialog
- Tests the password file to make sure it's in the correct file format before opening it
- The file format has been changed and is now no longer compatible with the old format

### Removed

- Native-image: jni-config.json, predefined-classes-config.json, proxy-config.json, reflect-config.json, resource-config.json, serialization-config.json

### Fixed

- Some small bugs were fixed

## [0.9.9] - 2024-09-11

### Changed

- Upgrade: Gradle from 8.8 to 8.10.1
- Upgrade dependencies: Eclipse SWT 4.33 (3.127.0)
- SWTUtil/Util: moved WIN32 variable to SWTUtil and get value from SWT.getPlatform

### Removed

- EntryDialog: removed unnecessary cast from Control to Text

## [0.9.8] - 2024-06-12

### Changed

- Upgrade: Gradle from 8.7 to 8.8
- Upgrade dependencies: Eclipse SWT 4.32 (3.126.0)
- IO: refactoring of path in openFile
- FileAction: refactoring of password dialog in saveDialog

### Removed

- IO: removed unnecessary clear password

## [0.9.7] - 2024-06-10

### Added

- Added a changelog (CHANGELOG.md)

### Changed

- Upgrade dependencies: Apache Commons Validator 1.9.0
- InfoDialog: changed the text and url
- Action: moved the table functions in fillTable outside of the try-catch block

### Removed

- FileAction: removed cData.setArgon2id(true) from clearData function

### Fixed

- ConfigDialog: OK button selection listener set cData modified only if is not locked
- ConfigDialog: disabled key derivation controls if app is locked

### Security

- Crypto: convert password byte array to char array with ByteBuffer and CharBuffеr instead of String toCharArray
