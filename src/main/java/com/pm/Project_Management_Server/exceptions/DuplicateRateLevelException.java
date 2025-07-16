package com.pm.Project_Management_Server.exceptions;

public class DuplicateRateLevelException extends RuntimeException {
    public DuplicateRateLevelException(String level) {
        super("Rate level already exists: " + level);
    }
}
