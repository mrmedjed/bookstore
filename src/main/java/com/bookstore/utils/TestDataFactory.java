package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Test Data Factory for generating test data
 * Following the Factory Pattern for object creation
 * Using Lombok @UtilityClass for cleaner static methods
 */
@UtilityClass
public class TestDataFactory {
    
    private static final Random random = new Random();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    /**
     * Creates a valid Book object with sample data using Lombok builder
     */
    public static Book createValidBook() {
        return Book.builder()
                .title("Test Book Title " + RandomStringUtils.randomAlphabetic(5))
                .description("This is a test book description for API testing purposes")
                .pageCount(random.nextInt(500) + 100) // Pages between 100-600
                .excerpt("This is a sample excerpt from the test book")
                .publishDate(LocalDateTime.now().format(dateFormatter))
                .build();
    }
    
    /**
     * Creates a Book object with specific data using Lombok builder
     */
    public static Book createBookWithData(String title, String description, int pageCount) {
        return Book.builder()
                .title(title)
                .description(description)
                .pageCount(pageCount)
                .excerpt("Sample excerpt for " + title)
                .publishDate(LocalDateTime.now().format(dateFormatter))
                .build();
    }
    
    /**
     * Creates a Book with minimal required data
     */
    public static Book createMinimalBook(String title) {
        return Book.builder()
                .title(title)
                .description("Minimal description")
                .pageCount(100)
                .excerpt("Minimal excerpt")
                .publishDate(LocalDateTime.now().format(dateFormatter))
                .build();
    }
    
    /**
     * Creates a Book with all fields populated
     */
    public static Book createCompleteBook() {
        return Book.builder()
                .id(generateRandomId())
                .title("Complete Test Book " + RandomStringUtils.randomAlphabetic(5))
                .description("Complete description with all details for comprehensive testing")
                .pageCount(random.nextInt(500) + 100)
                .excerpt("Complete excerpt providing full context")
                .publishDate(LocalDateTime.now().format(dateFormatter))
                .build();
    }
    
    /**
     * Creates a Book with invalid data for negative testing
     */
    public static Book createInvalidBook() {
        return Book.builder()
                .title("") // Empty title
                .description("") // Empty description
                .pageCount(-1) // Negative page count
                .excerpt("")
                .publishDate("invalid-date")
                .build();
    }
    
    /**
     * Creates a Book with very long data for boundary testing
     */
    public static Book createBookWithLongData() {
        return Book.builder()
                .title(RandomStringUtils.randomAlphabetic(1000))
                .description(RandomStringUtils.randomAlphabetic(5000))
                .pageCount(Integer.MAX_VALUE)
                .excerpt(RandomStringUtils.randomAlphabetic(2000))
                .publishDate(LocalDateTime.now().format(dateFormatter))
                .build();
    }
    
    /**
     * Creates a valid Author object with sample data using Lombok builder
     */
    public static Author createValidAuthor() {
        return Author.builder()
                .idBook(random.nextInt(100) + 1) // Random book ID
                .firstName("John" + RandomStringUtils.randomAlphabetic(3))
                .lastName("Doe" + RandomStringUtils.randomAlphabetic(3))
                .build();
    }
    
    /**
     * Creates an Author object with specific data using Lombok builder
     */
    public static Author createAuthorWithData(String firstName, String lastName, int bookId) {
        return Author.builder()
                .idBook(bookId)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
    
    /**
     * Creates a complete Author with ID
     */
    public static Author createCompleteAuthor() {
        return Author.builder()
                .id(generateRandomId())
                .idBook(random.nextInt(100) + 1)
                .firstName("Complete" + RandomStringUtils.randomAlphabetic(3))
                .lastName("Author" + RandomStringUtils.randomAlphabetic(3))
                .build();
    }
    
    /**
     * Creates an Author with invalid data for negative testing
     */
    public static Author createInvalidAuthor() {
        return Author.builder()
                .idBook(-1) // Invalid book ID
                .firstName("") // Empty first name
                .lastName("") // Empty last name
                .build();
    }
    
    /**
     * Creates an Author with very long data for boundary testing
     */
    public static Author createAuthorWithLongData() {
        return Author.builder()
                .idBook(Integer.MAX_VALUE)
                .firstName(RandomStringUtils.randomAlphabetic(1000))
                .lastName(RandomStringUtils.randomAlphabetic(1000))
                .build();
    }
    
    /**
     * Generate random valid ID
     */
    public static int generateRandomId() {
        return random.nextInt(1000) + 1;
    }
    
    /**
     * Generate invalid IDs for negative testing
     */
    public static int generateInvalidId() {
        return random.nextInt(99999) + 10000; // Large ID that likely doesn't exist
    }
    
    /**
     * Generate multiple books for bulk testing
     */
    public static Book[] createMultipleBooks(int count) {
        Book[] books = new Book[count];
        for (int i = 0; i < count; i++) {
            books[i] = Book.builder()
                    .title("Bulk Book " + (i + 1))
                    .description("Description for bulk book " + (i + 1))
                    .pageCount(random.nextInt(300) + 100)
                    .excerpt("Excerpt " + (i + 1))
                    .publishDate(LocalDateTime.now().format(dateFormatter))
                    .build();
        }
        return books;
    }
    
    /**
     * Generate multiple authors for bulk testing
     */
    public static Author[] createMultipleAuthors(int count) {
        Author[] authors = new Author[count];
        for (int i = 0; i < count; i++) {
            authors[i] = Author.builder()
                    .idBook(i + 1)
                    .firstName("Author" + (i + 1))
                    .lastName("Last" + (i + 1))
                    .build();
        }
        return authors;
    }
} 