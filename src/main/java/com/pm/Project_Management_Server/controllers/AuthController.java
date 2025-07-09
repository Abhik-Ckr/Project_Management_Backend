package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.*;
import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.services.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

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
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO dto) {
        String jwt = authService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(jwt);
    }

    // ---------- logout ----------
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String,String> body,
                                            Authentication authentication) {

        String newPwd = body.get("newPassword");
        if (newPwd == null || newPwd.isBlank()) {
            return ResponseEntity.badRequest().body("newPassword is required");
        }

        Users u = (Users) authentication.getPrincipal();
        authService.changePassword(u.getEmail(), newPwd);
        return ResponseEntity.ok("Password updated successfully");
    }



}
