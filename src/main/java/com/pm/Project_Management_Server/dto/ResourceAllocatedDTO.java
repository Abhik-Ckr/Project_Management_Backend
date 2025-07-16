package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAllocatedDTO {
    private Long id;
    private Long resourceId;
    private Long projectId;
    private String resourceName;
    private ResourceLevel level;
    private LocalDate startDate;
    private LocalDate endDate;
}
