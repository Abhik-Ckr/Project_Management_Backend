package com.pm.Project_Management_Server.exceptions;

public class RateCardAlreadyExistsException extends RuntimeException {
    public RateCardAlreadyExistsException(String level) {
        super("Rate already exists for level: " + level);
    }
}
