package com.bookstore.services;

import com.bookstore.config.ApiConfig;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.filters;
import io.qameta.allure.restassured.AllureRestAssured;

/**
 * Base class for all API service classes
 * Handles common initialization and configuration
 */
@Slf4j
public abstract class ServiceBase {

    // Initialization flag to prevent duplicate setup
    private static boolean initialized = false;
    
    // Static initialization - runs once when any service class is first loaded
    static {
        log.debug("Initializing RestAssured configuration for API services");
        initializeRestAssured();
    }
    
    protected ServiceBase() {
        // Base constructor - can be extended by subclasses if needed
    }

    /**
     * Initialize RestAssured configuration
     * Called automatically via static block, but can be called explicitly if needed
     */
    public static void initializeRestAssured() {
        if (!initialized) {
            baseURI = ApiConfig.BASE_URL;
            // AllureRestAssured filter automatically captures all request/response data for Allure reports
            // This eliminates the need for manual .log() statements while providing comprehensive API logging
            // Configured once globally to avoid duplicate logging
            filters(new AllureRestAssured());
            initialized = true;
        }
    }
} 