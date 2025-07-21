package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.UserDTO;
import com.pm.Project_Management_Server.entity.ProjectLead;
import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.entity.UserType;
import com.pm.Project_Management_Server.exceptions.InvalidUserTypeException;
import com.pm.Project_Management_Server.exceptions.UserNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectLeadRepository;
import com.pm.Project_Management_Server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final ProjectLeadRepository projectLeadRepo;

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return Optional.ofNullable(userRepo.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(id)))
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
        UserType type;
        try {
            type = UserType.valueOf(userType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserTypeException(userType);
        }

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

    @Override
    public List<Users> getAvailableProjectLeadUsers() {
        List<Users> allUsers = userRepo.findAll();
        List<ProjectLead> activeLeads = projectLeadRepo.findByEndDateIsNull();

        Set<Long> activeLeadUserIds = activeLeads.stream()
                .map(pl -> pl.getUser().getId())
                .collect(Collectors.toSet());

        return allUsers.stream()
                .filter(u -> u.getUserType() == UserType.USER) // only normal users
                .filter(u -> !activeLeadUserIds.contains(u.getId())) // not already assigned
                .collect(Collectors.toList());
    }


    private UserDTO mapToDTO(Users user) {
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getUserType().name()
        );
    }
}
