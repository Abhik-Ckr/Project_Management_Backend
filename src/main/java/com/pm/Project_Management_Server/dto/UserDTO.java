package com.pm.Project_Management_Server.dto;

import com.pm.Project_Management_Server.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String userName;
    private String email;
    private String userType;  // Enum: ADMIN, MANAGER, OTHER


}
