package com.pm.Project_Management_Server.exceptions;

public class InvalidResourceLevelException extends RuntimeException {
    public InvalidResourceLevelException() {
        super("Resource level cannot be null");
    }
}
