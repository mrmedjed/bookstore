package com.bookstore.tests;

import com.bookstore.base.BaseTest;
import com.bookstore.config.ApiConfig;
import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Integration test suite testing the interaction between Books and Authors APIs
 * This demonstrates end-to-end workflows and cross-entity relationships
 */
@Epic("Integration Testing")
@Feature("Books and Authors Integration")
public class IntegrationTests extends BaseTest {
    
    @Test(groups = {"regression"})
    @Story("Book-Author Relationship")
    @Description("Create a book and then create an author associated with that book")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookAndAssociatedAuthor() {
        logger.info("Testing book-author relationship creation");
        
        // Step 1: Create a new book
        Book newBook = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(newBook);
        logResponseDetails(bookResponse);
        
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int createdBookId = bookResponse.jsonPath().getInt("id");
        
        // Step 2: Create an author associated with the book
        Author newAuthor = TestDataFactory.createAuthorWithData("John", "Doe", createdBookId);
        Response authorResponse = authorService.createAuthor(newAuthor);
        logResponseDetails(authorResponse);
        
        Allure.step("Validate book-author relationship was established", () -> {
            verifyStatusCode(authorResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(authorResponse, "idBook", createdBookId);
            verifyResponseFieldEquals(authorResponse, "firstName", "John");
            verifyResponseFieldEquals(authorResponse, "lastName", "Doe");
        });
        
        logger.info("Book-author relationship creation test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Data Consistency")
    @Description("Verify data consistency when updating related entities")
    @Severity(SeverityLevel.NORMAL)
    public void testDataConsistencyAcrossEntities() {
        logger.info("Testing data consistency across Books and Authors");
        
        // Create a book
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        // Create multiple authors for the same book
        Author author1 = TestDataFactory.createAuthorWithData("Author1", "LastName1", bookId);
        Author author2 = TestDataFactory.createAuthorWithData("Author2", "LastName2", bookId);
        
        Response author1Response = authorService.createAuthor(author1);
        Response author2Response = authorService.createAuthor(author2);
        
        Allure.step("Validate data consistency across multiple authors", () -> {
            verifyStatusCode(author1Response, HttpStatus.SC_OK);
            verifyStatusCode(author2Response, HttpStatus.SC_OK);
            
            // Verify both authors reference the same book
            verifyResponseFieldEquals(author1Response, "idBook", bookId);
            verifyResponseFieldEquals(author2Response, "idBook", bookId);
        });
        
        logger.info("Data consistency test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Cascade Operations")
    @Description("Test behavior when deleting a book that has associated authors")
    @Severity(SeverityLevel.NORMAL)
    public void testCascadeDeleteBehavior() {
        logger.info("Testing cascade delete behavior");
        
        // Create a book
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        // Create an author for the book
        Author author = TestDataFactory.createAuthorWithData("TestAuthor", "TestLastName", bookId);
        Response authorResponse = authorService.createAuthor(author);
        verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        int authorId = authorResponse.jsonPath().getInt("id");
        
        // Delete the book
        Response deleteBookResponse = bookService.deleteBook(bookId);
        verifyStatusCode(deleteBookResponse, HttpStatus.SC_OK);
        
        Allure.step("Validate cascade delete behavior", () -> {
            // Verify book is deleted
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_NOT_FOUND);
            
            // Check if author still exists (behavior depends on API implementation)
            Response getAuthorResponse = authorService.getAuthorById(authorId);
            // Note: This test documents the actual API behavior
            logger.info("Author existence after book deletion - Status: {}", getAuthorResponse.getStatusCode());
        });
        
        logger.info("Cascade delete behavior test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Bulk Operations")
    @Description("Test performance and consistency during bulk operations")
    @Severity(SeverityLevel.MINOR)
    public void testBulkOperationsPerformance() {
        logger.info("Testing bulk operations performance");
        
        long startTime = System.currentTimeMillis();
        
        // Create multiple books and authors
        for (int i = 0; i < 5; i++) {
            // Create book
            Book book = TestDataFactory.createBookWithData(
                    "Bulk Book " + i, 
                    "Description for bulk book " + i, 
                    100 + i
            );
            Response bookResponse = bookService.createBook(book);
            verifyStatusCode(bookResponse, HttpStatus.SC_OK);
            
            int bookId = bookResponse.jsonPath().getInt("id");
            
            // Create author for the book
            Author author = TestDataFactory.createAuthorWithData(
                    "Author" + i, 
                    "LastName" + i, 
                    bookId
            );
            Response authorResponse = authorService.createAuthor(author);
            verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        logger.info("Bulk operations completed in {} ms", totalTime);
        
        Allure.step("Validate bulk operations performance", () -> {
            // Verify the operations completed within reasonable time
            assertTrue(totalTime < 30000, 
                    "Bulk operations should complete within 30 seconds but took: " + totalTime + "ms");
        });
        
        logger.info("Bulk operations performance test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Error Handling Integration")
    @Description("Test error handling scenarios across multiple APIs")
    @Severity(SeverityLevel.NORMAL)
    public void testCrossApiErrorHandling() {
        logger.info("Testing cross-API error handling");
        
        // Try to create an author with a non-existent book ID
        Author authorWithInvalidBook = TestDataFactory.createAuthorWithData(
                "TestAuthor", 
                "TestLastName", 
                ApiConfig.INVALID_ID
        );
        
        Response response = authorService.createAuthor(authorWithInvalidBook);
        logResponseDetails(response);
        
        // The API might accept this (since it's just a reference) or reject it
        // This test documents the actual behavior
        logger.info("Author creation with invalid book ID - Status: {}", response.getStatusCode());
        
        Allure.step("Validate cross-API error handling consistency", () -> {
            // Test updating non-existent entities
            Book nonExistentBook = TestDataFactory.createValidBook();
            Response updateBookResponse = bookService.updateBook(ApiConfig.INVALID_ID, nonExistentBook);
            verifyStatusCode(updateBookResponse, HttpStatus.SC_NOT_FOUND);
            
            Author nonExistentAuthor = TestDataFactory.createValidAuthor();
            Response updateAuthorResponse = authorService.updateAuthor(ApiConfig.INVALID_ID, nonExistentAuthor);
            verifyStatusCode(updateAuthorResponse, HttpStatus.SC_NOT_FOUND);
        });
        
        logger.info("Cross-API error handling test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Reverse Creation Flow")
    @Description("Create an author first, then create a book and verify the relationship can be established")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateAuthorThenBook() {
        logger.info("Testing reverse creation flow - author first, then book");
        
        // Step 1: Create an author with a temporary book ID
        Author newAuthor = TestDataFactory.createAuthorWithData("AuthorFirst", "LastNameFirst", 1);
        Response authorResponse = authorService.createAuthor(newAuthor);
        logResponseDetails(authorResponse);
        
        verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        int createdAuthorId = authorResponse.jsonPath().getInt("id");
        
        // Step 2: Create a book
        Book newBook = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(newBook);
        logResponseDetails(bookResponse);
        
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int createdBookId = bookResponse.jsonPath().getInt("id");
        
        // Step 3: Update the author to reference the new book
        Author updatedAuthor = TestDataFactory.createAuthorWithData("AuthorFirst", "LastNameFirst", createdBookId);
        Response updateResponse = authorService.updateAuthor(createdAuthorId, updatedAuthor);
        
        Allure.step("Validate reverse relationship establishment", () -> {
            verifyStatusCode(updateResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(updateResponse, "idBook", createdBookId);
            verifyResponseFieldEquals(updateResponse, "firstName", "AuthorFirst");
        });
        
        logger.info("Reverse creation flow test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Update Operations Impact")
    @Description("Verify updating book information doesn't break author relationships")
    @Severity(SeverityLevel.NORMAL)
    public void testBookUpdateImpactOnAuthors() {
        logger.info("Testing book update impact on associated authors");
        
        // Create a book and author
        Book originalBook = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(originalBook);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        Author author = TestDataFactory.createAuthorWithData("UpdateTest", "Author", bookId);
        Response authorResponse = authorService.createAuthor(author);
        verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        int authorId = authorResponse.jsonPath().getInt("id");
        
        // Update the book
        Book updatedBook = TestDataFactory.createBookWithData("Updated Title", "Updated Description", 999);
        Response updateBookResponse = bookService.updateBook(bookId, updatedBook);
        verifyStatusCode(updateBookResponse, HttpStatus.SC_OK);
        
        Allure.step("Verify author relationship remains intact after book update", () -> {
            // Verify the author still references the correct book
            Response getAuthorResponse = authorService.getAuthorById(authorId);
            verifyStatusCode(getAuthorResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(getAuthorResponse, "idBook", bookId);
            
            // Verify book was actually updated
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(getBookResponse, "title", "Updated Title");
        });
        
        logger.info("Book update impact test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Author Update Book Reference")
    @Description("Test updating author's book reference and verify data consistency")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthorBookReferenceUpdate() {
        logger.info("Testing author book reference update");
        
        // Create two books
        Book book1 = TestDataFactory.createBookWithData("Book 1", "First Book", 100);
        Book book2 = TestDataFactory.createBookWithData("Book 2", "Second Book", 200);
        
        Response book1Response = bookService.createBook(book1);
        Response book2Response = bookService.createBook(book2);
        
        verifyStatusCode(book1Response, HttpStatus.SC_OK);
        verifyStatusCode(book2Response, HttpStatus.SC_OK);
        
        int book1Id = book1Response.jsonPath().getInt("id");
        int book2Id = book2Response.jsonPath().getInt("id");
        
        // Create author associated with book1
        Author author = TestDataFactory.createAuthorWithData("Reference", "UpdateTest", book1Id);
        Response authorResponse = authorService.createAuthor(author);
        verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        int authorId = authorResponse.jsonPath().getInt("id");
        
        // Update author to reference book2
        Author updatedAuthor = TestDataFactory.createAuthorWithData("Reference", "UpdateTest", book2Id);
        Response updateResponse = authorService.updateAuthor(authorId, updatedAuthor);
        
        Allure.step("Verify author book reference update", () -> {
            verifyStatusCode(updateResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(updateResponse, "idBook", book2Id);
            
            // Verify both books still exist
            verifyStatusCode(bookService.getBookById(book1Id), HttpStatus.SC_OK);
            verifyStatusCode(bookService.getBookById(book2Id), HttpStatus.SC_OK);
        });
        
        logger.info("Author book reference update test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Retrieval Operations with Relationships")
    @Description("Test retrieving entities and verifying their relationships")
    @Severity(SeverityLevel.NORMAL)
    public void testRetrievalWithRelationshipValidation() {
        logger.info("Testing retrieval operations with relationship validation");
        
        // Create book and multiple authors
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        // Create multiple authors for the same book
        Author author1 = TestDataFactory.createAuthorWithData("First", "Author", bookId);
        Author author2 = TestDataFactory.createAuthorWithData("Second", "Author", bookId);
        Author author3 = TestDataFactory.createAuthorWithData("Third", "Author", bookId);
        
        Response auth1Response = authorService.createAuthor(author1);
        Response auth2Response = authorService.createAuthor(author2);
        Response auth3Response = authorService.createAuthor(author3);
        
        verifyStatusCode(auth1Response, HttpStatus.SC_OK);
        verifyStatusCode(auth2Response, HttpStatus.SC_OK);
        verifyStatusCode(auth3Response, HttpStatus.SC_OK);
        
        Allure.step("Validate relationship consistency through retrieval operations", () -> {
            // Get the book and verify it exists
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(getBookResponse, "id", bookId);
            
            // Get all authors and verify they all reference the correct book
            int auth1Id = auth1Response.jsonPath().getInt("id");
            int auth2Id = auth2Response.jsonPath().getInt("id");
            int auth3Id = auth3Response.jsonPath().getInt("id");
            
            verifyResponseFieldEquals(authorService.getAuthorById(auth1Id), "idBook", bookId);
            verifyResponseFieldEquals(authorService.getAuthorById(auth2Id), "idBook", bookId);
            verifyResponseFieldEquals(authorService.getAuthorById(auth3Id), "idBook", bookId);
        });
        
        logger.info("Retrieval with relationship validation test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Author Deletion Impact")
    @Description("Test deleting an author and verify book remains unaffected")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthorDeletionImpactOnBook() {
        logger.info("Testing author deletion impact on associated book");
        
        // Create book and author
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        Author author = TestDataFactory.createAuthorWithData("ToDelete", "Author", bookId);
        Response authorResponse = authorService.createAuthor(author);
        verifyStatusCode(authorResponse, HttpStatus.SC_OK);
        int authorId = authorResponse.jsonPath().getInt("id");
        
        // Delete the author
        Response deleteResponse = authorService.deleteAuthor(authorId);
        verifyStatusCode(deleteResponse, HttpStatus.SC_OK);
        
        Allure.step("Verify book remains unaffected after author deletion", () -> {
            // Verify author is deleted
            Response getAuthorResponse = authorService.getAuthorById(authorId);
            verifyStatusCode(getAuthorResponse, HttpStatus.SC_NOT_FOUND);
            
            // Verify book still exists and is unaffected
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_OK);
            verifyResponseFieldEquals(getBookResponse, "id", bookId);
        });
        
        logger.info("Author deletion impact test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Invalid Relationship Handling")
    @Description("Test comprehensive invalid relationship scenarios")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidRelationshipScenarios() {
        logger.info("Testing invalid relationship scenarios");
        
        // Test 1: Create author with non-existent book ID
        Author authorWithInvalidBook = TestDataFactory.createAuthorWithData("Invalid", "Book", ApiConfig.INVALID_ID);
        Response invalidBookResponse = authorService.createAuthor(authorWithInvalidBook);
        logResponseDetails(invalidBookResponse);
        
        // Test 2: Update author to reference deleted book
        Book tempBook = TestDataFactory.createValidBook();
        Response tempBookResponse = bookService.createBook(tempBook);
        verifyStatusCode(tempBookResponse, HttpStatus.SC_OK);
        int tempBookId = tempBookResponse.jsonPath().getInt("id");
        
        Author validAuthor = TestDataFactory.createAuthorWithData("Valid", "Author", tempBookId);
        Response validAuthorResponse = authorService.createAuthor(validAuthor);
        verifyStatusCode(validAuthorResponse, HttpStatus.SC_OK);
        int authorId = validAuthorResponse.jsonPath().getInt("id");
        
        // Delete the book
        Response deleteBookResponse = bookService.deleteBook(tempBookId);
        verifyStatusCode(deleteBookResponse, HttpStatus.SC_OK);
        
        // Try to update author to reference the deleted book
        Author updatedAuthor = TestDataFactory.createAuthorWithData("Updated", "Author", tempBookId);
        Response updateAuthorResponse = authorService.updateAuthor(authorId, updatedAuthor);
        
        Allure.step("Document invalid relationship handling behavior", () -> {
            logger.info("Author creation with invalid book ID - Status: {}", invalidBookResponse.getStatusCode());
            logger.info("Author update with deleted book reference - Status: {}", updateAuthorResponse.getStatusCode());
            
            // These tests document actual API behavior for invalid relationships
            // The assertions would depend on the specific API implementation
        });
        
        logger.info("Invalid relationship scenarios test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Concurrent Operations")
    @Description("Test concurrent operations on related entities")
    @Severity(SeverityLevel.MINOR)
    public void testConcurrentOperationsOnRelatedEntities() {
        logger.info("Testing concurrent operations on related entities");
        
        // Create a base book
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        long startTime = System.currentTimeMillis();
        
        // Simulate concurrent operations
        Allure.step("Execute concurrent author creation operations", () -> {
            // Create multiple authors concurrently (simulated)
            for (int i = 0; i < 3; i++) {
                Author author = TestDataFactory.createAuthorWithData("Concurrent" + i, "Author" + i, bookId);
                Response authorResponse = authorService.createAuthor(author);
                verifyStatusCode(authorResponse, HttpStatus.SC_OK);
                verifyResponseFieldEquals(authorResponse, "idBook", bookId);
            }
        });
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        Allure.step("Validate concurrent operations performance and data integrity", () -> {
            assertTrue(totalTime < 15000, "Concurrent operations should complete within 15 seconds");
            
            // Verify the book still exists after concurrent operations
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_OK);
        });
        
        logger.info("Concurrent operations test completed in {} ms", totalTime);
    }
    
    @Test(groups = {"regression"})
    @Story("Data Integrity After Partial Failures")
    @Description("Test data integrity when part of a multi-step operation fails")
    @Severity(SeverityLevel.NORMAL)
    public void testDataIntegrityAfterPartialFailure() {
        logger.info("Testing data integrity after partial operation failure");
        
        // Create a book successfully
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        // Create first author successfully
        Author author1 = TestDataFactory.createAuthorWithData("Success", "Author", bookId);
        Response author1Response = authorService.createAuthor(author1);
        verifyStatusCode(author1Response, HttpStatus.SC_OK);
        int author1Id = author1Response.jsonPath().getInt("id");
        
        // Attempt to create second author (might fail depending on API constraints)
        Author author2WithPotentialIssue = TestDataFactory.createInvalidAuthor();
        author2WithPotentialIssue.setIdBook(bookId); // Set valid book ID but keep other invalid data
        Response author2Response = authorService.createAuthor(author2WithPotentialIssue);
        
        Allure.step("Verify data integrity after potential partial failure", () -> {
            // Verify first author still exists regardless of second author creation result
            Response getAuthor1Response = authorService.getAuthorById(author1Id);
            verifyStatusCode(getAuthor1Response, HttpStatus.SC_OK);
            verifyResponseFieldEquals(getAuthor1Response, "idBook", bookId);
            
            // Verify book still exists regardless of author creation results
            Response getBookResponse = bookService.getBookById(bookId);
            verifyStatusCode(getBookResponse, HttpStatus.SC_OK);
            
            logger.info("Second author creation status: {}", author2Response.getStatusCode());
        });
        
        logger.info("Data integrity after partial failure test completed successfully");
    }
    
    @Test(groups = {"regression"})
    @Story("Relationship Boundary Conditions")
    @Description("Test boundary conditions for entity relationships")
    @Severity(SeverityLevel.MINOR)
    public void testRelationshipBoundaryConditions() {
        logger.info("Testing relationship boundary conditions");
        
        // Create a book for testing
        Book book = TestDataFactory.createValidBook();
        Response bookResponse = bookService.createBook(book);
        verifyStatusCode(bookResponse, HttpStatus.SC_OK);
        int bookId = bookResponse.jsonPath().getInt("id");
        
        Allure.step("Test maximum practical number of authors per book", () -> {
            // Test creating multiple authors for the same book (testing practical limits)
            int maxAuthorsToTest = 10;
            int successfulCreations = 0;
            
            for (int i = 0; i < maxAuthorsToTest; i++) {
                Author author = TestDataFactory.createAuthorWithData("BoundaryAuthor" + i, "Test" + i, bookId);
                Response authorResponse = authorService.createAuthor(author);
                
                if (authorResponse.getStatusCode() == HttpStatus.SC_OK) {
                    successfulCreations++;
                    verifyResponseFieldEquals(authorResponse, "idBook", bookId);
                }
            }
            
            logger.info("Successfully created {} authors for the same book", successfulCreations);
            assertTrue(successfulCreations > 0, "Should be able to create at least one author per book");
        });
        
        Allure.step("Test boundary values for book and author IDs", () -> {
            // Test with boundary ID values
            Author authorWithZeroBookId = TestDataFactory.createAuthorWithData("Zero", "BookId", 0);
            Response zeroIdResponse = authorService.createAuthor(authorWithZeroBookId);
            logger.info("Author creation with book ID 0 - Status: {}", zeroIdResponse.getStatusCode());
            
            Author authorWithMaxIntBookId = TestDataFactory.createAuthorWithData("MaxInt", "BookId", Integer.MAX_VALUE);
            Response maxIntResponse = authorService.createAuthor(authorWithMaxIntBookId);
            logger.info("Author creation with MAX_INT book ID - Status: {}", maxIntResponse.getStatusCode());
        });
        
        logger.info("Relationship boundary conditions test completed successfully");
    }
} 