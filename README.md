# SecPwdMan

[![Build Status](https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml/badge.svg)](https://github.com/seerainer/SecPwdMan/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/seerainer/SecPwdMan/workflows/CodeQL/badge.svg)](https://github.com/seerainer/SecPwdMan/security/code-scanning)

**SecPwdMan** stores all your passwords securely encrypted with only one master password.

![SecPwdMan Screenshot](https://github.com/seerainer/SecPwdMan/assets/50533219/3651e148-d5a7-4f5c-b288-3df4a21ca774)

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
