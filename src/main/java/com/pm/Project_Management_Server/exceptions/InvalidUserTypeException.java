package com.pm.Project_Management_Server.exceptions;

public class InvalidUserTypeException extends RuntimeException {
    public InvalidUserTypeException(String type) {
        super("Invalid user type: " + type);
    }
}
