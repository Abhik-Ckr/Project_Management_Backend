package com.pm.Project_Management_Server.dto;


import com.pm.Project_Management_Server.entity.ResourceLevel;
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
    private Long projectId;
    private ResourceLevel level;
    private Double rate;
    private Boolean active;
    private LocalDateTime lastUpdated;


}
