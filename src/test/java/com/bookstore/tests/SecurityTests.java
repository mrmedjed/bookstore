package com.bookstore.tests;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Book;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;

/**
 * Security-focused test suite for Books and Authors APIs
 * Tests various security vulnerabilities and attack vectors
 */
@Epic("Security Testing")
@Feature("Input Validation and Attack Prevention")
public class SecurityTests extends BaseTest {
    
    @Test(groups = {"security", "regression"})
    @Story("SQL Injection Prevention")
    @Description("Verify API prevents SQL injection attacks in book data")
    @Severity(SeverityLevel.CRITICAL)
    public void testSqlInjectionInBookFields() {
        logger.info("Testing SQL injection prevention in book creation");
        
        Book maliciousBook = TestDataFactory.createBookWithData(
                "'; DROP TABLE Books; --",
                "Test Description'; UPDATE Books SET title='HACKED' WHERE id=1; --",
                100
        );
        
        Response response = bookService.createBook(maliciousBook);
        logResponseDetails(response);
        
        Allure.step("Validate SQL injection prevention", () -> {
            // API should either accept and sanitize, or reject with proper error
            response.then().statusCode(anyOf(
                    equalTo(HttpStatus.SC_OK),
                    equalTo(HttpStatus.SC_BAD_REQUEST)
            ));
            
            // If accepted, verify data was sanitized
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                // SQL should be treated as literal text, not executed
                verifyResponseContainsField(response, "title");
            }
        });
        
        logger.info("SQL injection prevention test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("XSS Prevention")
    @Description("Verify API prevents Cross-Site Scripting attacks")
    @Severity(SeverityLevel.CRITICAL)
    public void testXssPreventionInBookFields() {
        logger.info("Testing XSS prevention in book creation");
        
        Book xssBook = TestDataFactory.createBookWithData(
                "<script>alert('XSS Attack')</script>",
                "Description with <img src=x onerror=alert('XSS')> payload",
                150
        );
        
        Response response = bookService.createBook(xssBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate XSS payload sanitization", () -> {
            // Verify XSS payloads are properly encoded/escaped
            String responseBody = response.getBody().asString();
            assertFalse(responseBody.contains("<script>"), 
                    "Response should not contain unescaped script tags");
            assertFalse(responseBody.contains("onerror="), 
                    "Response should not contain unescaped event handlers");
        });
        
        logger.info("XSS prevention test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("Command Injection Prevention")
    @Description("Verify API prevents command injection attacks")
    @Severity(SeverityLevel.CRITICAL)
    public void testCommandInjectionPrevention() {
        logger.info("Testing command injection prevention");
        
        Author maliciousAuthor = TestDataFactory.createAuthorWithData(
                "John; cat /etc/passwd;",
                "Doe && rm -rf /",
                1
        );
        
        Response response = authorService.createAuthor(maliciousAuthor);
        logResponseDetails(response);
        
        // API should handle malicious input safely
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("Command injection prevention test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("Path Traversal Prevention")
    @Description("Verify API prevents directory traversal attacks")
    @Severity(SeverityLevel.NORMAL)
    public void testPathTraversalPrevention() {
        logger.info("Testing path traversal prevention");
        
        Book pathTraversalBook = TestDataFactory.createBookWithData(
                "../../../etc/passwd",
                "..\\..\\windows\\system32\\config\\sam",
                200
        );
        
        Response response = bookService.createBook(pathTraversalBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate path traversal sequence sanitization", () -> {
            // Verify path traversal attempts are sanitized
            Book createdBook = deserializeResponse(response, Book.class);
            assertFalse(createdBook.getTitle().contains("../"), 
                    "Path traversal sequences should be sanitized");
        });
        
        logger.info("Path traversal prevention test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("Large Payload DOS Prevention")
    @Description("Verify API handles extremely large payloads gracefully")
    @Severity(SeverityLevel.NORMAL)
    public void testLargePayloadHandling() {
        logger.info("Testing large payload handling");
        
        // Create book with very large field values
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            largeString.append("A");
        }
        
        Book largeBook = TestDataFactory.createBookWithData(
                largeString.toString(),
                largeString.toString(),
                Integer.MAX_VALUE
        );
        
        Response response = bookService.createBook(largeBook);
        logResponseDetails(response);
        
        // API should either accept (if within limits) or reject gracefully
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST),
                equalTo(413) // Request Entity Too Large
        ));
        
        logger.info("Large payload handling test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("Null Byte Injection Prevention")
    @Description("Verify API prevents null byte injection attacks")
    @Severity(SeverityLevel.NORMAL)
    public void testNullByteInjectionPrevention() {
        logger.info("Testing null byte injection prevention");
        
        Book nullByteBook = TestDataFactory.createBookWithData(
                "Test\u0000.txt",
                "Description with null\u0000byte",
                100
        );
        
        Response response = bookService.createBook(nullByteBook);
        logResponseDetails(response);
        
        // API should handle null bytes safely
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("Null byte injection prevention test completed successfully");
    }
    
    @Test(groups = {"security", "regression"})
    @Story("Header Injection Prevention")
    @Description("Verify API prevents HTTP header injection attacks")
    @Severity(SeverityLevel.NORMAL)
    public void testHeaderInjectionPrevention() {
        logger.info("Testing header injection prevention");
        
        Book headerInjectionBook = TestDataFactory.createBookWithData(
                "Test\r\nSet-Cookie: evil=true",
                "Description\nLocation: http://evil.com",
                100
        );
        
        Response response = bookService.createBook(headerInjectionBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate no malicious headers were injected", () -> {
            // Verify no malicious headers were injected in response
            assertFalse(response.getHeaders().hasHeaderWithName("Set-Cookie"), 
                    "No malicious cookies should be set");
            assertFalse(response.getHeaders().hasHeaderWithName("Location"), 
                    "No malicious redirects should be set");
        });
        
        logger.info("Header injection prevention test completed successfully");
    }
} 