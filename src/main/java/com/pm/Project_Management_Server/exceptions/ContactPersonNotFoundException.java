package com.pm.Project_Management_Server.exceptions;

public class ContactPersonNotFoundException extends RuntimeException {
    public ContactPersonNotFoundException(Long projectId) {
        super("No contact person found for project ID: " + projectId);
    }
}
