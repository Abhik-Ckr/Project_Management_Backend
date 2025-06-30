package com.pm.Project_Management_Server.dto;



import com.pm.Project_Management_Server.entity.ResourceLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenPositionDTO {
    private Long id;
    private Long projectId;
    private ResourceLevel level;
    private Integer numberRequired;
}
