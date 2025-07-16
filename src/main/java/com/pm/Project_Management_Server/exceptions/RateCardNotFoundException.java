package com.pm.Project_Management_Server.exceptions;

public class RateCardNotFoundException extends RuntimeException {
    public RateCardNotFoundException(String msg) {
        super(msg);
    }
    public RateCardNotFoundException(Long id) {
        super("Rate card not found with ID: " + id);
    }

    public RateCardNotFoundException() {
        super("Rate card not found");
    }
}
