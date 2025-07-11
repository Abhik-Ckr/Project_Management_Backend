package com.pm.Project_Management_Server.exceptions;

public class NoLeadAssignedException extends RuntimeException {
    public NoLeadAssignedException(Long projectId) {
        super("No project lead or user assigned for project ID: " + projectId);
    }
}
