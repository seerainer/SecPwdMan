@echo off
setlocal enabledelayedexpansion

REM SecPwdMan Test Runner Script for Windows
REM This script runs all categories of tests for SecPwdMan

echo =========================================
echo SecPwdMan Comprehensive Test Suite
echo =========================================

REM Function to print status messages
call :print_status "Starting comprehensive test suite..."

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    call :print_error "Gradle wrapper not found. Please run this script from the project root directory."
    exit /b 1
)

REM Clean build first
call :print_status "Cleaning previous build..."
call gradlew.bat clean

if !errorlevel! neq 0 (
    call :print_error "Clean failed"
    exit /b 1
)

REM Compile main classes
call :print_status "Compiling main classes..."
call gradlew.bat compileJava

if !errorlevel! neq 0 (
    call :print_error "Main compilation failed"
    exit /b 1
)

REM Compile test classes
call :print_status "Compiling test classes..."
call gradlew.bat compileTestJava

if !errorlevel! neq 0 (
    call :print_error "Test compilation failed"
    exit /b 1
)

call :print_success "Compilation completed successfully"

REM Run unit tests
call :print_status "Running unit tests..."
call gradlew.bat unitTest
set UNIT_RESULT=!errorlevel!

if !UNIT_RESULT! equ 0 (
    call :print_success "Unit tests passed"
) else (
    call :print_warning "Unit tests failed or had issues"
)

REM Run integration tests
call :print_status "Running integration tests..."
call gradlew.bat integrationTest
set INTEGRATION_RESULT=!errorlevel!

if !INTEGRATION_RESULT! equ 0 (
    call :print_success "Integration tests passed"
) else (
    call :print_warning "Integration tests failed or had issues"
)

REM Run code coverage analysis
call :print_status "Generating code coverage report..."
call gradlew.bat jacocoTestReport
set COVERAGE_RESULT=!errorlevel!

if !COVERAGE_RESULT! equ 0 (
    call :print_success "Code coverage analysis completed"
    call :print_status "Coverage report available at: build\reports\jacoco\test\html\index.html"
) else (
    call :print_warning "Code coverage analysis failed"
)

REM Generate test summary
echo.
echo =========================================
echo Test Execution Summary
echo =========================================

if !UNIT_RESULT! equ 0 (
    call :print_success "✓ Unit Tests"
) else (
    call :print_error "✗ Unit Tests"
)

if !INTEGRATION_RESULT! equ 0 (
    call :print_success "✓ Integration Tests"
) else (
    call :print_error "✗ Integration Tests"
)

if !COVERAGE_RESULT! equ 0 (
    call :print_success "✓ Code Coverage Analysis"
) else (
    call :print_error "✗ Code Coverage Analysis"
)

REM Calculate total failures
set /a TOTAL_FAILURES=0
if !UNIT_RESULT! neq 0 set /a TOTAL_FAILURES+=1
if !INTEGRATION_RESULT! neq 0 set /a TOTAL_FAILURES+=1

echo.
if !TOTAL_FAILURES! equ 0 (
    call :print_success "All core tests passed! ✓"
    echo.
    call :print_status "Reports generated:"
    call :print_status "  - Test results: build\reports\tests\"
    call :print_status "  - Coverage: build\reports\jacoco\test\html\index.html"
    echo.
    exit /b 0
) else (
    call :print_error "!TOTAL_FAILURES! test suite(s) failed"
    echo.
    call :print_status "Check the following for detailed error information:"
    call :print_status "  - Test results: build\reports\tests\"
    call :print_status "  - Build logs: build\logs\"
    echo.
    exit /b 1
)

REM Helper functions
:print_status
echo [INFO] %~1
goto :eof

:print_success
echo [SUCCESS] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof
