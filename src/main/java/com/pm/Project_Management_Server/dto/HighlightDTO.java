package com.pm.Project_Management_Server.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HighlightDTO {
    private Long id;
    private Long projectId;
    private String description;
    private LocalDate createdOn;
}
