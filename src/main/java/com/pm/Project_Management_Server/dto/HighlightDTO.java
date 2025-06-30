package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HighlightDTO {
    private Long id;
    private Long projectId;
    private String description;
    private LocalDate createdOn;
}
