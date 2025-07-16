package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.UserDTO;
import com.pm.Project_Management_Server.entity.UserType;
import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.exceptions.InvalidUserTypeException;
import com.pm.Project_Management_Server.exceptions.UserNotFoundException;
import com.pm.Project_Management_Server.repositories.UserRepository;
import com.pm.Project_Management_Server.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepo;
    @InjectMocks private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_Success() {
        Users user = Users.builder()
                .id(1L)
                .userName("syam")
                .password("pw")
                .email("syam@email.com")
                .userType(UserType.ADMIN)
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = service.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("syam", result.get().getUserName());
    }


    @Test
    void testGetUserById_NotFound() {
        when(userRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserById(99L));
    }

    @Test
    void testGetAllUsers() {
        Users u1 = Users.builder()
                .id(1L)
                .userName("Alice")
                .password("x")
                .email("alice@x.com")
                .userType(UserType.USER)
                .build();

        Users u2 = Users.builder()
                .id(2L)
                .userName("Bob")
                .password("y")
                .email("bob@x.com")
                .userType(UserType.USER)
                .build();

        when(userRepo.findAll()).thenReturn(List.of(u1, u2));

        List<UserDTO> result = service.getAllUsers();
        assertEquals(2, result.size());
    }


    @Test
    void testGetUsersByType_Valid() {
        Users u = Users.builder()
                .id(1L)
                .userName("Arjun")
                .password("abc")
                .email("a@a.com")
                .userType(UserType.ADMIN)
                .build();

        when(userRepo.findByUserType(UserType.ADMIN)).thenReturn(List.of(u));

        List<UserDTO> result = service.getUsersByType("admin");
        assertEquals(1, result.size());
        assertEquals("Arjun", result.get(0).getUserName());
    }


    @Test
    void testGetUsersByType_Invalid() {
        assertThrows(InvalidUserTypeException.class, () -> service.getUsersByType("not_a_type"));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepo.existsById(1L)).thenReturn(true);
        boolean result = service.deleteUser(1L);
        assertTrue(result);
        verify(userRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotExists() {
        when(userRepo.existsById(2L)).thenReturn(false);
        boolean result = service.deleteUser(2L);
        assertFalse(result);
        verify(userRepo, never()).deleteById(2L);
    }
}
