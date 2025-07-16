package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRateCardDTO {
    private Long id;
    private Long projectId;
    private ResourceLevel level;
    private double rate;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;


    public boolean getActive() { return active;
    }

    public boolean isActive() { return active;
    }
}

