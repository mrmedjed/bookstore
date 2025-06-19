package com.bookstore.config;


import lombok.experimental.UtilityClass;

/**
 * API Configuration class containing base URLs and endpoint paths
 * Following the Single Responsibility Principle - handles only configuration
 * Using Lombok @UtilityClass for cleaner static constant management
 */
@UtilityClass
public class ApiConfig {
    
    // Base configuration
    public static final String BASE_URL = "https://fakerestapi.azurewebsites.net";
    public static final String API_VERSION = "/api/v1";
    
    // Books API endpoints
    public static final String BOOKS_ENDPOINT = API_VERSION + "/Books";
    public static final String BOOK_BY_ID_ENDPOINT = API_VERSION + "/Books/{id}";
    
    // Authors API endpoints
    public static final String AUTHORS_ENDPOINT = API_VERSION + "/Authors";
    public static final String AUTHOR_BY_ID_ENDPOINT = API_VERSION + "/Authors/{id}";
    
    // Test data constants
    public static final int VALID_BOOK_ID = 1;
    public static final int VALID_AUTHOR_ID = 1;
    public static final int INVALID_ID = 99999;
    public static final int NEGATIVE_ID = -1;
    
    // Timeout constants
    public static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    public static final int SHORT_TIMEOUT = 5000;    // 5 seconds
    public static final int LONG_TIMEOUT = 60000;    // 60 seconds
    
    // Performance thresholds
    public static final long ACCEPTABLE_RESPONSE_TIME = 2000L; // 2 seconds
    public static final long FAST_RESPONSE_TIME = 500L;        // 500ms
} 