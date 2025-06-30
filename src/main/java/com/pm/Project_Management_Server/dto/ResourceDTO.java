package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private Long id;
    private Long projectId;
    private String resourceName;
    private ResourceLevel level;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer allocationPercentage;  // default 100
    private Integer exp;                   // years of experience
    private Boolean allocated;
}
