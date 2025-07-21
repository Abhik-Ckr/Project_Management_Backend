package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.Project;
import lombok.Data;

@Data
public class ProjectStatusUpdateRequestDTO {
    private Project.Status newStatus; // Enum: ACTIVE, ON_HOLD, COMPLETED, etc.
}

