#!/bin/bash

# API Automation Test Runner Script
# This script provides easy commands to run different test suites

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Options:"
    echo "  -s, --smoke       Run smoke tests only"
    echo "  -r, --regression  Run regression tests only"
    echo "  -a, --all         Run all tests (default)"
    echo "  -b, --books       Run Books API tests only"
    echo "  -u, --authors     Run Authors API tests only"
    echo "  -i, --integration Run Integration tests only"
    echo "  -S, --security    Run Security tests only"
    echo "  -c, --clean       Clean before running tests"
    echo "  -R, --report      Generate and serve Allure report after tests"
    echo "  -t, --threads N   Set number of parallel threads (default: 3)"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 --smoke                    # Run smoke tests"
    echo "  $0 --regression --clean       # Clean and run regression tests"
    echo "  $0 --books --report           # Run books tests and show report"
    echo "  $0 --all --threads 5          # Run all tests with 5 threads"
}

# Default values
TEST_TYPE="all"
CLEAN=false
GENERATE_REPORT=false
THREADS=3
MAVEN_OPTS=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -s|--smoke)
            TEST_TYPE="smoke"
            shift
            ;;
        -r|--regression)
            TEST_TYPE="regression"
            shift
            ;;
        -a|--all)
            TEST_TYPE="all"
            shift
            ;;
        -b|--books)
            TEST_TYPE="books"
            shift
            ;;
        -u|--authors)
            TEST_TYPE="authors"
            shift
            ;;
        -i|--integration)
            TEST_TYPE="integration"
            shift
            ;;
        -S|--security)
            TEST_TYPE="security"
            shift
            ;;
        -c|--clean)
            CLEAN=true
            shift
            ;;
        -R|--report)
            GENERATE_REPORT=true
            shift
            ;;
        -t|--threads)
            THREADS="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_message $RED "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate threads parameter
if ! [[ "$THREADS" =~ ^[0-9]+$ ]] || [ "$THREADS" -lt 1 ]; then
    print_message $RED "Error: Threads must be a positive integer"
    exit 1
fi

print_message $BLUE "=== API Automation Test Runner ==="
print_message $BLUE "Test Type: $TEST_TYPE"
print_message $BLUE "Threads: $THREADS"
print_message $BLUE "Clean: $CLEAN"
print_message $BLUE "Generate Report: $GENERATE_REPORT"
print_message $BLUE "=================================="

# Set Maven options
MAVEN_OPTS="-DthreadCount=$THREADS"

# Clean if requested
if [ "$CLEAN" = true ]; then
    print_message $YELLOW "Cleaning project..."
    mvn clean
fi

# Prepare test command based on test type
case $TEST_TYPE in
    "smoke")
        print_message $YELLOW "Running smoke tests..."
        mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml $MAVEN_OPTS
        ;;
    "regression")
        print_message $YELLOW "Running regression tests..."
        mvn test -DsuiteXmlFile=src/test/resources/testng-regression.xml $MAVEN_OPTS
        ;;
    "books")
        print_message $YELLOW "Running Books API tests..."
        mvn test -Dtest=BookApiTests $MAVEN_OPTS
        ;;
    "authors")
        print_message $YELLOW "Running Authors API tests..."
        mvn test -Dtest=AuthorApiTests $MAVEN_OPTS
        ;;
    "integration")
        print_message $YELLOW "Running Integration tests..."
        mvn test -Dtest=IntegrationTests $MAVEN_OPTS
        ;;
    "security")
        print_message $YELLOW "Running Security tests..."
        mvn test -DsuiteXmlFile=src/test/resources/testng-security.xml $MAVEN_OPTS
        ;;
    "all"|*)
        print_message $YELLOW "Running all tests..."
        mvn test $MAVEN_OPTS
        ;;
esac

# Check if tests passed
if [ $? -eq 0 ]; then
    print_message $GREEN "✅ Tests completed successfully!"
else
    print_message $RED "❌ Some tests failed. Check the reports for details."
fi

# Generate and serve Allure report if requested
if [ "$GENERATE_REPORT" = true ]; then
    print_message $YELLOW "Generating Allure report..."
    mvn allure:report
    
    if [ $? -eq 0 ]; then
        print_message $GREEN "📊 Report generated successfully!"
        print_message $BLUE "Starting Allure server..."
        mvn allure:serve
    else
        print_message $RED "Failed to generate Allure report"
    fi
fi

print_message $BLUE "=== Test execution completed ===" 