package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRateCardDTO {
    private Long id;
    private ResourceLevel level;  // Junior / Senior / Expert etc.
    private Double rate;
}
