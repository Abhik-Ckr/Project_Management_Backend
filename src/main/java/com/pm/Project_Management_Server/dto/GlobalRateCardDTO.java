package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRateCardDTO {
    private Long id;
    private String level;  // Junior / Senior / Expert etc.
    private BigDecimal hourlyRate;
}
