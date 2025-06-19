@echo off
REM API Automation Test Runner Script for Windows
REM This script provides easy commands to run different test suites

setlocal enabledelayedexpansion

REM Default values
set TEST_TYPE=all
set CLEAN=false
set GENERATE_REPORT=false
set THREADS=3

REM Parse command line arguments
:parse_args
if "%~1"=="" goto :execute_tests
if "%~1"=="-s" (
    set TEST_TYPE=smoke
    shift
    goto :parse_args
)
if "%~1"=="--smoke" (
    set TEST_TYPE=smoke
    shift
    goto :parse_args
)
if "%~1"=="-r" (
    set TEST_TYPE=regression
    shift
    goto :parse_args
)
if "%~1"=="--regression" (
    set TEST_TYPE=regression
    shift
    goto :parse_args
)
if "%~1"=="-a" (
    set TEST_TYPE=all
    shift
    goto :parse_args
)
if "%~1"=="--all" (
    set TEST_TYPE=all
    shift
    goto :parse_args
)
if "%~1"=="-b" (
    set TEST_TYPE=books
    shift
    goto :parse_args
)
if "%~1"=="--books" (
    set TEST_TYPE=books
    shift
    goto :parse_args
)
if "%~1"=="-u" (
    set TEST_TYPE=authors
    shift
    goto :parse_args
)
if "%~1"=="--authors" (
    set TEST_TYPE=authors
    shift
    goto :parse_args
)
if "%~1"=="-i" (
    set TEST_TYPE=integration
    shift
    goto :parse_args
)
if "%~1"=="--integration" (
    set TEST_TYPE=integration
    shift
    goto :parse_args
)
if "%~1"=="-S" (
    set TEST_TYPE=security
    shift
    goto :parse_args
)
if "%~1"=="--security" (
    set TEST_TYPE=security
    shift
    goto :parse_args
)
if "%~1"=="-c" (
    set CLEAN=true
    shift
    goto :parse_args
)
if "%~1"=="--clean" (
    set CLEAN=true
    shift
    goto :parse_args
)
if "%~1"=="-R" (
    set GENERATE_REPORT=true
    shift
    goto :parse_args
)
if "%~1"=="--report" (
    set GENERATE_REPORT=true
    shift
    goto :parse_args
)
if "%~1"=="-t" (
    set THREADS=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--threads" (
    set THREADS=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="-h" goto :show_usage
if "%~1"=="--help" goto :show_usage

echo Unknown option: %~1
goto :show_usage

:show_usage
echo Usage: %~nx0 [OPTIONS]
echo Options:
echo   -s, --smoke       Run smoke tests only
echo   -r, --regression  Run regression tests only
echo   -a, --all         Run all tests (default)
echo   -b, --books       Run Books API tests only
echo   -u, --authors     Run Authors API tests only
echo   -i, --integration Run Integration tests only
echo   -S, --security    Run Security tests only
echo   -c, --clean       Clean before running tests
echo   -R, --report      Generate and serve Allure report after tests
echo   -t, --threads N   Set number of parallel threads (default: 3)
echo   -h, --help        Show this help message
echo.
echo Examples:
echo   %~nx0 --smoke                    # Run smoke tests
echo   %~nx0 --regression --clean       # Clean and run regression tests
echo   %~nx0 --books --report           # Run books tests and show report
goto :end

:execute_tests
echo === API Automation Test Runner ===
echo Test Type: %TEST_TYPE%
echo Threads: %THREADS%
echo Clean: %CLEAN%
echo Generate Report: %GENERATE_REPORT%
echo ==================================

REM Clean if requested
if "%CLEAN%"=="true" (
    echo Cleaning project...
    call mvn clean
)

REM Run tests based on type
if "%TEST_TYPE%"=="smoke" (
    echo Running smoke tests...
    call mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml -DthreadCount=%THREADS%
) else if "%TEST_TYPE%"=="regression" (
    echo Running regression tests...
    call mvn test -DsuiteXmlFile=src/test/resources/testng-regression.xml -DthreadCount=%THREADS%
) else if "%TEST_TYPE%"=="books" (
    echo Running Books API tests...
    call mvn test -Dtest=BookApiTests -DthreadCount=%THREADS%
) else if "%TEST_TYPE%"=="authors" (
    echo Running Authors API tests...
    call mvn test -Dtest=AuthorApiTests -DthreadCount=%THREADS%
) else if "%TEST_TYPE%"=="integration" (
    echo Running Integration tests...
    call mvn test -Dtest=IntegrationTests -DthreadCount=%THREADS%
) else if "%TEST_TYPE%"=="security" (
    echo Running Security tests...
    call mvn test -DsuiteXmlFile=src/test/resources/testng-security.xml -DthreadCount=%THREADS%
) else (
    echo Running all tests...
    call mvn test -DthreadCount=%THREADS%
)

REM Check if tests passed
if %ERRORLEVEL% equ 0 (
    echo ✅ Tests completed successfully!
) else (
    echo ❌ Some tests failed. Check the reports for details.
)

REM Generate and serve Allure report if requested
if "%GENERATE_REPORT%"=="true" (
    echo Generating Allure report...
    call mvn allure:report
    
    if %ERRORLEVEL% equ 0 (
        echo 📊 Report generated successfully!
        echo Starting Allure server...
        call mvn allure:serve
    ) else (
        echo Failed to generate Allure report
    )
)

echo === Test execution completed ===

:end
endlocal 