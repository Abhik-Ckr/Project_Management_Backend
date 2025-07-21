package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.Project.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.projection.EntityProjection;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String projectName;
    private ProjectType type;
    private String department;
    private String status;            // Enum as String: ACTIVE, COMPLETED, etc.
    private Double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long clientId;
    private Long projectRateCardId;
}
