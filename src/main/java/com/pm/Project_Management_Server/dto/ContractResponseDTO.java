package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {
    private Long contractId;
    private String projectName;
    private String duration;
    private Double quotedAmount;
    private String resourceLevel;
}
