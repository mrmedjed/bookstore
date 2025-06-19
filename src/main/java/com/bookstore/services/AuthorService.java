package com.bookstore.services;

import com.bookstore.config.ApiConfig;
import com.bookstore.models.Author;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

/**
 * Author Service class for handling Author API operations
 * Following the Service Layer pattern for separation of concerns
 * Responsible only for API communication - assertions handled in tests
 */
@Slf4j
public class AuthorService extends ServiceBase {
    
    /**
     * Get request specification with common headers
     */
    private RequestSpecification getRequestSpec() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
    
    /**
     * Get all authors
     */
    @Step("Get all authors from API")
    public Response getAllAuthors() {
        log.debug("Fetching all authors from API");
        
        return getRequestSpec()
                .when()
                .get(ApiConfig.AUTHORS_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Get author by ID
     */
    @Step("Get author by ID: {authorId}")
    public Response getAuthorById(int authorId) {
        log.debug("Fetching author with ID: {}", authorId);
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .get(ApiConfig.AUTHOR_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Create a new author
     */
    @Step("Create new author: '{author.firstName} {author.lastName}'")
    public Response createAuthor(Author author) {
        log.debug("Creating new author: {} {}", author.getFirstName(), author.getLastName());
        
        return getRequestSpec()
                .body(author)
                .when()
                .post(ApiConfig.AUTHORS_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Update an existing author
     */
    @Step("Update author with ID: {authorId}")
    public Response updateAuthor(int authorId, Author author) {
        log.debug("Updating author ID: {}", authorId);
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .body(author)
                .when()
                .put(ApiConfig.AUTHOR_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Delete an author by ID
     */
    @Step("Delete author with ID: {authorId}")
    public Response deleteAuthor(int authorId) {
        log.debug("Deleting author with ID: {}", authorId);
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .delete(ApiConfig.AUTHOR_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Get authors with query parameters (for advanced testing)
     */
    @Step("Get authors with query parameter: {queryParam}={value}")
    public Response getAuthorsWithParams(String queryParam, String value) {
        log.debug("Fetching authors with parameter: {}={}", queryParam, value);
        
        return getRequestSpec()
                .queryParam(queryParam, value)
                .when()
                .get(ApiConfig.AUTHORS_ENDPOINT)
                .then()
                .extract().response();
    }
} 