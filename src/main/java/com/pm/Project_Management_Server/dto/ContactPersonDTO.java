package com.pm.Project_Management_Server.dto;

import lombok.Data;

@Data
public class ContactPersonDTO {
    private Long id;
    private String name;
    private String email;
    private Long projectId;
}
