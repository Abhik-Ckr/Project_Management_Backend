package com.pm.Project_Management_Server.dto;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@Data
public class ContractDTO {
    private Long id;
    private Long projectId;
    private Integer duration;  // Example: in weeks/months
    private Long resourceReqId;
    private BigDecimal amountQuoted;
}
