package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLeadDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private Long projectId;
    private LocalDate startDate;
    private LocalDate endDate;
}
