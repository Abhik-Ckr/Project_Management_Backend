package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateIssueDTO {
    private Long projectId;
    private String severity;
    private String description;
    private String createdBy;
}
