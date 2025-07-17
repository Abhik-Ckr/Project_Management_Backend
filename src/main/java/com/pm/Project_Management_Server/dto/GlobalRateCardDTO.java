package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalRateCardDTO {
    private Long id;
    private ResourceLevel level;
    private double rate;
    private LocalDate startDate;
    private LocalDate endDate;



}
