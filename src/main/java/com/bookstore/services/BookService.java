package com.bookstore.services;

import com.bookstore.config.ApiConfig;
import com.bookstore.models.Book;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

/**
 * Book Service class for handling Book API operations
 * Following the Service Layer pattern for separation of concerns
 * Responsible only for API communication - assertions handled in tests
 */
@Slf4j
public class BookService extends ServiceBase {
    
    /**
     * Get request specification with common headers
     */
    private RequestSpecification getRequestSpec() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
    
    /**
     * Get all books
     */
    @Step("Get all books from API")
    public Response getAllBooks() {
        log.debug("Fetching all books from API");
        
        return getRequestSpec()
                .when()
                .get(ApiConfig.BOOKS_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Get book by ID
     */
    @Step("Get book by ID: {bookId}")
    public Response getBookById(int bookId) {
        log.debug("Fetching book with ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .get(ApiConfig.BOOK_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Create a new book
     */
    @Step("Create new book: '{book.title}'")
    public Response createBook(Book book) {
        log.debug("Creating new book: {}", book.getTitle());
        
        return getRequestSpec()
                .body(book)
                .when()
                .post(ApiConfig.BOOKS_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Update an existing book
     */
    @Step("Update book with ID: {bookId}")
    public Response updateBook(int bookId, Book book) {
        log.debug("Updating book ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .body(book)
                .when()
                .put(ApiConfig.BOOK_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Delete a book by ID
     */
    @Step("Delete book with ID: {bookId}")
    public Response deleteBook(int bookId) {
        log.debug("Deleting book with ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .delete(ApiConfig.BOOK_BY_ID_ENDPOINT)
                .then()
                .extract().response();
    }
    
    /**
     * Get books with query parameters (for advanced testing)
     */
    @Step("Get books with query parameter: {queryParam}={value}")
    public Response getBooksWithParams(String queryParam, String value) {
        log.debug("Fetching books with parameter: {}={}", queryParam, value);
        
        return getRequestSpec()
                .queryParam(queryParam, value)
                .when()
                .get(ApiConfig.BOOKS_ENDPOINT)
                .then()
                .extract().response();
    }
} 