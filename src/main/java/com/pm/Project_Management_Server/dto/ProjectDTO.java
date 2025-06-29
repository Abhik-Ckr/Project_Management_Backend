package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String projectName;
    private String type;
    private String department;
    private String status; // ENUM like ACTIVE, COMPLETED, etc.
    private Long clientId;
    private List<Long> resourceIds;
    private List<Long> highlightIds;
    private Long contractId;
    private Long projectRateCardId;
    private BigDecimal budgets;
    private Long contactPersonId;
    private Long managerId;
    private Long projectLeadId;
}
