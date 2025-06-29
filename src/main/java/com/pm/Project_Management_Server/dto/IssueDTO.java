package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {
    private Long id;
    private Long projectId;
    private String severity;      // High / Urgent / Medium / Low
    private String description;
    private String createdBy;
    private LocalDate createdDate;
    private String status;        // Open / Resolved / InProgress etc.
}
