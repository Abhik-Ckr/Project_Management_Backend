package com.pm.Project_Management_Server.exceptions;

public class InvalidIssueSeverityException extends RuntimeException {
    public InvalidIssueSeverityException(String severity) {
        super("Invalid severity level: " + severity);
    }
}
