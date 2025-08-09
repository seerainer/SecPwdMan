# 🔐 SecPwdMan

<p align="center">
  <img width="120" height="120" alt="Logo" src="https://github.com/user-attachments/assets/fcdba2df-fb5e-4aba-a3f9-76ab2ff2be03" />
</p>

<p align="center">
<b>SecPwdMan</b> is a cross-platform, open-source password manager that stores all your passwords securely encrypted with a single master password. It is designed for privacy, security, and usability, supporting advanced cryptography, and modern desktop features.
</p>

<p align="center">
  <a href="https://github.com/seerainer/SecPwdMan/releases"><img src="https://img.shields.io/github/v/release/seerainer/SecPwdMan?style=flat-square" alt="Release"></a>
  <a href="LICENSE.txt"><img src="https://img.shields.io/github/license/seerainer/SecPwdMan?style=flat-square" alt="License"></a>
  <a href="https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml"><img src="https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml/badge.svg" alt="Build Status"></a>
  <a href="https://github.com/seerainer/SecPwdMan/security/code-scanning"><img src="https://github.com/seerainer/SecPwdMan/workflows/CodeQL/badge.svg" alt="CodeQL"></a>
</p>

---

## Features

- **Encryption**: AES/GCM (256-bit) or ChaCha20-Poly1305 (256-bit)
- **Key Transformation**: Argon2 (Recommended) or PBKDF2-HMAC-SHA256
- **Password Strength Measurement**
- **Random Password Generator**
- **Cross-Platform**: Linux, Mac & Windows

## Dependencies

- [slf4j](https://github.com/qos-ch/slf4j) ([MIT license](https://github.com/qos-ch/slf4j/blob/master/LICENSE.txt))
- [zxcvbn4j](https://github.com/nulab/zxcvbn4j) ([MIT license](https://github.com/nulab/zxcvbn4j/blob/main/LICENSE.txt))
- [eclipse/swt](https://github.com/eclipse-platform/eclipse.platform.swt) ([EPL 2 license](https://www.eclipse.org/legal/epl-2.0/))
- [nanojson](https://github.com/mmastrac/nanojson) ([Apache 2 license](https://www.apache.org/licenses/LICENSE-2.0))
- [password4j](https://github.com/Password4j/password4j) ([Apache 2 license](https://www.apache.org/licenses/LICENSE-2.0))
- [sesseltjonna-csv](https://github.com/skjolber/sesseltjonna-csv) ([Apache 2 license](https://www.apache.org/licenses/LICENSE-2.0))

## Installation

Clone the repository:

~~~ sh
git clone https://github.com/seerainer/SecPwdMan.git
~~~

## Usage

To run the application, use the following command:

~~~ sh
./gradlew run
~~~

## Bugs and Feedback

For bugs, questions, and discussions, please use the [GitHub Issues](https://github.com/seerainer/SecPwdMan/issues).
