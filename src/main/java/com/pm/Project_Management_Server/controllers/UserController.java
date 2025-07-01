package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.UserCreateDTO;
import com.pm.Project_Management_Server.dto.UserDTO;
import com.pm.Project_Management_Server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO userDTO = userService.registerUser(userCreateDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> login(@RequestBody Map<String, String> loginRequest) {
        String userName = loginRequest.get("userName");
        String password = loginRequest.get("password");
        try {
            UserDTO userDTO = userService.login(userName, password);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
