# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
