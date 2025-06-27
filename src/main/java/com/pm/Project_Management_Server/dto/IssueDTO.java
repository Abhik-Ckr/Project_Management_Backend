package com.pm.Project_Management_Server.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class IssueDTO {
    private Long id;
    private Long projectId;
    private String severity;      // High / Urgent / Medium / Low
    private String description;
    private String createdBy;
    private LocalDate createdDate;
    private String status;        // Open / Resolved / InProgress etc.
}
