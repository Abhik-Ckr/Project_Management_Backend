package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.*;
import com.pm.Project_Management_Server.services.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ---------- register ----------
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    // ---------- login (session) ----------
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO dto,
                                        HttpSession session) {
        authService.verifyLogin(dto.getEmail(), dto.getPassword());
        session.setAttribute("USER", dto.getEmail());
        return ResponseEntity.ok("login ok");
    }

    // ---------- logout ----------
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
