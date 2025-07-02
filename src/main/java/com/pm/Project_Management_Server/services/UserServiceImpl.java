package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.UserDTO;
import com.pm.Project_Management_Server.entity.User;
import com.pm.Project_Management_Server.entity.UserType;
import com.pm.Project_Management_Server.repositories.UserRepository;
import com.pm.Project_Management_Server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepo.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByType(String userType) {
        UserType type = UserType.valueOf(userType.toUpperCase());
        return userRepo.findByUserType(type)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepo.existsById(id)) return false;
        userRepo.deleteById(id);
        return true;
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getUserType().name()
        );
    }
}
