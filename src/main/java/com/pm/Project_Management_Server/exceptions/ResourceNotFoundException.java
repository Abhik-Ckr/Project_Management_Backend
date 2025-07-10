package com.pm.Project_Management_Server.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource) {
        super(resource + " not found.");
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found.");
    }
}
