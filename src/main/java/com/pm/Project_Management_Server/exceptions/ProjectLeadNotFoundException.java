package com.pm.Project_Management_Server.exceptions;

public class ProjectLeadNotFoundException extends RuntimeException {
    public ProjectLeadNotFoundException(Long id) {
        super("Project lead not found with id: " + id);
    }

    public ProjectLeadNotFoundException() {
        super("Project lead not found");
    }
}
