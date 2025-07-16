package com.pm.Project_Management_Server.exceptions;

public class InvalidHighlightDescriptionException extends RuntimeException {
    public InvalidHighlightDescriptionException() {
        super("Description must not be null or blank");
    }
}
