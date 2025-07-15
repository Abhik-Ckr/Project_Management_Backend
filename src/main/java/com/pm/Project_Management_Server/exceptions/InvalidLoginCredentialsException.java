package com.pm.Project_Management_Server.exceptions;

public class InvalidLoginCredentialsException extends RuntimeException {
    public InvalidLoginCredentialsException() {
        super("Invalid credentials");
    }

    public InvalidLoginCredentialsException(String msg) {
        super(msg);
    }
}
