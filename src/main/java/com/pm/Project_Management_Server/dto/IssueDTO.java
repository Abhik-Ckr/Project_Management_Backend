package com.pm.Project_Management_Server.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IssueDTO {
    private Long id;
    private String severity;      // Enum as String: HIGH, MEDIUM, etc.
    private String description;
    private String createdBy;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private String status;        // Enum as String: OPEN, CLOSED, etc.

    private Long projectId;
}
