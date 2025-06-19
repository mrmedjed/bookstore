# API Automation Testing - Online Bookstore

[![API Tests](https://github.com/mrmedjed/bookstore/actions/workflows/api-tests.yml/badge.svg)](https://github.com/mrmedjed/bookstore/actions/workflows/api-tests.yml)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.4.0-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.9.0-orange.svg)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.25.0-yellow.svg)](https://docs.qameta.io/allure/)

## 📖 Overview

Comprehensive API automation testing framework for **FakeRestAPI** bookstore, built with Java 17, REST Assured, and TestNG. Features modern testing practices, detailed Allure reporting, and CI/CD integration.

### 🎯 Key Features

- **Complete CRUD Testing**: Books and Authors API endpoints
- **Multiple Test Types**: Smoke, regression, integration, and security tests
- **Service Layer Architecture**: Clean separation of concerns with SOLID principles
- **Allure Reporting**: Step-by-step execution details with screenshots
- **CI/CD Pipeline**: GitHub Actions with automated execution
- **Cross-platform Scripts**: Windows and Unix execution support
- **Parallel Execution**: Configurable thread pools for performance

## 🏗️ Project Structure

```
api-automation-task/
├── src/main/java/com/bookstore/
│   ├── config/ApiConfig.java       # API configuration
│   ├── models/                     # Book and Author POJOs
│   ├── services/                   # API service layer
│   └── utils/TestDataFactory.java  # Test data generation
├── src/test/java/com/bookstore/
│   ├── base/BaseTest.java          # Common test utilities
│   └── tests/                      # Test suites
├── src/test/resources/
│   ├── testng*.xml                 # TestNG configurations
│   └── logback-test.xml            # Logging configuration
├── scripts/                        # Cross-platform test runners
├── docs/                           # Documentation
└── .github/workflows/              # CI/CD pipeline
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Maven 3.6+**

### Installation & Execution

```bash
# Clone and setup
git clone https://github.com/mrmedjed/bookstore.git
cd api-automation-task
mvn clean install

# Run tests
mvn clean test                          # All tests
mvn test -Dgroups=smoke                # Smoke tests only
mvn test -Dtest=BookApiTests           # Specific test class

# Cross-platform scripts
scripts/run-tests.sh --smoke           # Linux/Mac
scripts\run-tests.bat --smoke          # Windows

# Generate reports
mvn allure:serve                       # Generate and view Allure report
```

### Test Suites

| Suite          | Description                 | Command                                                            |
| -------------- | --------------------------- | ------------------------------------------------------------------ |
| **Smoke**      | Critical functionality      | `mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml`      |
| **Regression** | Comprehensive coverage      | `mvn test -DsuiteXmlFile=src/test/resources/testng-regression.xml` |
| **Security**   | Error handling & validation | `mvn test -DsuiteXmlFile=src/test/resources/testng-security.xml`   |

## 🧪 Test Coverage

### Books API (`/api/v1/Books`)

- **GET**: Retrieve all books, by ID, performance testing
- **POST**: Create with valid/invalid data, boundary testing, special characters
- **PUT**: Update existing, non-existent books, partial updates
- **DELETE**: Delete existing, error handling

### Authors API (`/api/v1/Authors`)

- **GET**: Retrieve all authors, by ID, performance testing
- **POST**: Create with valid/invalid data, long field values
- **PUT**: Update existing, non-existent authors
- **DELETE**: Delete existing, error handling

**Total**: 70+ test cases covering positive, negative, and edge scenarios

## 📊 Reports & Logging

- **Allure Reports**:  
  Generated HTML reports with detailed test execution steps, available live on GitHub Pages:  
  🔗 [https://mrmedjed.github.io/bookstore/reports/](https://mrmedjed.github.io/bookstore/reports/index.html)  
- **TestNG Reports**: Standard XML/HTML reports in `target/surefire-reports/`
- **Logs**: Structured logging to `target/logs/api-tests.log`

## 🔧 Configuration

### API Configuration

```java
// src/main/java/com/bookstore/config/ApiConfig.java
public static final String BASE_URL = "https://fakerestapi.azurewebsites.net";
public static final String API_VERSION = "/api/v1";
public static final int DEFAULT_TIMEOUT = 5000;
```

### TestNG Suites

- `testng.xml`: Main suite (all tests)
- `testng-smoke.xml`: Critical functionality only
- `testng-regression.xml`: Full regression suite
- `testng-security.xml`: Security-focused tests

## 🔄 CI/CD Pipeline

GitHub Actions workflow with:

- **Triggers**: Push, PR, scheduled (daily 2 AM UTC), manual dispatch
- **Execution**: Java 17 matrix with parallel test execution
- **Artifacts**: Allure reports, test logs, Surefire reports (auto-generated)
- **Deployment**: GitHub Pages for report hosting

## 🛠️ Development

### Adding Tests

```java
@Test(groups = {"smoke", "regression"})
@Story("Feature Story")
@Description("Test description")
@Severity(SeverityLevel.CRITICAL)
public void testNewFeature_ShouldReturnExpectedResult() {
    // Use BaseTest utilities and service layer
}
```

### Architecture Patterns

- **Service Layer**: API operations abstraction
- **Factory Pattern**: Test data generation with Lombok builders
- **Base Test**: Common utilities and setup
- **POJO Models**: Type-safe JSON serialization

## 🤝 Contributing

1. Fork repository
2. Create feature branch
3. Follow existing patterns
4. Ensure tests pass (`mvn clean test`)
5. Submit pull request

## 📚 Documentation
- **[FakeRestAPI Docs](https://fakerestapi.azurewebsites.net/index.html)**: API specification

## 🐛 Troubleshooting

```bash
# API connectivity issues
curl https://fakerestapi.azurewebsites.net/api/v1/Books

# Maven dependency issues
mvn dependency:purge-local-repository && mvn clean install

# Java version issues
java -version  # Should be 17+
export JAVA_HOME=/path/to/java17
```

## 📄 License

**Built with ❤️ using Java 17, REST Assured, TestNG, and Allure**
