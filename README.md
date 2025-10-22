# 🔐 SecPwdMan

<p align="center">
  <img width="120" height="120" alt="Logo" src="https://github.com/user-attachments/assets/fcdba2df-fb5e-4aba-a3f9-76ab2ff2be03" />
</p>

<p align="center">
<b>SecPwdMan</b> is a cross-platform, open-source password manager that stores all your passwords securely encrypted with a single master password. It is designed for privacy, security, and usability, supporting advanced cryptography, and modern desktop features.
</p>

<p align="center">
  <a href="https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml"><img src="https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml/badge.svg" alt="Build Status"></a>
  <a href="https://github.com/seerainer/SecPwdMan/security/code-scanning"><img src="https://github.com/seerainer/SecPwdMan/workflows/CodeQL/badge.svg" alt="CodeQL"></a>
  <br />
  <a href="https://github.com/seerainer/SecPwdMan/releases"><img src="https://img.shields.io/github/v/release/seerainer/SecPwdMan?style=flat-square" alt="Release"></a>
  <a href="LICENSE.txt"><img src="https://img.shields.io/github/license/seerainer/SecPwdMan?style=flat-square" alt="License"></a>
</p>

---

## Table of Contents
- [Features](#features)
- [Screenshot](#screenshot)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Security](#security)
- [Testing](#testing)
- [Build & Development](#build)
- [Dependencies](#dependencies)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- **Encryption:** AES/GCM (256-bit) or ChaCha20-Poly1305 (256-bit)
- **Key Derivation:** Argon2 (recommended), scrypt or PBKDF2
- **Password Strength Measurement:** Integrated zxcvbn4j
- **Random Password Generator:** Customizable, supports custom characters
- **Automatic Lock:** Locks after inactivity
- **Deflate Compression:** For password file storage
- **Secure File Deletion:** Shreds exported files
- **Screenshot Protection:** Prevents screen capture on Windows
- **Cross-Platform:** Linux, Mac, Windows
- **Modern UI:** Built with Eclipse SWT
- **Import/Export:** CSV support
- **Configurable Table & Dialog Fonts**
- **Theming:** Dark mode
- **Unit & Integration Tests:** Comprehensive test coverage
- **Secure Native Memory:** All sensitive data (passwords, keys) are handled using off-heap native memory (Java Foreign Memory API) via the `SecureMemory` utility, ensuring automatic zeroing and minimizing heap exposure.

---

## Screenshot
![SecPwdMan Screenshot](https://github.com/seerainer/SecPwdMan/assets/50533219/3651e148-d5a7-4f5c-b288-3df4a21ca774)

---

## Installation

### Prerequisites
- **Java 25** (GraalVM recommended for native image)
- **Gradle 8.14+**
- **Git**

### Clone the Repository
```sh
git clone https://github.com/seerainer/SecPwdMan.git
cd SecPwdMan
```

### Build & Run
```sh
./gradlew build
./gradlew run
```

#### Native Image (GraalVM)
```sh
./gradlew nativeCompile
./build/native/nativeCompile/SecPwdMan
```

---

## Usage

### Start the Application
```sh
./gradlew run
```

### Main Features
- Add, edit and delete password entries
- Organize passwords in groups
- Generate strong random passwords
- Import/export as CSV
- Change master password and key derivation settings
- Lock/unlock the application

### Command-Line Arguments
- You can pass a password file as an argument to open it directly:
  ```sh
  ./gradlew run --args='my-passwords.json'
  ```

---

## Configuration
- All configuration and log files are stored in your home directory
- Settings include:
  - Key derivation function (Argon2, scrypt, PBKDF2)
  - Encryption algorithm (AES, ChaCha20)
  - Auto-lock timeout
  - Table and dialog font
  - Window size and position

---

## Security
- **Encryption:** All passwords are encrypted with a strong symmetric cipher
- **Key Derivation:** Uses Argon2, scrypt or PBKDF2 for master password transformation
- **SecureRandom:** Cryptographically secure random number generation
- **Screenshot Protection:** Prevents screen capture on Windows
- **Secure File Deletion:** Shreds exported files to prevent recovery
- **Password Strength:** Integrated zxcvbn4j for strength feedback
- **No Cloud Storage:** All data is local; no remote sync

---

## Testing
- **Unit Tests:**
  - Run with: `./gradlew unitTest`
- **Integration Tests:**
  - Run with: `./gradlew integrationTest`
- **All Tests:**
  - Run with: `./gradlew allTests`
- **Test Reports:**
  - See `build/reports/tests/`

---

## Build

### Build Tasks
- `./gradlew build` — Compile and package
- `./gradlew run` — Run the application
- `./gradlew nativeCompile` — Build native image (GraalVM)
- `./gradlew unitTest` — Run unit tests
- `./gradlew integrationTest` — Run integration tests

### Directory Structure
```
SecPwdMan/
├── src/
│   ├── main/java/io/github/seerainer/secpwdman/   # Source code
│   ├── main/resources/                            # Resource files
│   ├── test/java/io/github/seerainer/secpwdman/   # Test code
│   ├── test/resources/                            # Test resources
├── build.gradle                                   # Build configuration
├── README.md                                      # This file
├── CHANGELOG.md                                   # Changelog
```

---

## Dependencies
- [slf4j](https://github.com/qos-ch/slf4j) — Logging
- [zxcvbn4j](https://github.com/nulab/zxcvbn4j) — Password strength
- [nanojson](https://github.com/mmastrac/nanojson) — JSON parsing
- [password4j](https://github.com/Password4j/password4j) — Argon2
- [Eclipse/SWT](https://github.com/eclipse-platform/eclipse.platform.swt) — GUI
- [JUnit 5](https://junit.org/junit5/) — Testing
- [AssertJ](https://assertj.github.io/doc/) — Fluent assertions

---

## Contributing

Contributions are welcome! Please:
- [Open issues](https://github.com/seerainer/SecPwdMan/issues) for bugs, feature requests or questions
- Fork the repository and submit pull requests

---

## License

SecPwdMan is licensed under the GNU General Public License v3.0. See [LICENSE.txt](LICENSE.txt) for details.