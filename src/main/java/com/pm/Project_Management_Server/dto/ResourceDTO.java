package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    private Long id;
    private String resourceName;
    private ResourceLevel level;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean allocated;
    private Long projectId;  // Nullable — since resource may not be assigned to a project
    private LocalDate actualEndDate;
    private boolean exited;

}
