package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ResourceDTO {
    private Long id;
    private String resourceName;
    private ResourceLevel level;
    private LocalDate startDate;
    private LocalDate endDate;
    private int exp;
    private boolean allocated;
    private Long projectId;  // Nullable — since resource may not be assigned to a project
}
