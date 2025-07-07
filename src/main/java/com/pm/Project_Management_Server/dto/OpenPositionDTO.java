package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.Data;

@Data
public class OpenPositionDTO {
    private Long id;
    private ResourceLevel level;   // Enum: JUNIOR, MID, SENIOR, etc.
    private int numberRequired;
    private Long projectId;
}
