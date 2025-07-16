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
    private String level;
    private int required;
    private int allocated;
    private int deficit;  // Positive = Deficit, Negative = Excess, 0 = Balanced
}
