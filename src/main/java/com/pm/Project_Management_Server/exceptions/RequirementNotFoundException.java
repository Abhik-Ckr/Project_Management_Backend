package com.pm.Project_Management_Server.exceptions;

public class RequirementNotFoundException extends RuntimeException {
    public RequirementNotFoundException(Long id) {
        super("Requirement not found with id: " + id);
    }

    public RequirementNotFoundException() {
        super("Requirement not found");
    }
}
