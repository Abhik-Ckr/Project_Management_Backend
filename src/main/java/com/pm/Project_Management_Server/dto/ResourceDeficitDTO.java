package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDeficitDTO {
    private ResourceLevel level;
    private String status; // "DEFICIT" or "EXCESS"
    private int difference; // always positive
}

