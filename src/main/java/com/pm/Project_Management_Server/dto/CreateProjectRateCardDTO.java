package com.pm.Project_Management_Server.dto;
 //creating and updating a Project rate card.

import com.pm.Project_Management_Server.entity.ResourceLevel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRateCardDTO {
    private ResourceLevel level;
    private Double rate;
}
