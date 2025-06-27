package com.pm.Project_Management_Server.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GlobalRateCardDTO {
    private Long id;
    private String level;  // Junior / Senior / Expert etc.
    private BigDecimal hourlyRate;
}
