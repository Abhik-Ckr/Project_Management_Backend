package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.UserCreateDTO;
import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.entity.UserType;
//import com.pm.Project_Management_Server.exceptions.BadCredentialsException;
import com.pm.Project_Management_Server.exceptions.EmailAlreadyExistsException;
//import com.pm.Project_Management_Server.exceptions.UserNameAlreadyExistsException;
import com.pm.Project_Management_Server.exceptions.InvalidLoginCredentialsException;
import com.pm.Project_Management_Server.repositories.UserRepository;
import com.pm.Project_Management_Server.services.AuthService;
import com.pm.Project_Management_Server.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        UserCreateDTO dto = new UserCreateDTO("john", "john@example.com", "pass", "USER");

        when(userRepo.existsByUserName("john")).thenReturn(false);
        when(userRepo.existsByEmail("john@example.com")).thenReturn(false);
        when(encoder.encode("pass")).thenReturn("encodedPass");

        Users savedUser = new Users(1L, "john", "encodedPass", "john@example.com", UserType.USER);
        when(userRepo.save(any())).thenReturn(savedUser);

        var result = authService.register(dto);
        assertEquals("john", result.getUserName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("USER", result.getUserType());
    }

//    @Test
//    void testRegisterFails_UserNameExists() {
//        UserCreateDTO dto = new UserCreateDTO("john", "john@example.com", "pass", "USER");
//        when(userRepo.existsByUserName("john")).thenReturn(true);
//
//        assertThrows(UserNameAlreadyExistsException.class, () -> authService.register(dto));
//    }

    @Test
    void testRegisterFails_EmailExists() {
        UserCreateDTO dto = new UserCreateDTO("john", "john@example.com", "pass", "USER");
        when(userRepo.existsByUserName("john")).thenReturn(false);
        when(userRepo.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(dto));
    }

    @Test
    void testLoginSuccess() {
        Users user = new Users(1L, "john", "encodedPass", "john@example.com", UserType.USER);
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("pass", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken("john@example.com")).thenReturn("mocked-jwt");

        String token = authService.login("john@example.com", "pass");
        assertEquals("mocked-jwt", token);
    }

    @Test
    void testLoginFails_WrongEmail() {
        when(userRepo.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidLoginCredentialsException.class, () ->
                authService.login("invalid@example.com", "pass")
        );

    }

    @Test
    void testLoginFails_WrongPassword() {
        Users user = new Users(1L, "john", "encodedPass", "john@example.com", UserType.USER);
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        assertThrows(InvalidLoginCredentialsException.class, () ->
                authService.login("john@example.com", "wrongpass")
        );
    }

    @Test
    void testChangePasswordSuccess() {
        Users user = new Users(1L, "john", "encodedPass", "john@example.com", UserType.USER);
        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(encoder.encode("newpass")).thenReturn("newEncodedPass");

        authService.changePassword("john@example.com", "newpass");
        verify(userRepo).save(user);
        assertEquals("newEncodedPass", user.getPassword());
    }

    @Test
    void testChangePasswordFails_UserNotFound() {
        when(userRepo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.changePassword("notfound@example.com", "pass"));
    }
}
