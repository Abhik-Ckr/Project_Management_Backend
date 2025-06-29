package com.pm.Project_Management_Server.mapper;

import com.pm.Project_Management_Server.dto.UserDTO;
import com.pm.Project_Management_Server.entity.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserDTO toDTO(User u) {
        return new UserDTO(
                u.getId(),
                u.getUserName(),
                u.getEmail(),
                u.getUserType().name()
        );
    }
}
