package com.bookstore.tests;

import com.bookstore.base.BaseTest;
import com.bookstore.config.ApiConfig;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive test suite for Books API
 * Covers happy path, edge cases, and negative scenarios
 */
@Epic("Books API Testing")
@Feature("Books CRUD Operations")
public class BookApiTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"})
    @Story("Get All Books")
    @Description("Verify that all books can be retrieved successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllBooks() {
        logger.info("Testing GET all books endpoint");
        
        Response response = bookService.getAllBooks();
        logResponseDetails(response);
        
        // Validate HTTP status first
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        // Deserialize to POJO for better assertions
        List<Book> books = deserializeResponseList(response, Book.class);
        
        // Type-safe assertions
        assertTrue(books.size() > 0, "Should return at least one book");
        
        Allure.step("Validate first book data structure and content", () -> {
            Book firstBook = books.get(0);

            assertTrue(firstBook.getId() > 0, "Book ID should be positive");
            assertNotNull(firstBook.getTitle(), "Book title should not be null");
            assertNotNull(firstBook.getDescription(), "Book description should not be null");
            assertTrue(firstBook.getPageCount() >= 0, "Page count should be non-negative");
            assertNotNull(firstBook.getPublishDate(), "Publish date should not be null");
            assertNotNull(firstBook.getExcerpt(), "Excerpt should not be null");
        });
        
        logger.info("GET all books test completed successfully - Found {} books", books.size());
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Get Book by ID")
    @Description("Verify that a specific book can be retrieved by its ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookById() {
        logger.info("Testing GET book by ID endpoint");
        
        Response response = bookService.getBookById(ApiConfig.VALID_BOOK_ID);
        logResponseDetails(response);
        
        // Validate HTTP status first
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        // Deserialize to POJO for better assertions
        Book book = deserializeResponse(response, Book.class);
        
        Allure.step("Validate retrieved book data structure and content", () -> {
            // Type-safe assertions
            assertNotNull(book, "Book should not be null");
            assertEquals(book.getId(), ApiConfig.VALID_BOOK_ID, "Book ID should match requested ID");
            assertNotNull(book.getTitle(), "Book title should not be null");
            assertNotNull(book.getDescription(), "Book description should not be null");
            assertNotNull(book.getPublishDate(), "Publish date should not be null");
        });
        
        logger.info("GET book by ID test completed successfully - Found book: {}", book.getTitle());
    }
    
    @Test(groups = {"regression"})
    @Story("Get Book by Invalid ID")
    @Description("Verify proper error handling when requesting a book with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookByInvalidId() {
        logger.info("Testing GET book by invalid ID endpoint");
        
        Response response = bookService.getBookById(ApiConfig.INVALID_ID);
        logResponseDetails(response);
        
        // For negative scenarios, we only validate HTTP status - no POJO deserialization
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        // Optionally verify error response format (if API returns structured errors)
        // String errorBody = response.getBody().asString();
        // assertFalse(errorBody.isEmpty(), "Error response should not be empty");
        
        logger.info("GET book by invalid ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Get Book by Negative ID")
    @Description("Verify proper error handling when requesting a book with negative ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookByNegativeId() {
        logger.info("Testing GET book by negative ID endpoint");
        
        Response response = bookService.getBookById(ApiConfig.NEGATIVE_ID);
        logResponseDetails(response);
        
        // Note: API behavior may vary, adjust expected status code based on actual API behavior
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("GET book by negative ID test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Create New Book")
    @Description("Verify that a new book can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBook() {
        logger.info("Testing POST create book endpoint");
        
        Book newBook = TestDataFactory.createValidBook();
        Response response = bookService.createBook(newBook);
        logResponseDetails(response);
        
        // Validate HTTP status first
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        // Deserialize to POJO for better assertions
        Book createdBook = deserializeResponse(response, Book.class);
        
        Allure.step("Validate created book matches input data", () -> {
            // Type-safe assertions
            assertNotNull(createdBook, "Created book should not be null");
            assertEquals(createdBook.getTitle(), newBook.getTitle(), "Title should match");
            assertEquals(createdBook.getDescription(), newBook.getDescription(), "Description should match");
            assertEquals(createdBook.getPageCount(), newBook.getPageCount(), "Page count should match");
            assertTrue(createdBook.getId() > 0, "Created book should have a positive ID");
        });
        
        logger.info("POST create book test completed successfully - Created book ID: {}", createdBook.getId());
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Invalid Data")
    @Description("Verify proper error handling when creating a book with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithInvalidData() {
        logger.info("Testing POST create book with invalid data");
        
        Book invalidBook = TestDataFactory.createInvalidBook();
        Response response = bookService.createBook(invalidBook);
        logResponseDetails(response);
        
        // Note: API behavior may vary, adjust expected status code based on actual API behavior
        // Some APIs might accept empty strings, others might reject them
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("POST create book with invalid data test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Long Data")
    @Description("Verify boundary testing with very long field values")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookWithLongData() {
        logger.info("Testing POST create book with long data");
        
        Book longDataBook = TestDataFactory.createBookWithLongData();
        Response response = bookService.createBook(longDataBook);
        logResponseDetails(response);
        
        // This test checks boundary conditions - API might accept or reject long data
        // Adjust assertion based on API specification
        Allure.step("Validate long data book creation", () -> {
            response.then().statusCode(anyOf(
                    equalTo(HttpStatus.SC_OK),
                    equalTo(HttpStatus.SC_BAD_REQUEST)
            ));
        });
        
        logger.info("POST create book with long data test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Update Existing Book")
    @Description("Verify that an existing book can be updated successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateBook() {
        logger.info("Testing PUT update book endpoint");
        
        // First create a book to update
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdBookId = createResponse.jsonPath().getInt("id");
        
        // Update the book
        Book updatedBook = TestDataFactory.createBookWithData("Updated Title", "Updated Description", 250);
        updatedBook.setId(createdBookId);
        
        Response updateResponse = bookService.updateBook(createdBookId, updatedBook);
        logResponseDetails(updateResponse);
        
        Allure.step("Validate book update was successful", () -> {
            verifyStatusCode(updateResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(updateResponse, "id", createdBookId);
            verifyResponseFieldEquals(updateResponse, "title", "Updated Title");
        });
        
        logger.info("PUT update book test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Non-existent Book")
    @Description("Verify proper error handling when updating a non-existent book")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentBook() {
        logger.info("Testing PUT update non-existent book endpoint");
        
        Book updatedBook = TestDataFactory.createValidBook();
        Response response = bookService.updateBook(ApiConfig.INVALID_ID, updatedBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("PUT update non-existent book test completed successfully");
    }
    
    @Test(groups = {"smoke", "regression"})
    @Story("Delete Book")
    @Description("Verify that a book can be deleted successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBook() {
        logger.info("Testing DELETE book endpoint");
        
        // First create a book to delete
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        
        int createdBookId = createResponse.jsonPath().getInt("id");
        
        // Delete the book
        Response deleteResponse = bookService.deleteBook(createdBookId);
        logResponseDetails(deleteResponse);
        
        verifyStatusCode(deleteResponse, HttpStatus.SC_OK);
        
        Allure.step("Verify book was completely removed", () -> {
            // Verify the book is deleted by trying to get it
            Response getResponse = bookService.getBookById(createdBookId);
            verifyStatusCode(getResponse, HttpStatus.SC_NOT_FOUND);
        });
        
        logger.info("DELETE book test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Delete Non-existent Book")
    @Description("Verify proper error handling when deleting a non-existent book")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentBook() {
        logger.info("Testing DELETE non-existent book endpoint");
        
        Response response = bookService.deleteBook(ApiConfig.INVALID_ID);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_NOT_FOUND);
        
        logger.info("DELETE non-existent book test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Performance Test")
    @Description("Verify API response time is within acceptable limits")
    @Severity(SeverityLevel.MINOR)
    public void testGetAllBooksPerformance() {
        logger.info("Testing GET all books performance");
        
        Response response = bookService.getAllBooks();
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
    
    // NEW TEST CASES - Data Validation & Input Testing
    
    @Test(groups = {"regression"})
    @Story("Create Book with Null Values")
    @Description("Verify proper error handling when creating a book with null values")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithNullValues() {
        logger.info("Testing POST create book with null values");
        
        Book nullFieldBook = Book.builder()
                .title(null)
                .description(null)
                .pageCount(100)
                .excerpt(null)
                .publishDate(null)
                .build();
        
        Response response = bookService.createBook(nullFieldBook);
        logResponseDetails(response);
        
        // API should handle null values gracefully - either accept or reject with proper error
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create book with null values test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Special Characters")
    @Description("Verify handling of special characters in book data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithSpecialCharacters() {
        logger.info("Testing POST create book with special characters");
        
        Book specialCharBook = TestDataFactory.createBookWithData(
                "Test & Book <script>alert('test')</script>", 
                "Description with 'quotes' & \"double quotes\" and symbols: @#$%^&*()",
                150
        );
        
        Response response = bookService.createBook(specialCharBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate special characters are properly handled", () -> {
            // Verify special characters are properly handled/escaped
            Book createdBook = deserializeResponse(response, Book.class);
            assertNotNull(createdBook.getTitle(), "Title should not be null after creation");
        });
        
        logger.info("POST create book with special characters test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Unicode Characters")
    @Description("Verify Unicode/UTF-8 support in book data")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookWithUnicodeCharacters() {
        logger.info("Testing POST create book with Unicode characters");
        
        Book unicodeBook = TestDataFactory.createBookWithData(
                "测试书籍 📚 Тестовая книга", 
                "Description with émojis 🎉 and åccénts",
                200
        );
        
        Response response = bookService.createBook(unicodeBook);
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate Unicode character preservation", () -> {
            Book createdBook = deserializeResponse(response, Book.class);
            assertTrue(createdBook.getTitle().contains("测试"), "Unicode characters should be preserved");
        });
        
        logger.info("POST create book with Unicode characters test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Zero Page Count")
    @Description("Verify boundary testing with zero page count")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithZeroPageCount() {
        logger.info("Testing POST create book with zero page count");
        
        Book zeroPageBook = TestDataFactory.createBookWithData("Zero Page Book", "Description", 0);
        Response response = bookService.createBook(zeroPageBook);
        logResponseDetails(response);
        
        // API behavior may vary - some might accept 0 pages, others might reject
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create book with zero page count test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Book Idempotency")
    @Description("Verify that multiple identical update requests produce the same result")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookIdempotency() {
        logger.info("Testing PUT update book idempotency");
        
        // Create a book first
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        
        // Prepare update data
        Book updateData = TestDataFactory.createBookWithData("Idempotent Title", "Idempotent Description", 300);
        updateData.setId(bookId);
        
        // Perform first update
        Response firstUpdate = bookService.updateBook(bookId, updateData);
        verifyStatusCode(firstUpdate, HttpStatus.SC_OK);
        
        // Perform second identical update
        Response secondUpdate = bookService.updateBook(bookId, updateData);
        verifyStatusCode(secondUpdate, HttpStatus.SC_OK);
        
        Allure.step("Validate idempotent update behavior", () -> {
            // Results should be identical
            Book firstResult = deserializeResponse(firstUpdate, Book.class);
            Book secondResult = deserializeResponse(secondUpdate, Book.class);
            
            assertEquals(firstResult.getTitle(), secondResult.getTitle(), "Titles should match after idempotent updates");
            assertEquals(firstResult.getDescription(), secondResult.getDescription(), "Descriptions should match after idempotent updates");
        });
        
        logger.info("PUT update book idempotency test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Duplicate Books")
    @Description("Verify behavior when creating books with identical data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateDuplicateBooks() {
        logger.info("Testing POST create duplicate books");
        
        Book originalBook = TestDataFactory.createBookWithData("Duplicate Test", "Same description", 150);
        
        // Create first book
        Response firstResponse = bookService.createBook(originalBook);
        verifyStatusCode(firstResponse, HttpStatus.SC_OK);
        
        // Create identical book
        Response secondResponse = bookService.createBook(originalBook);
        
        // API might allow duplicates or reject them - both behaviors are valid
        secondResponse.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_CONFLICT),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create duplicate books test completed successfully");
    }
    
    // ADDITIONAL CRITICAL TEST FLOWS
    
    @Test(groups = {"regression"})
    @Story("Update Book with Invalid Data")
    @Description("Verify proper error handling when updating a book with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookWithInvalidData() {
        logger.info("Testing PUT update book with invalid data");
        
        // First create a book to update
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        
        // Attempt to update with invalid data
        Book invalidBook = TestDataFactory.createInvalidBook();
        invalidBook.setId(bookId);
        
        Response updateResponse = bookService.updateBook(bookId, invalidBook);
        logResponseDetails(updateResponse);
        
        // API should reject invalid data
        verifyStatusCode(updateResponse, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("PUT update book with invalid data test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Book with Negative Page Count")
    @Description("Verify boundary testing when updating book with negative page count")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookWithNegativePageCount() {
        logger.info("Testing PUT update book with negative page count");
        
        // Create a book first
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        
        // Update with negative page count
        Book updateBook = TestDataFactory.createBookWithData("Updated Title", "Updated Description", -100);
        updateBook.setId(bookId);
        
        Response updateResponse = bookService.updateBook(bookId, updateBook);
        logResponseDetails(updateResponse);
        
        // API should handle negative values - either accept or reject
        updateResponse.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("PUT update book with negative page count test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Delete Book with Negative ID")
    @Description("Verify proper error handling when deleting a book with negative ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookByNegativeId() {
        logger.info("Testing DELETE book by negative ID endpoint");
        
        Response response = bookService.deleteBook(ApiConfig.NEGATIVE_ID);
        logResponseDetails(response);
        
        // API should handle negative ID appropriately
        verifyStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        
        logger.info("DELETE book by negative ID test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Book ID Mismatch")
    @Description("Verify behavior when URL ID doesn't match request body ID")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookIdMismatch() {
        logger.info("Testing PUT update book with ID mismatch");
        
        // Create a book first
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        
        // Update with different ID in body
        Book updateBook = TestDataFactory.createBookWithData("Mismatch Test", "Description", 200);
        updateBook.setId(bookId + 1000); // Different ID in body
        
        Response updateResponse = bookService.updateBook(bookId, updateBook);
        logResponseDetails(updateResponse);
        
        // API behavior may vary - document actual behavior
        Allure.step("Validate ID mismatch handling", () -> {
            updateResponse.then().statusCode(anyOf(
                    equalTo(HttpStatus.SC_OK),
                    equalTo(HttpStatus.SC_BAD_REQUEST),
                    equalTo(HttpStatus.SC_CONFLICT)
            ));
        });
        
        logger.info("PUT update book with ID mismatch test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Negative Page Count")
    @Description("Verify boundary testing when creating book with negative page count")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithNegativePageCount() {
        logger.info("Testing POST create book with negative page count");
        
        Book negativePageBook = TestDataFactory.createBookWithData("Negative Pages", "Description", -50);
        Response response = bookService.createBook(negativePageBook);
        logResponseDetails(response);
        
        // API behavior may vary - some might accept negative values, others reject
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create book with negative page count test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Book with Null Values")
    @Description("Verify proper error handling when updating a book with null values")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookWithNullValues() {
        logger.info("Testing PUT update book with null values");
        
        // Create a book first
        Book newBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(newBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        
        // Update with null values
        Book nullBook = Book.builder()
                .id(bookId)
                .title(null)
                .description(null)
                .pageCount(100)
                .excerpt(null)
                .publishDate(null)
                .build();
        
        Response updateResponse = bookService.updateBook(bookId, nullBook);
        logResponseDetails(updateResponse);
        
        // API should handle null values gracefully
        updateResponse.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("PUT update book with null values test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Get All Books Empty Response Handling")
    @Description("Verify handling when no books exist in the system")
    @Severity(SeverityLevel.MINOR)
    public void testGetAllBooksWhenEmpty() {
        logger.info("Testing GET all books when potentially empty");
        
        Response response = bookService.getAllBooks();
        logResponseDetails(response);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate empty response structure", () -> {
            // Even if empty, response should be valid JSON array
            response.then().body("$", instanceOf(List.class));
            
            // Log the count for documentation
            List<Book> books = deserializeResponseList(response, Book.class);
            logger.info("Total books found: {}", books.size());
        });
        
        logger.info("GET all books empty response test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Create Book with Maximum Length Data")
    @Description("Verify boundary testing with maximum allowed field lengths")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookWithMaxLengthData() {
        logger.info("Testing POST create book with maximum length data");
        
        // Test with very long but reasonable data (not the extreme long data)
        Book maxLengthBook = TestDataFactory.createBookWithData(
                "Maximum Length Title: " + "x".repeat(200),
                "Maximum Length Description: " + "y".repeat(1000),
                Integer.MAX_VALUE / 1000 // Large but reasonable page count
        );
        
        Response response = bookService.createBook(maxLengthBook);
        logResponseDetails(response);
        
        // API might accept or reject based on field limits
        response.then().statusCode(anyOf(
                equalTo(HttpStatus.SC_OK),
                equalTo(HttpStatus.SC_BAD_REQUEST)
        ));
        
        logger.info("POST create book with maximum length data test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Book Partial Fields")
    @Description("Verify partial update behavior with only some fields provided")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookPartialFields() {
        logger.info("Testing PUT update book with partial fields");
        
        // Create a book first
        Book originalBook = TestDataFactory.createValidBook();
        Response createResponse = bookService.createBook(originalBook);
        verifyStatusCode(createResponse, HttpStatus.SC_OK);
        int bookId = createResponse.jsonPath().getInt("id");
        Book createdBook = deserializeResponse(createResponse, Book.class);
        
        // Update with only some fields
        Book partialUpdate = Book.builder()
                .id(bookId)
                .title("Partially Updated Title")
                .description(createdBook.getDescription()) // Keep original
                .pageCount(999) // New value
                .excerpt(createdBook.getExcerpt()) // Keep original
                .publishDate(createdBook.getPublishDate()) // Keep original
                .build();
        
        Response updateResponse = bookService.updateBook(bookId, partialUpdate);
        verifyStatusCode(updateResponse, HttpStatus.SC_OK);
        
        Allure.step("Validate partial update preserved unchanged fields", () -> {
            Book updatedBook = deserializeResponse(updateResponse, Book.class);
            assertEquals(updatedBook.getTitle(), "Partially Updated Title", "Title should be updated");
            assertEquals(updatedBook.getPageCount(), 999, "Page count should be updated");
            assertEquals(updatedBook.getDescription(), createdBook.getDescription(), "Description should be preserved");
        });
        
        logger.info("PUT update book with partial fields test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Concurrent Book Operations")
    @Description("Verify behavior with concurrent create operations")
    @Severity(SeverityLevel.MINOR)
    public void testConcurrentBookCreation() {
        logger.info("Testing concurrent book creation");
        
        // Create multiple books with same data simultaneously
        Book bookTemplate = TestDataFactory.createBookWithData("Concurrent Book", "Concurrent Description", 200);
        
        // Simulate concurrent requests (simplified version)
        Response response1 = bookService.createBook(bookTemplate);
        Response response2 = bookService.createBook(bookTemplate);
        
        Allure.step("Validate concurrent creation behavior", () -> {
            // Both should succeed (or fail consistently)
            response1.then().statusCode(anyOf(
                    equalTo(HttpStatus.SC_OK),
                    equalTo(HttpStatus.SC_CONFLICT),
                    equalTo(HttpStatus.SC_BAD_REQUEST)
            ));
            
            response2.then().statusCode(anyOf(
                    equalTo(HttpStatus.SC_OK),
                    equalTo(HttpStatus.SC_CONFLICT),
                    equalTo(HttpStatus.SC_BAD_REQUEST)
            ));
            
            // If both succeed, they should have different IDs
            if (response1.getStatusCode() == HttpStatus.SC_OK && response2.getStatusCode() == HttpStatus.SC_OK) {
                int id1 = response1.jsonPath().getInt("id");
                int id2 = response2.jsonPath().getInt("id");
                assertNotEquals(id1, id2, "Concurrent creations should result in different IDs");
            }
        });
        
        logger.info("Concurrent book creation test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Books API Query Parameters")
    @Description("Verify API behavior with query parameters if supported")
    @Severity(SeverityLevel.MINOR)
    public void testGetBooksWithQueryParameters() {
        logger.info("Testing GET books with query parameters");
        
        // Test with potential query parameters (behavior depends on API)
        Response response = bookService.getBooksWithParams("limit", "5");
        logResponseDetails(response);
        
        // API might support query params or ignore them
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate query parameter handling", () -> {
            List<Book> books = deserializeResponseList(response, Book.class);
            logger.info("Books returned with query param limit=5: {}", books.size());
            // Note: Actual filtering depends on API implementation
        });
        
        logger.info("GET books with query parameters test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Book Creation Response Time")
    @Description("Verify book creation performance is within acceptable limits")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookPerformance() {
        logger.info("Testing POST create book performance");
        
        Book newBook = TestDataFactory.createValidBook();
        Response response = bookService.createBook(newBook);
        long responseTime = response.getTime();
        
        logger.info("Create book response time: {} ms", responseTime);
        
        verifyStatusCode(response, HttpStatus.SC_OK);
        
        Allure.step("Validate create performance metrics", () -> {
            assertTrue(responseTime < 5000, 
                    "Create response time should be less than 5000ms but was: " + responseTime + "ms");
        });
        
        logger.info("Create book performance test completed successfully");
    }
} 