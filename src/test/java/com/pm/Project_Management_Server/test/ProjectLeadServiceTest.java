//package com.pm.Project_Management_Server.test;
//
//import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
//import com.pm.Project_Management_Server.dto.UserDTO;
//import com.pm.Project_Management_Server.entity.Project;
//import com.pm.Project_Management_Server.entity.ProjectLead;
//import com.pm.Project_Management_Server.entity.UserType;
//import com.pm.Project_Management_Server.entity.Users;
//import com.pm.Project_Management_Server.exceptions.*;
//import com.pm.Project_Management_Server.repositories.ContactPersonRepository;
//import com.pm.Project_Management_Server.repositories.ProjectLeadRepository;
//import com.pm.Project_Management_Server.repositories.ProjectRepository;
//import com.pm.Project_Management_Server.repositories.UserRepository;
//import com.pm.Project_Management_Server.services.ProjectLeadServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ProjectLeadServiceTest {
//
//    @Mock private ProjectLeadRepository projectLeadRepository;
//    @Mock private ProjectRepository projectRepository;
//    @Mock private UserRepository userRepository;
//    @Mock private ContactPersonRepository contactPersonRepository;
//
//    @InjectMocks private ProjectLeadServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetAllProjectLeads() {
//        Users user = new Users(); user.setId(1L);
//        ProjectLead lead = new ProjectLead(); lead.setId(1L); lead.setUser(user);
//        when(projectLeadRepository.findAll()).thenReturn(List.of(lead));
//
//        List<ProjectLeadDTO> result = service.getAllProjectLeads();
//        assertEquals(1, result.size());
//        assertEquals(1L, result.get(0).getUserId());
//    }
//
//    @Test
//    void testGetById_Success() {
//        Users user = new Users(); user.setId(1L);
//        ProjectLead lead = new ProjectLead(); lead.setId(10L); lead.setUser(user);
//        when(projectLeadRepository.findById(10L)).thenReturn(Optional.of(lead));
//
//        ProjectLeadDTO dto = service.getById(10L);
//        assertEquals(10L, dto.getId());
//    }
//
//    @Test
//    void testGetById_NotFound() {
//        when(projectLeadRepository.findById(404L)).thenReturn(Optional.empty());
//        assertThrows(ProjectLeadNotFoundException.class, () -> service.getById(404L));
//    }
//
//    @Test
//    void testRemoveProjectLead_Success() {
//        when(projectLeadRepository.existsById(10L)).thenReturn(true);
//        service.removeProjectLead(10L);
//        verify(projectLeadRepository).deleteById(10L);
//    }
//
//    @Test
//    void testRemoveProjectLead_NotFound() {
//        when(projectLeadRepository.existsById(100L)).thenReturn(false);
//        assertThrows(ProjectLeadNotFoundException.class, () -> service.removeProjectLead(100L));
//    }
//
//    @Test
//    void testGetUserByProjectId_Success() {
//        Users user = new Users(); user.setId(1L); user.setUserName("John"); user.setEmail("john@example.com");user.setUserType(UserType.ADMIN);
//        ProjectLead lead = new ProjectLead(); lead.setUser(user);
//        Project project = new Project(); project.setId(10L); project.setProjectLead(lead);
//
//        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
//
//        UserDTO dto = service.getUserByProjectId(10L);
//        assertEquals("John", dto.getUserName());
//    }
//
//    @Test
//    void testGetUserByProjectId_ProjectNotFound() {
//        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
//        assertThrows(ProjectNotFoundException.class, () -> service.getUserByProjectId(99L));
//    }
//
//    @Test
//    void testGetUserByProjectId_NoLead() {
//        Project project = new Project(); project.setId(11L); project.setProjectLead(null);
//        when(projectRepository.findById(11L)).thenReturn(Optional.of(project));
//        assertThrows(NoLeadAssignedException.class, () -> service.getUserByProjectId(11L));
//    }
//
//    @Test
//    void testAddProjectLead_Success() {
//        Users user = new Users(); user.setId(1L);
//        ProjectLead saved = new ProjectLead(); saved.setId(10L); saved.setUser(user);
//
//        ProjectLeadDTO input = new ProjectLeadDTO(); input.setUserId(1L);
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(projectLeadRepository.save(any())).thenReturn(saved);
//
//        ProjectLeadDTO result = service.addProjectLead(input);
//        assertEquals(1L, result.getUserId());
//    }
//
//    @Test
//    void testAddProjectLead_UserNotFound() {
//        ProjectLeadDTO dto = new ProjectLeadDTO(); dto.setUserId(404L);
//        when(userRepository.findById(404L)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> service.addProjectLead(dto));
//    }
//
//    @Test
//    void testGetAllProjectLeadUsers() {
//        Users user = new Users(); user.setId(1L); user.setUserName("Lead"); user.setEmail("lead@test.com"); user.setUserType(UserType.ADMIN);
//        ProjectLead lead = new ProjectLead(); lead.setId(1L); lead.setUser(user);
//        when(projectLeadRepository.findAll()).thenReturn(List.of(lead));
//
//        List<UserDTO> result = service.getAllProjectLeadUsers();
//        assertEquals(1, result.size());
//        assertEquals("Lead", result.get(0).getUserName());
//    }
//}
