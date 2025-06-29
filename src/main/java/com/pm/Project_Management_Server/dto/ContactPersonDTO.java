package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPersonDTO {
    private Long id;
    private String name;
    private String email;
    private Long projectId;
}
