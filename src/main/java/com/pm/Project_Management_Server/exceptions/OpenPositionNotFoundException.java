package com.pm.Project_Management_Server.exceptions;

public class OpenPositionNotFoundException extends RuntimeException {
    public OpenPositionNotFoundException(Long id) {
        super("Open position not found with id: " + id);
    }

    public OpenPositionNotFoundException() {
        super("Open position not found");
    }
}
