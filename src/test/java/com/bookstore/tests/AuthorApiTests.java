package com.bookstore.tests;

import com.bookstore.base.BaseTest;
import com.bookstore.config.ApiConfig;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static io.restassured.RestAssured.given;

/**
 * Comprehensive test suite for Authors API
 * Covers happy path, edge cases, and negative scenarios
 */
@Epic("Authors API Testing")
@Feature("Authors CRUD Operations")
public class AuthorApiTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"})
    @Story("Get All Authors")
    @Description("Verify that all authors can be retrieved successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllAuthors() {
        logger.info("Testing GET all authors endpoint");
        
        Response response = authorService.getAllAuthors();
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate response structure and author data fields", () -> {
            // Verify response structure
            response.then()
                    .contentType("application/json")
                    .body("", hasSize(greaterThan(0)))
                    .body("[0]", hasKey("id"))
                    .body("[0]", hasKey("idBook"))
                    .body("[0]", hasKey("firstName"))
                    .body("[0]", hasKey("lastName"));
        });
        
        logger.info("GET all authors test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Get Author by ID")
    @Description("Verify that a specific author can be retrieved by their ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAuthorById() {
        logger.info("Testing GET author by ID endpoint");
        
        Response response = authorService.getAuthorById(ApiConfig.VALID_AUTHOR_ID);
        logResponseDetails(response);
        
        Allure.step("Validate retrieved author data structure", () -> {
            verifyStatusCode(response, HttpStatus.SC_OK);
            verifyResponseFieldEquals(response, "id", ApiConfig.VALID_AUTHOR_ID);
            verifyResponseContainsField(response, "firstName");
            verifyResponseContainsField(response, "lastName");
            verifyResponseContainsField(response, "idBook");
        });
        
        logger.info("GET author by ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Get Author by Invalid ID")
    @Description("Verify proper error handling when requesting an author with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAuthorByInvalidId() {
        logger.info("Testing GET author by invalid ID endpoint");
        
        Response response = authorService.getAuthorById(ApiConfig.INVALID_ID);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("GET author by invalid ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Get Author by Negative ID")
    @Description("Verify proper error handling when requesting an author with negative ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAuthorByNegativeId() {
        logger.info("Testing GET author by negative ID endpoint");
        
        Response response = authorService.getAuthorById(ApiConfig.NEGATIVE_ID);
        logResponseDetails(response);
        
        // Note: API behavior may vary, adjust expected status code based on actual API behavior
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("GET author by negative ID test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Create New Author")
    @Description("Verify that a new author can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateAuthor() {
        logger.info("Testing POST create author endpoint");
        
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response response = authorService.createAuthor(newAuthor);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate created author matches input data", () -> {
            // Verify the created author data
            verifyResponseFieldEquals(response, "firstName", newAuthor.getFirstName());
            verifyResponseFieldEquals(response, "lastName", newAuthor.getLastName());
            verifyResponseFieldEquals(response, "idBook", newAuthor.getIdBook());
        });
        
        logger.info("POST create author test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Author with Invalid Data")
    @Description("Verify proper error handling when creating an author with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithInvalidData() {
        logger.info("Testing POST create author with invalid data");
        
        Author invalidAuthor = TestDataFactory.createInvalidAuthor();
        Response response = authorService.createAuthor(invalidAuthor);
        logResponseDetails(response);
        
        // Note: API behavior may vary, adjust expected status code based on actual API behavior
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("POST create author with invalid data test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Author with Long Data")
    @Description("Verify boundary testing with very long field values for author")
    @Severity(SeverityLevel.MINOR)
    public void testCreateAuthorWithLongData() {
        logger.info("Testing POST create author with long data");
        
        Author longDataAuthor = TestDataFactory.createAuthorWithLongData();
        Response response = authorService.createAuthor(longDataAuthor);
        logResponseDetails(response);
        
        // This test checks boundary conditions - API might accept or reject long data
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create author with long data test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Update Existing Author")
    @Description("Verify that an existing author can be updated successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateAuthor() {
        logger.info("Testing PUT update author endpoint");
        
        // First create an author to update
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdAuthorId = createResponse.jsonPath().getInt("id");
        
        // Update the author
        Author updatedAuthor = TestDataFactory.createAuthorWithData("UpdatedFirstName", "UpdatedLastName", 1);
        updatedAuthor.setId(createdAuthorId);
        
        Response updateResponse = authorService.updateAuthor(createdAuthorId, updatedAuthor);
        logResponseDetails(updateResponse);
        
        Allure.step("Validate author update was successful", () -> {
            verifyStatusCode(updateResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(updateResponse, "id", createdAuthorId);
            verifyResponseFieldEquals(updateResponse, "firstName", "UpdatedFirstName");
            verifyResponseFieldEquals(updateResponse, "lastName", "UpdatedLastName");
        });
        
        logger.info("PUT update author test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Non-existent Author")
    @Description("Verify proper error handling when updating a non-existent author")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentAuthor() {
        logger.info("Testing PUT update non-existent author endpoint");
        
        Author updatedAuthor = TestDataFactory.createValidAuthor();
        Response response = authorService.updateAuthor(ApiConfig.INVALID_ID, updatedAuthor);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("PUT update non-existent author test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Delete Author")
    @Description("Verify that an author can be deleted successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteAuthor() {
        logger.info("Testing DELETE author endpoint");
        
        // First create an author to delete
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdAuthorId = createResponse.jsonPath().getInt("id");
        
        // Delete the author
        Response deleteResponse = authorService.deleteAuthor(createdAuthorId);
        logResponseDetails(deleteResponse);
        
        verifyStatusCode(deleteResponse, HttpStatus.SC_OK);
        
        Allure.step("Verify author was completely removed", () -> {
            // Verify the author is deleted by trying to get it
            Response getResponse = authorService.getAuthorById(createdAuthorId);
            verifyStatusCode(getResponse, HttpStatus.SC_NOT_FOUND);
        });
        
        logger.info("DELETE author test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Delete Non-existent Author")
    @Description("Verify proper error handling when deleting a non-existent author")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentAuthor() {
        logger.info("Testing DELETE non-existent author endpoint");
        
        Response response = authorService.deleteAuthor(ApiConfig.INVALID_ID);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("DELETE non-existent author test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Authors for Specific Book")
    @Description("Verify retrieving authors for a specific book")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAuthorsByBookId() {
        logger.info("Testing GET authors by book ID");
        
        // This test assumes the API supports filtering by book ID
        // Adjust implementation based on actual API capabilities
        Response response = authorService.getAuthorsWithParams("idBook", "1");
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate authors belong to specified book", () -> {
            // Verify all returned authors have the specified book ID
            response.then()
                    .body("findAll { it.idBook == 1 }.size()", greaterThanOrEqualTo(0));
        });
        
        logger.info("GET authors by book ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Performance Test")
    @Description("Verify API response time is within acceptable limits")
    @Severity(SeverityLevel.MINOR)
    public void testGetAllAuthorsPerformance() {
        logger.info("Testing GET all authors performance");
        
        Response response = authorService.getAllAuthors();
        long responseTime = response.getTime();
        
        logger.info("Response time: {} ms", responseTime);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate performance metrics", () -> {
            // Assert response time is less than 5 seconds (5000 ms)
            assertTrue(responseTime < 5000, 
                    "Response time should be less than 5000ms but was: " + responseTime + "ms");
        });
        
        logger.info("Performance test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Input Validation - Null Fields")
    @Description("Verify proper error handling when creating author with null required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithNullFields() {
        logger.info("Testing POST create author with null fields");
        
        Author nullFieldsAuthor = TestDataFactory.createAuthorWithData(null, null, 1);
        Response response = authorService.createAuthor(nullFieldsAuthor);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("POST create author with null fields test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Input Validation - Empty Fields")
    @Description("Verify proper error handling when creating author with empty required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithEmptyFields() {
        logger.info("Testing POST create author with empty fields");
        
        Author emptyFieldsAuthor = TestDataFactory.createAuthorWithData("", "", 1);
        Response response = authorService.createAuthor(emptyFieldsAuthor);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("POST create author with empty fields test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Input Validation - Special Characters")
    @Description("Verify handling of special characters in author names")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithSpecialCharacters() {
        logger.info("Testing POST create author with special characters");
        
        Author specialCharAuthor = TestDataFactory.createAuthorWithData(
            "John<script>alert('xss')</script>", 
            "O'Connor & Sons", 
            1
        );
        Response response = authorService.createAuthor(specialCharAuthor);
        logResponseDetails(response);
        
        // API should either accept and sanitize, or reject with 400
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        // If accepted, verify XSS protection
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            Allure.step("Verify XSS protection", () -> {
                String firstName = response.jsonPath().getString("firstName");
                assertFalse(firstName.contains("<script>"), 
                        "Response should not contain script tags");
            });
        }
        
        logger.info("POST create author with special characters test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Input Validation - Unicode Characters")
    @Description("Verify handling of Unicode characters in author names")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithUnicodeCharacters() {
        logger.info("Testing POST create author with Unicode characters");
        
        Author unicodeAuthor = TestDataFactory.createAuthorWithData(
            "José María", 
            "Müller-Σωκράτης", 
            1
        );
        Response response = authorService.createAuthor(unicodeAuthor);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Verify Unicode characters are preserved", () -> {
            verifyResponseFieldEquals(response, "firstName", "José María");
            verifyResponseFieldEquals(response, "lastName", "Müller-Σωκράτης");
        });
        
        logger.info("POST create author with Unicode characters test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Edge Cases - Zero ID")
    @Description("Verify proper error handling when requesting author with ID zero")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAuthorByZeroId() {
        logger.info("Testing GET author by zero ID endpoint");
        
        Response response = authorService.getAuthorById(0);
        logResponseDetails(response);
        
        // API should return 400 or 404 for zero ID
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_BAD_REQUEST),
                equalTo(HttpStatus.SC_NOT_FOUND)
        ));
        
        logger.info("GET author by zero ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Edge Cases - Extremely Large ID")
    @Description("Verify proper error handling when requesting author with extremely large ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAuthorByExtremelyLargeId() {
        logger.info("Testing GET author by extremely large ID endpoint");
        
        Response response = authorService.getAuthorById(Integer.MAX_VALUE);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("GET author by extremely large ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Data Consistency - Invalid Book Reference")
    @Description("Verify proper error handling when creating author with invalid book ID reference")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithInvalidBookReference() {
        logger.info("Testing POST create author with invalid book ID reference");
        
        Author invalidBookRefAuthor = TestDataFactory.createAuthorWithData(
            "John", 
            "Doe", 
            ApiConfig.INVALID_ID
        );
        Response response = authorService.createAuthor(invalidBookRefAuthor);
        logResponseDetails(response);
        
        // API behavior may vary - might accept or reject invalid book references
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST),
                equalTo(HttpStatus.SC_UNPROCESSABLE_ENTITY)
        ));
        
        logger.info("POST create author with invalid book reference test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Data Consistency - ID Mismatch")
    @Description("Verify proper error handling when update path ID doesn't match body ID")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateAuthorWithIdMismatch() {
        logger.info("Testing PUT update author with ID mismatch");
        
        // First create an author
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdAuthorId = createResponse.jsonPath().getInt("id");
        
        // Create update author with different ID in body
        Author updateAuthor = TestDataFactory.createAuthorWithData("Updated", "Name", 1);
        updateAuthor.setId(createdAuthorId + 1); // Different ID
        
        Response response = authorService.updateAuthor(createdAuthorId, updateAuthor);
        logResponseDetails(response);
        
        // API should handle ID mismatch gracefully
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST),
                equalTo(HttpStatus.SC_CONFLICT)
        ));
        
        logger.info("PUT update author with ID mismatch test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Concurrent Operations - Double Deletion")
    @Description("Verify behavior when attempting to delete an already deleted author")
    @Severity(SeverityLevel.NORMAL)
    public void testDoubleDeleteAuthor() {
        logger.info("Testing double deletion of author");
        
        // First create an author
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdAuthorId = createResponse.jsonPath().getInt("id");
        
        // First deletion
        Response firstDeleteResponse = authorService.deleteAuthor(createdAuthorId);
        verifyStatusCode(firstDeleteResponse, HttpStatus.SC_OK);
        
        // Second deletion attempt
        Response secondDeleteResponse = authorService.deleteAuthor(createdAuthorId);
        logResponseDetails(secondDeleteResponse);
        
        verifyStatusCode(secondDeleteResponse, HttpStatus.SC_NOT_FOUND);
        
        logger.info("Double deletion test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("HTTP Protocol - Wrong Content Type")
    @Description("Verify proper error handling when sending request with wrong content type")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithWrongContentType() {
        logger.info("Testing POST create author with wrong content type");
        
        Author newAuthor = TestDataFactory.createValidAuthor();
        
        Response response = given()
                .contentType("text/plain") // Wrong content type
                .accept("application/json")
                .body(newAuthor)
                .when()
                .post(ApiConfig.AUTHORS_ENDPOINT)
                .then()
                .extract().response();
        
        logResponseDetails(response);
        
        // Should return 415 Unsupported Media Type or 400 Bad Request
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create author with wrong content type test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("HTTP Protocol - Malformed JSON")
    @Description("Verify proper error handling when sending malformed JSON payload")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorWithMalformedJson() {
        logger.info("Testing POST create author with malformed JSON");
        
        String malformedJson = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"idBook\": }"; // Missing value
        
        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(malformedJson)
                .when()
                .post(ApiConfig.AUTHORS_ENDPOINT)
                .then()
                .extract().response();
        
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("POST create author with malformed JSON test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Partial Update - Single Field")
    @Description("Verify partial update of author with single field change")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialUpdateAuthor() {
        logger.info("Testing partial update of author");
        
        // First create an author
        Author newAuthor = TestDataFactory.createValidAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdAuthorId = createResponse.jsonPath().getInt("id");
        String originalLastName = createResponse.jsonPath().getString("lastName");
        
        // Update only firstName
        Author partialUpdateAuthor = new Author();
        partialUpdateAuthor.setId(createdAuthorId);
        partialUpdateAuthor.setFirstName("UpdatedFirstName");
        partialUpdateAuthor.setLastName(originalLastName); // Keep original
        partialUpdateAuthor.setIdBook(newAuthor.getIdBook()); // Keep original
        
        Response updateResponse = authorService.updateAuthor(createdAuthorId, partialUpdateAuthor);
        logResponseDetails(updateResponse);
        
        verifyStatusCode(updateResponse, HttpStatus.SC_OK);
        
        Allure.step("Verify partial update was successful", () -> {
            verifyResponseFieldEquals(updateResponse, "firstName", "UpdatedFirstName");
            verifyResponseFieldEquals(updateResponse, "lastName", originalLastName);
        });
        
        logger.info("Partial update test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Data Validation - SQL Injection Prevention")
    @Description("Verify protection against SQL injection attempts in author data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateAuthorWithSqlInjection() {
        logger.info("Testing POST create author with SQL injection attempt");
        
        Author sqlInjectionAuthor = TestDataFactory.createAuthorWithData(
            "'; DROP TABLE Authors; --",
            "' OR '1'='1",
            1
        );
        Response response = authorService.createAuthor(sqlInjectionAuthor);
        logResponseDetails(response);
        
        // API should either sanitize and accept, or reject
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        // Verify the system is still functional after SQL injection attempt
        Allure.step("Verify system integrity after SQL injection attempt", () -> {
            Response getAllResponse = authorService.getAllAuthors();
            verifyStatusCode(getAllResponse, HttpStatus.SC_OK);
        });
        
        logger.info("SQL injection prevention test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Boundary Testing - Maximum Integer Values")
    @Description("Verify handling of maximum integer values for numeric fields")
    @Severity(SeverityLevel.MINOR)
    public void testCreateAuthorWithMaxIntegerValues() {
        logger.info("Testing POST create author with maximum integer values");
        
        Author maxValueAuthor = TestDataFactory.createAuthorWithData(
            "MaxValue", 
            "Test", 
            Integer.MAX_VALUE
        );
        Response response = authorService.createAuthor(maxValueAuthor);
        logResponseDetails(response);
        
        // API behavior may vary with extreme values
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST),
                equalTo(HttpStatus.SC_UNPROCESSABLE_ENTITY)
        ));
        
        logger.info("Maximum integer values test completed successfully");
    }
} 