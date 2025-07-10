package com.pm.Project_Management_Server.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }
}
