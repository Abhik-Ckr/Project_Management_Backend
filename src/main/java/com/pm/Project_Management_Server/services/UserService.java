package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.UserCreateDTO;
import com.pm.Project_Management_Server.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(UserCreateDTO userCreateDTO);
    UserDTO login(String userName, String password);
} 