package com.pm.Project_Management_Server.dto;
 //creating and updating a Project rate card.

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRateCardDTO {
    private String level;
    private Double hourlyRate;
}
