package com.pm.Project_Management_Server.exceptions;

public class ProjectForLeadNotFoundException extends RuntimeException {
    public ProjectForLeadNotFoundException(Long leadId) {
        super("No project found for this lead (ID: " + leadId + ")");
    }
}
