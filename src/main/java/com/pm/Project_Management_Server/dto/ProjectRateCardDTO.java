package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRateCardDTO {
    private Long id;
    private Long projectId;
    private ResourceLevel level;
    private double rate;
    private boolean active;
    private LocalDateTime lastUpdated;



    public boolean getActive() {return active;
    }
}
