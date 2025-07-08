package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDTO> getUserById(Long id);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByType(String userType);
    boolean deleteUser(Long id);

}
