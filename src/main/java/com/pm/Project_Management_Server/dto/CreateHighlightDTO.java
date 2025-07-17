package com.pm.Project_Management_Server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHighlightDTO {
    private Long projectId;
    @NotBlank(message = "Description must not be blank")
    private String description;
    private LocalDate createdOn;


}
