# SecPwdMan — Agent Guide

## Build & Run

```
gradlew build               # compile + test + jar
gradlew classes             # compile only
gradlew run                 # run the application
gradlew nativeCompile       # GraalVM native image (requires GraalVM JDK)
```

- **Java 25** required. Toolchain is auto-provisioned via `foojay-resolver-convention` for normal builds. `nativeCompile` requires GraalVM 25; on Windows it also requires MSVC (Visual Studio).
- Native image output: `build/native/nativeCompile/SecPwdMan`
- **Configuration cache is disabled** (`org.gradle.configuration-cache=false`). Do not enable it.
- **SWT artifact is resolved at Gradle configuration time** from `os.name`/`os.arch`. Unsupported OS or arch throws `GradleException` immediately — not at link time.
- On non-Windows, `ForeignRegistrationFeature` and `Win32Affinity` are excluded from the JAR automatically.
- `gradlew.bat` uses CRLF on Windows; `gradlew` is forced LF via `.gitattributes` — never convert it.

## Testing

Tests are **tag-based**, all in one source set (`src/test`):

| Task | Tags | Notes |
|---|---|---|
| `gradlew test` | `unit` + `integration` | Runs both |
| `gradlew unitTest` | `unit` only | |
| `gradlew integrationTest` | `integration` only | 10-min task timeout |
| `gradlew allTests` | both, sequential | CI command |

`gradlew build` and `gradlew check` both trigger `unitTest` + `integrationTest`.

**Do not infer tag from class name:**
- `CSVParserTests` → `@Tag("integration")`
- `CSVParserBenchmark` → `@Tag("integration")`
- `SecureMemoryTest` → `@Tag("integration")`
- `CryptoTest` → `@Tag("unit")`
- `SecPwdManIntegrationTest` → `@Tag("integration")`

Run a focused test (use `test`, `unitTest`, or `integrationTest` depending on the tag):
```
gradlew test --tests "io.github.seerainer.secpwdman.crypto.CryptoTest"
gradlew test --tests "io.github.seerainer.secpwdman.crypto.CryptoTest.methodName"
```

Test JVM quirks (already configured in `build.gradle`, do not duplicate):
- 4 GB max heap — OOM possible on constrained machines
- `--enable-native-access=ALL-UNNAMED` required (Foreign Memory API)
- `--add-opens` for `java.lang`, `java.security`, `javax.crypto`
- Parallel execution enabled by default
- `java.awt.headless=true` set as system property

## Code Style

No automated formatter or linter — conventions are not build-enforced.

- **Indentation:** 4 spaces (tabs in existing resource-heavy files are legacy).
- **Copyright header:** All new Java files must include the GPLv3 header from existing files.
- **Imports:** No wildcard imports. `static` imports first (individual, not wildcard where feasible), then standard Java, third-party, project.
- **Local variables:** `final var` where type is clear from the RHS.
- **Logging:** Use `io.github.seerainer.secpwdman.util.LogFactory`, never `System.out`. Pattern: `private static final Logger LOG = LogFactory.getLog();`

## Constants

Constants live in **`final` utility classes** (not interfaces) with a private constructor. Access via `static import`:

```java
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.*;
import static io.github.seerainer.secpwdman.config.StringConstants.*;
import static io.github.seerainer.secpwdman.crypto.CryptoConstants.*;
import static io.github.seerainer.secpwdman.config.Icons.*;
```

Do **not** use `implements` on these classes — they are not interfaces.

## Package Map

```
io.github.seerainer.secpwdman
  (root)          Main — entry point, Display/Shell init
  action/         Action (abstract base) + EditAction, FileAction, ViewAction
  config/         PrimitiveConstants, StringConstants, Icons (final util classes)
                  ConfigData (runtime config state), Messages (i18n loader)
  crypto/         CryptoConstants (final util class), Crypto, CryptoConfig,
                  encryption strategies (AES, ChaCha20), KDF strategies (Argon2, PBKDF2, scrypt)
  csv/            CSVParser and supporting types
  io/             File I/O, JSON serialization, ByteContainer
  ui/             SWT dialogs and MainWindow
  util/           LogFactory, SecureMemory, SWTUtil, URLUtil, RandomPassword, etc.
```

## SWT Notes

- UI resources (Colors, Fonts, Images) must be disposed if not system resources.
- Follow `Display`/`Shell` lifecycle in `Main.java`.
- `SWTUtil.WIN32` and `SWTUtil.DARK` are static booleans for platform/theme branching.

## Dependencies

- **SWT** — native UI (platform artifact selected at build time)
- **Password4j** — Argon2/scrypt/PBKDF2 key derivation
- **NanoJSON** — JSON parsing
- **zxcvbn4j** — password strength
- **SLF4J + java.util.logging** — logging via `LogFactory`
- **AssertJ** — test assertions (fluent style, not JUnit `assertEquals`)

Check existing libraries before proposing new dependencies.
