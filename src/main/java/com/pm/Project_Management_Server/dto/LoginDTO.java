package com.pm.Project_Management_Server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank private String userName;
    @NotBlank private String password;
}
