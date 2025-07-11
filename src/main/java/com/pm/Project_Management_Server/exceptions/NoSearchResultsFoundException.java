package com.pm.Project_Management_Server.exceptions;

public class NoSearchResultsFoundException extends RuntimeException {
    public NoSearchResultsFoundException(String keyword) {
        super("No search results found for keyword: " + keyword);
    }
}
