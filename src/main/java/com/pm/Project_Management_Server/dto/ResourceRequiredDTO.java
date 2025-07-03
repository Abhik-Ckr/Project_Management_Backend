package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.Data;

@Data
public class ResourceRequiredDTO {
    private Long id;
    private ResourceLevel level;
    private int quantity;
    private Long projectId;


}
