package com.pm.Project_Management_Server.exceptions;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Client not found with ID: " + id);
    }

    public ClientNotFoundException() {
        super("Client not found");
    }
}
