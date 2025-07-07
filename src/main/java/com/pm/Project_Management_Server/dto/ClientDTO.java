package com.pm.Project_Management_Server.dto;

import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate onBoardedOn;
    private Integer clientRating;



}
