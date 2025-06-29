package com.pm.Project_Management_Server.dto;


import lombok.AllArgsConstructor;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRateCardDTO {
    private Long id;
    @OneToOne
    @JoinColumn(name = "project_id")
    private Long projectId;
    private String level;
    private BigDecimal hourlyRate;
    private Boolean isActive;
    private LocalDateTime lastUpdated;
}
