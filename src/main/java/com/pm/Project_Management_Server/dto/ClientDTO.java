package com.pm.Project_Management_Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate onBoardedOn;
    private Integer clientRating;  // 0 to 10
}
