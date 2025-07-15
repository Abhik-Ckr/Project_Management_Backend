package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.*;
import com.pm.Project_Management_Server.entity.*;
//import com.pm.Project_Management_Server.exceptions.BadCredentialsException;
import com.pm.Project_Management_Server.exceptions.EmailAlreadyExistsException;
import com.pm.Project_Management_Server.exceptions.InvalidLoginCredentialsException;
import com.pm.Project_Management_Server.exceptions.UserNotFoundException;
import com.pm.Project_Management_Server.exceptions.UsernameAlreadyExistsException;
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
        // no field for name, hence username shouldn't be unique
//        if (userRepo.existsByUserName(dto.getUserName()))
//            throw new UsernameAlreadyExistsException("Username already taken");
        if (userRepo.existsByEmail(dto.getEmail()))
            throw new EmailAlreadyExistsException("Email already taken");

        Users user = new Users();
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUserType(UserType.valueOf(dto.getUserType()));

        return UserMapper.toDTO(userRepo.save(user));
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
    }



    //temporarily storing exceptions in the file!!!
    public String login(String email, String rawPwd) {
        Users user = userRepo.findByEmail(email)
                .orElseThrow(InvalidLoginCredentialsException::new);
        if (!encoder.matches(rawPwd, user.getPassword()))
            throw new InvalidLoginCredentialsException();

        return jwtService.generateToken(user.getEmail());
    }
}
