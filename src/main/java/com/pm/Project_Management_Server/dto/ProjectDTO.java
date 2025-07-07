package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String projectName;
    private String type;
    private String department;
    private String status;            // Enum as String: ACTIVE, COMPLETED, etc.
    private Double budget;
    private Long clientId;
    private Long projectLeadId;
    private Long projectRateCardId;
}
