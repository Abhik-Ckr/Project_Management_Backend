package com.pm.Project_Management_Server.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid credentials");
    }
}
