package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
