package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book model class representing the Book entity
 * Using Lombok annotations to reduce boilerplate code
 * Following POJO design pattern with proper encapsulation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("pageCount")
    private int pageCount;
    
    @JsonProperty("excerpt")
    private String excerpt;
    
    @JsonProperty("publishDate")
    private String publishDate;
    
    // Constructor for creating new books (without ID)
    public Book(String title, String description, int pageCount, String excerpt, String publishDate) {
        this.title = title;
        this.description = description;
        this.pageCount = pageCount;
        this.excerpt = excerpt;
        this.publishDate = publishDate;
    }
} 