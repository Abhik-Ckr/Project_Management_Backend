package com.pm.Project_Management_Server.dto;

import lombok.Data;

@Data
public class ResourceRequiredDTO {
    private Long id;
    private String resourceLevel;  // jr, intermediate, sr, etc.
    private String expRange;        // Example: "2-4 years"
    private Integer quantity;
    private Long projectId;
}
