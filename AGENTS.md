# SecPwdMan Developer Guide for Agents

This document outlines the build processes, code style guidelines, and conventions for the SecPwdMan project. Agents operating in this codebase must adhere strictly to these rules.

## 1. Build & Test Commands

The project uses **Gradle** as the build system. Use the wrapper (`./gradlew` or `gradlew.bat`) for all operations.

### Common Commands
- **Build Project:** `gradlew build`
- **Compile Only:** `gradlew classes`
- **Run Application:** `gradlew run`
- **Run All Tests:** `gradlew test` (Runs both unit and integration tests)
- **Clean:** `gradlew clean`

### Testing
Tests are split into `unitTest` and `integrationTest` tasks, but standard `test` runs both.
- **Run Unit Tests Only:** `gradlew unitTest`
- **Run Integration Tests Only:** `gradlew integrationTest`
- **Run a Single Test Class:**
  ```bash
  gradlew test --tests "io.github.seerainer.secpwdman.path.to.TestClass"
  ```
- **Run a Specific Test Method:**
  ```bash
  gradlew test --tests "io.github.seerainer.secpwdman.path.to.TestClass.methodName"
  ```

### Environment
- **Java Version:** 25 (Preview features may be enabled/required).
- **UI Framework:** SWT (Standard Widget Toolkit).
- **Native Image:** The project supports GraalVM native image generation (`gradlew nativeCompile`).

## 2. Code Style & Conventions

### Formatting & Structure
- **Indentation:** Use **4 spaces** for indentation.
- **Line Endings:** Preserve existing line endings.
- **Copyright Header:** All new Java files **MUST** include the GPLv3 copyright header found in existing files (e.g., `Main.java`).
- **Imports:**
  - Avoid wildcard imports (`import java.util.*;`).
  - Order: Standard Java, Third-party, Project imports.

### Java Modernization & Typing
- **Type Inference:** Use `final var` for local variables where the type is obvious.
  ```java
  final var mainUI = new MainWindow(display, args); // Correct
  MainWindow mainUI = new MainWindow(display, args); // Avoid
  ```
- **Final:** Prefer immutability. Mark local variables and parameters as `final` where possible.
- **Java 25:** You may use modern Java features available up to version 25.

### Project Specific Patterns
- **Constants:** The project uses the "Constant Interface" pattern (e.g., `implements PrimitiveConstants, StringConstants`).
- **Logging:**
  - **Do not** use `System.out.println`.
  - Use the project's wrapper: `io.github.seerainer.secpwdman.util.LogFactory`.
  - Usage: `LogFactory.getLog().info(...)` or `LogFactory.getLog().error(...)`.
- **UI (SWT):**
  - UI logic resides in `io.github.seerainer.secpwdman.ui`.
  - Ensure SWT resources (Colors, Fonts, Images) are disposed of properly if they are not system resources.
  - Follow the `Display` and `Shell` management patterns seen in `Main.java`.

### Error Handling
- Exceptions should be caught and logged using `LogFactory`.
- Do not swallow exceptions without logging.
- Use `try-with-resources` for I/O operations to ensure proper closing of streams.

### Naming Conventions
- **Classes:** PascalCase (`MainWindow`, `SecureMemory`).
- **Methods/Variables:** camelCase (`getDisplay`, `startTime`).
- **Constants:** UPPER_SNAKE_CASE (`APP_NAME`, `START_TIME`).
- **Package:** `io.github.seerainer.secpwdman` (lowercase).

## 3. Directory Structure
- `src/main/java`: Application source code.
- `src/main/resources`: Non-Java resources (properties, icons, metadata).
- `src/test/java`: JUnit 5 tests.
- `src/test/resources`: Test data and configuration.

## 4. Third-Party Libraries
- **SWT:** For native UI components.
- **Password4j:** For password hashing/encryption primitives.
- **NanoJSON:** For JSON parsing.
- **ZXCVBN:** For password strength estimation.
- **SLF4J:** For logging facade.

When adding new functionality, check if an existing library already provides it before suggesting new dependencies.
