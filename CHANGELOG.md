# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **Envelope encryption** following the [OWASP Cryptographic Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html#encrypting-stored-keys): vault data is now encrypted with a randomly generated 256-bit Data Encryption Key (DEK); the DEK is itself encrypted by a Key Encryption Key (KEK) derived from the master password via the configured KDF (Argon2, scrypt, or PBKDF2)
- `EnvelopeCrypto` class implementing DEK generation, DEK wrap/unwrap (KEK layer, always AES-256-GCM), and data encrypt/decrypt (DEK layer, AES-256-GCM or ChaCha20-Poly1305)
- `EnvelopeCryptoTest` — 24 unit tests covering all KDF × cipher × Argon2 × HMAC combinations, password-change-without-re-encryption, wrong-key/wrong-password rejection, and 1 MB data round-trip
- `SecurityFixesTest` — unit tests for KDF parameter clamping, truncated DEK/ciphertext rejection, fail-closed unknown-algorithm handling, and the `SealedObject` AEAD allowlist
- `StorageHardeningTest` — unit tests for `CharArrayString` wiping, `SensitiveData` zero-on-replace, serialization allowlist round-trip, atomic save, and `ConfigData` clamping
- `SWTUtil` resource helpers: guarded `safeDispose` overloads (`Resource`/`Widget`/`DropTarget`), `disposeOnExit` owner tracking, and `setOwnedFont`/`getOwnedFont` custom-font ownership (system/inherited fonts are never disposed)
- Add compression metadata handling in JSON serialization

### Changed

- **Password file format**: the JSON vault file now stores an `encryptedDEK` field (base64-encoded DEK wrapped by the KEK) alongside the existing `encryptedData` field; the master password no longer directly derives the data encryption key
- Changing the master password now only re-wraps the DEK — vault data is not re-encrypted
- `SensitiveData`: added `dek` (plaintext DEK, session-only) and `wrappedDek` fields; the plaintext DEK is zeroed on lock and on close alongside all other key material
- `EncryptionStrategy`, `AESEncryptionStrategy`, `ChaCha20EncryptionStrategy`, `EncryptionContext`: added `encryptWithKey` / `decryptWithKey` methods that accept a pre-built `SecretKey` (DEK) and skip KDF derivation
- `IO.saveFile` / `IO.openFile`: rewired to use envelope encryption; session DEK is generated once per vault and retained in `SensitiveData` while unlocked
- `JsonUtil`: `getJsonFile` now serialises both ciphertext and wrapped DEK; `setJsonFile` returns an `EncryptedFile` record carrying both artefacts
- Constants: refactored all four constant interfaces (`PrimitiveConstants`, `StringConstants`, `CryptoConstants`, `Icons`) from the Constant Interface anti-pattern (Effective Java Item 22) to `final` utility classes with `private` constructors; all usages updated to `static import`
- `CryptoConfig` setters now clamp to safe ranges (Argon2 memo 19..512 / iter 2..256 / para 1..8, PBKDF2 OWASP minimum 600k SHA256 / 210k SHA512, scrypt N snapped to allowlist with floor 128, scrypt P 1..10); null cipher/KDF/HMAC inputs are rejected
- `ConfigData` setters now clamp auto-lock 1..60 min, clipboard clear 5..300 s, buffer 64..1 MiB, column width 10..5000, password min length 8..64
- `ConfigData.setDivider` now rejects CSV-breaking characters (double-quote, CR, LF, other control characters except TAB) and falls back to `DELIMITER`; `StorageHardeningTest` covers the validation
- `IO.save` now writes crash-safe (temp file in target directory + fsync + atomic move) and applies owner-only file permissions; permissions are restricted before writing (no creation-default window) and the parent directory is fsynced best-effort after the move so the rename survives power loss; config directory and log directory are hardened best-effort; `IOUtil.isReadable` no longer requires write access so read-only vaults can be opened
- `IO.open` now returns a size-capped stream (`MAX_FILE_SIZE`) so a file that grows between the size pre-check and the read fails instead of exhausting memory
- `IO.openFile` derives the KEK once per open (unwrap + DEK-decrypt) instead of twice (`unseal` + `unwrapDek`)
- `CharArrayString` rewritten to own wipeable `char[]` buffer (no reflection) with an added `char[]` constructor
- `SensitiveData` setters now zero the previous array before replacing it
- `SerializationUtils.deserialize` now enforces an exact-class `ObjectInputFilter` allowlist (`ByteContainer`, `SealedObject`, `String`, `byte[]`) plus stream limits (`maxdepth`, `maxarray`, `maxbytes`, `maxrefs`) instead of the broad `java.base/*` pattern; `serialize` clears its heap intermediate
- `EnvelopeCrypto.selectStrategy` now fails closed on unknown key algorithms; truncated DEK blobs and ciphertexts are rejected with `IllegalArgumentException` before KDF/cipher use; the wrap path reports `dekWrapFailed` instead of the unwrap message
- `Crypto.generateSealedObject` now accepts only AEAD transformations
- `Action.escapeSpecialChar` now neutralizes CSV formula injection (`=,+,-,@,|, %` prefixed with `'`)
- `AutoLockManager` timeout is recomputed and clamped per cycle instead of once at construction, with disposed-display guards
- SWT resource ownership: `Widgets.toolItem`/`cTabItem`/`menuItem` now track their `Image`s via `disposeOnDispose`; `MainWindow` owns the shared app icon, `TrayItem`, `DropTarget`, and startup fonts; `ViewAction.changeFont` and `MainWindow.initializeShellValues` install fonts via `setOwnedFont`; `InfoDialog` fonts and `Entry`/`System`/`Text`/`PasswordGenerator` dialog icons are freed on dialog dispose
- `FileAction.disposeResources` is now idempotent and guarded: frees both toolbar images (normal + disabled), only `getOwnedFont` fonts (never system/inherited fonts), the shared shell/tray image exactly once, and the `TrayItem` itself
- Removed redundant self-dispose listeners (`Widgets.shell` dispose listener, `Event.dispose` shell-dispose handler)
- AES transformation canonicalized: `CryptoConstants.cipherAES` is now the JCE-standard `AES/GCM/NoPadding` (was the non-standard `AES_256/GCM/NOPADDING`, kept as `legacyCipherAES`); `CryptoConfig.setCipherALGO` normalizes the legacy spelling on load so pre-1.x vault/config files keep opening, and the `SealedObject` AEAD allowlist still accepts it
- Fail-closed crypto configuration: `Crypto.resetConfig` now throws `IllegalArgumentException` on cipher/key mismatch instead of silently rewriting to AES; `CryptoConfig` setters (`cipherALGO`, `keyALGO`, `hmac`, `keyDerivation`, `argon2Type`) throw on null/blank instead of silently keeping the old value; the cipher/key pairing check is case-insensitive for the accepted `ChaCha20-Poly1305` spelling
- Typed DEK-layer errors: `EnvelopeCrypto.encryptWithDek`/`decryptWithDek`/`wrapDek`/`seal` now declare the JCE checked exceptions instead of wrapping everything in `RuntimeException` (single heap copy still zeroed in `finally` — the `withSecretMemory` wrapper added no protection there since `readFromNative` materializes the same heap copy); `IO.saveFile` maps `seal()` failures to the severe-error dialog instead of escaping uncaught, and `IO.openFile` maps unexpected `RuntimeException`s there too while DEK-layer `BadPaddingException` reaches the wrong-password message
- Vault format versioning with AAD binding: new saves write `formatVersion: 1` and bind cipher/key algorithms plus KDF type and parameters as AEAD associated data on both envelope layers (`EnvelopeCrypto.aadFor`); files without the field open via the legacy no-AAD path, opening a legacy file and saving migrates it forward, and newer-than-known versions fail closed in `JsonUtil.setJsonFile` instead of decrypting with the wrong scheme
- Index-free menu handling: new `MenuIds` stable identifiers attached at creation (`Widgets.tag`) with fail-fast `findMenuItem`/`findToolItem` lookups; `enableItems`, `setText`, `hidePasswordColumn`, `resizeColumns`, `showPasswordColumn`, the table-popup sync and the startup resize selection no longer use positional `getItem(N)` indexes; the enablement matrix lives in the headless-testable `MenuStates.enabled` (`MenuStatesTest`, 7 tests) with the toolbar mirroring its menu counterpart
- Per-class logging: `LogFactory.getLog(Class)` replaces the single shared logger so records carry class context; all call sites migrated and the no-arg overload removed
- `SingleInstanceManager` reports lock/close failures through the logger instead of `System.err` (still console-visible pre-`configureLogging` via the JUL root handler)
- Windows native-image hardening: `/DYNAMICBASE`, `/NXCOMPAT`, `/HIGHENTROPYVA` linker flags enabled for release builds; `window_affinity.obj` stays off (built manually via `JNI/build.cmd`, absent on clean checkouts)
- Test coverage: new headless `PasswordStrengthTest` (score-to-text mapping, extracted as `strengthText`) and `RandomPasswordTest` (keystore password length/pool/randomness contract)

### Fixed

- Auto-lock / lock-on-minimize no longer silently skipped when the vault has unsaved changes — `FileAction.setLocked` always locks and clears secrets/table/clipboard
- `FileAction.clearConfidentialData` now also clears `wrappedDek` and `sealedData`
- Clipboard handling: `EditAction` disposes `Clipboard` in `finally`, clears `CharArrayString` intermediates, and auto-clears non-password copies too; `PasswordGeneratorDialog` copies now auto-clear; `Action.clearClipboard` guards disposed displays; generator strength-check slice is cleared
- `RandomPassword` rejection sampling is bounded (100 attempts) so short lengths with many character classes cannot hang the UI thread
- Integration test `shouldCreateAndReadConfiguration` now uses `autoLockTime` 30 instead of 300 (above the 60-minute maximum)
- SWT menu icons: `Widgets.menuItem` no longer disposes the `Image` immediately after `setImage` (use-after-dispose — SWT does not copy); the image now lives until the `MenuItem` is disposed
- SWT dialog icons: `Entry`/`System`/`Text`/`PasswordGenerator` dialogs no longer dispose the app `Image` before `dialog.open()` while the shell still references it; disposal now happens on dialog dispose
- SWT leaks closed: toolbar disabled (gray) images, `CTabItem` images, and replaced fonts from `ViewAction.changeFont` are now disposed; leaked `TrayItem` and `DropTarget` are disposed with the main shell
- SWT double-dispose corrected: the shared shell/tray app icon is freed exactly once, and `disposeResources` no longer disposes system/inherited fonts
- Divider crash fixed: an empty `divider` value in `config.json` no longer throws `StringIndexOutOfBoundsException` in `JsonUtil.setJsonConfig` (uncaught — `IOUtil.openConfig` only handles `IOException`/`JsonParserException`); empty values fall back to `DELIMITER`
- `ConfigDialog` divider apply no longer throws `NullPointerException` when no header exists yet, validates the character through `setDivider` before rewriting the header, and wipes the `Text.getTextChars` buffer after use
- Tray handling no longer relies on `SystemTray` index `0`: the app `TrayItem` is now stored on the main shell (`Widgets.setTrayItem`/`getTrayItem`) and reused by `FileAction`/`Event`, avoiding wrong-item targeting when other tray items exist
- `PasswordStrength.evalPasswordStrength` now honors `ConfigData.getPasswordMinLength()` instead of the static default, so strength feedback matches the configured policy
- `SingleInstanceManager` logger warnings now include full throwables (`LOG.warn(..., e)`) rather than only `e.getMessage()`, restoring stack traces for lock/IO diagnostics
- Improve error handling in CSV processing and file actions
- Refactor methods to ensure secure clearing of sensitive data

## [1.2.0] - 2025-10-23

### Added

- Single instance utility, ensuring only one instance of the application runs at a time

### Changed

- Upgrade dependencies: Eclipse SWT 4.37

### Fixed

- Old password files should now be converted to the new format
- Some small bugs were fixed

## [1.1.1] - 2025-08-10

### Fixed

- Some small bugs were fixed

## [1.1.0] - 2025-08-09

### Added

- Automatic lock
- Own simple CSV parser
- scrypt key derivation function
- PBKDF2 with Hmac SHA-512
- Deflate compression
- Password generator dialog
- An option to add custom character for random password generation
- Feature to securely delete exported files
- Internal password encryption
- Secure memory management
- Screenshot protection on Windows
- Unit and Integration Tests

### Changed

- Upgrade: Gradle from 8.13 to 9.0.0
- Upgrade dependencies: Eclipse SWT 4.36, nanojson 1.10, password4j 1.8.4
- Storing the current key derivation function in the password file replacing the isArgon2 value
- The icon of "Change Master Password" in the file menu
- Coloring all straight lines

### Fixed

- Some small bugs were fixed

### Removed

- sesseltjonna-csv

## [1.0.0] - 2025-02-25

### Changed

- Upgrade: Gradle from 8.12.1 to 8.13
- Upgrade: slf4j from 2.0.16 to 2.0.17
- Configuration and log files are now stored in the users home directory
- Using ByteBuffer.allocateDirect instead of ByteBuffer.wrap

### Fixed

- The password dialog is no longer above the message box
- The KeyStore password was empty
- Some small bugs were fixed

### Security

- Initializing SecureRandom only once

## [1.0.0-rc.2] - 2025-01-25

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
