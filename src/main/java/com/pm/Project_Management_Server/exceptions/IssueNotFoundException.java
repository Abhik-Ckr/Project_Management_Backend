package com.pm.Project_Management_Server.exceptions;

public class IssueNotFoundException extends RuntimeException {
    public IssueNotFoundException(Long id) {
        super("Issue not found with id: " + id);
    }

    public IssueNotFoundException() {
        super("Issue not found");
    }
}
