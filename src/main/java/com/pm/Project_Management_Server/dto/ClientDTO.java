package com.pm.Project_Management_Server.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate onBoardedOn;
    private Integer clientRating;  // 0 to 10
}
