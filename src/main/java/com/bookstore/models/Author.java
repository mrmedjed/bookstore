package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author model class representing the Author entity
 * Using Lombok annotations to reduce boilerplate code
 * Following POJO design pattern with proper encapsulation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("idBook")
    private int idBook;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    // Constructor for creating new authors (without ID)
    public Author(int idBook, String firstName, String lastName) {
        this.idBook = idBook;
        this.firstName = firstName;
        this.lastName = lastName;
    }
} 