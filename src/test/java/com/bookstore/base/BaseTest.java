package com.bookstore.base;

import com.bookstore.services.AuthorService;
import com.bookstore.services.BookService;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Base Test class containing common setup and utility methods
 * Following the Template Method pattern for test structure
 */
public class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    
    protected BookService bookService;
    protected AuthorService authorService;
    
    @BeforeClass
    @Step("Initialize test services")
    public void setupClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        bookService = new BookService();
        authorService = new AuthorService();
    }
    
    @BeforeMethod
    @Step("Setup test method")
    public void setupMethod() {
        logger.info("Starting test method");
    }
    
    /**
     * Common assertion helper methods
     */
    
    @Step("Verify response status code is {expectedStatusCode}")
    protected void verifyStatusCode(Response response, int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        
        assertEquals(actualStatusCode, expectedStatusCode, 
                "Expected status code " + expectedStatusCode + " but got " + actualStatusCode);
    }
    
    @Step("Verify response contains field {fieldName}")
    protected void verifyResponseContainsField(Response response, String fieldName) {
        boolean hasField = response.jsonPath().get(fieldName) != null;
        assertTrue(hasField, "Response should contain field: " + fieldName);
    }
    
    @Step("Verify response field {fieldName} equals {expectedValue}")
    protected void verifyResponseFieldEquals(Response response, String fieldName, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldName);
        assertEquals(actualValue, expectedValue, 
                "Field '" + fieldName + "' - expected: " + expectedValue + ", actual: " + actualValue);
    }
    
    @Step("Log response details")
    protected void logResponseDetails(Response response) {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Time: {} ms", response.getTime());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", response.getBody().asString());
    }
    
    /**
     * Helper method to safely deserialize response to POJO
     * Use this for successful responses that should contain valid data
     */
    @Step("Deserialize response to {clazz}")
    protected <T> T deserializeResponse(Response response, Class<T> clazz) {
        try {
            T result = response.as(clazz);
            logger.debug("Successfully deserialized response to {}", clazz.getSimpleName());
            return result;
        } catch (Exception e) {
            logger.error("Failed to deserialize response to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new AssertionError("Response deserialization failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Helper method to safely deserialize response to List of POJOs
     * Use this for successful responses that should contain a list of data
     */
    @Step("Deserialize response to List<{clazz}>")
    protected <T> List<T> deserializeResponseList(Response response, Class<T> clazz) {
        try {
            List<T> result = response.jsonPath().getList("", clazz);
            logger.debug("Successfully deserialized response to List<{}>", clazz.getSimpleName());
            return result;
        } catch (Exception e) {
            logger.error("Failed to deserialize response to List<{}>: {}", clazz.getSimpleName(), e.getMessage());
            throw new AssertionError("Response list deserialization failed: " + e.getMessage(), e);
        }
    }
} 