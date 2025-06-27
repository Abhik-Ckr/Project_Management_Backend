package com.pm.Project_Management_Server.dto;

import lombok.Data;

@Data
public class OpenPositionDTO {
    private Long id;
    private Long projectId;
    private String level;
    private Integer numberRequired;
}
