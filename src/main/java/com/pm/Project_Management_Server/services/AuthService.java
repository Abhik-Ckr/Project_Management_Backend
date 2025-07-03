package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.*;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.mapper.UserMapper;
import com.pm.Project_Management_Server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional
    public UserDTO register(UserCreateDTO dto) {
        if (userRepo.existsByUserName(dto.getUserName()))
            throw new IllegalStateException("username already taken");
        if (userRepo.existsByEmail(dto.getEmail()))
            throw new IllegalStateException("email already taken");

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserType(UserType.valueOf(dto.getUserType()));

        return UserMapper.toDTO(userRepo.save(user));
    }


    //temporarily storing exceptions in the file!!!
    public String login(String email, String rawPwd) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("bad credentials"));
        if (!encoder.matches(rawPwd, user.getPassword()))
            throw new IllegalArgumentException("bad credentials");

        return jwtService.generateToken(user.getEmail());
    }
}
