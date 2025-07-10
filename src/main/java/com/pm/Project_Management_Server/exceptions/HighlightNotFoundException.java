package com.pm.Project_Management_Server.exceptions;

public class HighlightNotFoundException extends RuntimeException {
    public HighlightNotFoundException(Long id) {
        super("Highlight not found with id: " + id);
    }
}
