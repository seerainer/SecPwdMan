# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
