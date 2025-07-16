package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import jdk.jshell.Snippet;
import lombok.*;

import java.time.LocalDate;

@Data

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    private Long id;
    private String resourceName;
    private ResourceLevel level;
    private LocalDate startDate;

    private Boolean allocated;



}
