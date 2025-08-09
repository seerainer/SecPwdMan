#!/bin/bash

# SecPwdMan Test Runner Script
# This script runs all categories of tests for SecPwdMan

echo "========================================="
echo "SecPwdMan Comprehensive Test Suite"
echo "========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    print_error "Gradle wrapper not found. Please run this script from the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

print_status "Starting comprehensive test suite..."

# Clean build first
print_status "Cleaning previous build..."
./gradlew clean

if [ $? -ne 0 ]; then
    print_error "Clean failed"
    exit 1
fi

# Compile main classes
print_status "Compiling main classes..."
./gradlew compileJava

if [ $? -ne 0 ]; then
    print_error "Main compilation failed"
    exit 1
fi

# Compile test classes
print_status "Compiling test classes..."
./gradlew compileTestJava

if [ $? -ne 0 ]; then
    print_error "Test compilation failed"
    exit 1
fi

print_success "Compilation completed successfully"

# Run unit tests
print_status "Running unit tests..."
./gradlew unitTest

UNIT_RESULT=$?
if [ $UNIT_RESULT -eq 0 ]; then
    print_success "Unit tests passed"
else
    print_warning "Unit tests failed or had issues"
fi

# Run integration tests
print_status "Running integration tests..."
./gradlew integrationTest

INTEGRATION_RESULT=$?
if [ $INTEGRATION_RESULT -eq 0 ]; then
    print_success "Integration tests passed"
else
    print_warning "Integration tests failed or had issues"
fi

# Run code coverage analysis
print_status "Generating code coverage report..."
./gradlew jacocoTestReport

COVERAGE_RESULT=$?
if [ $COVERAGE_RESULT -eq 0 ]; then
    print_success "Code coverage analysis completed"
    print_status "Coverage report available at: build/reports/jacoco/test/html/index.html"
else
    print_warning "Code coverage analysis failed"
fi

# Generate test summary
echo ""
echo "========================================="
echo "Test Execution Summary"
echo "========================================="

if [ $UNIT_RESULT -eq 0 ]; then
    print_success "✓ Unit Tests"
else
    print_error "✗ Unit Tests"
fi

if [ $INTEGRATION_RESULT -eq 0 ]; then
    print_success "✓ Integration Tests"
else
    print_error "✗ Integration Tests"
fi

if [ $COVERAGE_RESULT -eq 0 ]; then
    print_success "✓ Code Coverage Analysis"
else
    print_error "✗ Code Coverage Analysis"
fi

# Overall result
TOTAL_FAILURES=$((
    $([ $UNIT_RESULT -ne 0 ] && echo 1 || echo 0) +
    $([ $INTEGRATION_RESULT -ne 0 ] && echo 1 || echo 0)
))

echo ""
if [ $TOTAL_FAILURES -eq 0 ]; then
    print_success "All core tests passed! ✓"
    echo ""
    print_status "Reports generated:"
    print_status "  - Test results: build/reports/tests/"
    print_status "  - Coverage: build/reports/jacoco/test/html/index.html"
    echo ""
    exit 0
else
    print_error "$TOTAL_FAILURES test suite(s) failed"
    echo ""
    print_status "Check the following for detailed error information:"
    print_status "  - Test results: build/reports/tests/"
    print_status "  - Build logs: build/logs/"
    echo ""
    exit 1
fi
