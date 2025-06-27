package com.pm.Project_Management_Server.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProjectRateCardDTO {
    private Long id;
    private Long projectId;
    private String level;
    private BigDecimal hourlyRate;
    private Boolean isActive;
    private LocalDateTime lastUpdated;
}
