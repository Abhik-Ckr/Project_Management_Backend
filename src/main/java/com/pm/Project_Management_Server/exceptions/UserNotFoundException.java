package com.pm.Project_Management_Server.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg) {
        super(msg);
    }
    public UserNotFoundException(Long id) {
        super("User not found with ID: " + id);
    }
}
