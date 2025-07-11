package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.exceptions.*;
import com.pm.Project_Management_Server.repositories.*;
import com.pm.Project_Management_Server.services.ProjectServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Mock private ProjectRepository projectRepo;
    @Mock private ProjectRateCardRepository projectRateCardRepo;
    @Mock private GlobalRateCardRepository globalRateCardRepo;
    @Mock private ProjectRateCardRepository rateCardRepo;
    @Mock private HighlightRepository highlightRepo;
    @Mock private ResourceRepository resourceRepo;
    @Mock private ProjectLeadRepository leadRepo;
    @Mock private ClientRepository clientRepo;
    @Mock private ContactPersonRepository contactPersonRepo;

    @InjectMocks
    private ProjectServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProjects() {
        Project project = Project.builder()
                .id(1L)
                .projectName("Test")
                .status(Project.Status.ACTIVE)
                .build();
        when(projectRepo.findAll()).thenReturn(List.of(project));
        assertEquals(1, service.getAllProjects().size());
    }

    @Test
    void testCreateProject() {
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectName("Build CRM");
        Project project = Project.builder().projectName("Build CRM").build();
        when(projectRepo.save(any())).thenReturn(project);
        assertEquals("Build CRM", service.createProject(dto).getProjectName());
    }

    @Test
    void testGetProjectById() {
        Project project = Project.builder().id(1L).projectName("CRM").build();
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        assertEquals("CRM", service.getProjectById(1L).getProjectName());
    }

    @Test
    void testDeleteProject() {
        service.deleteProject(10L);
        verify(projectRepo).deleteById(10L);
    }

    @Test
    void testUpdateProject_Success() {
        Project existing = Project.builder().id(1L).build();
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectName("Updated");
        dto.setStatus("ACTIVE");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepo.save(any())).thenReturn(existing);
        ProjectDTO result = service.updateProject(1L, dto);
        assertEquals("Updated", result.getProjectName());
    }

    @Test
    void testCalculateBudgetSpent() {
        Project project = Project.builder().id(1L).build();
        Resource r = Resource.builder()
                .startDate(LocalDate.now().minusDays(5))
                .level(ResourceLevel.JR)
                .build();
        when(resourceRepo.findByProjectId(1L)).thenReturn(List.of(r));
        when(projectRateCardRepo.findByProjectIdAndLevel(any(), any()))
                .thenReturn(Optional.of(ProjectRateCard.builder().rate(100.0).active(true).build()));
        assertTrue(service.calculateBudgetSpent(project) > 0);
    }

    @Test
    void testCalculateBudgetSpentById() {
        Project p = Project.builder().id(1L).build();
        when(projectRepo.findById(1L)).thenReturn(Optional.of(p));
        when(resourceRepo.findByProjectId(1L)).thenReturn(Collections.emptyList());
        assertEquals(0.0, service.calculateBudgetSpentById(1L));
    }

    @Test
    void testGetProjectsOverBudget() {
        Project project = Project.builder()
                .id(1L)
                .budget(10.0)
                .status(Project.Status.ACTIVE)
                .build();
        when(projectRepo.findAll()).thenReturn(List.of(project));
        when(resourceRepo.findByProjectId(1L)).thenReturn(Collections.emptyList());
        assertEquals(0, service.getProjectsOverBudget().size());
    }

    @Test
    void testGetProjectByLeadId_Success() {
        Project project = Project.builder().id(1L).build();
        when(projectRepo.findByProjectLeadId(1L)).thenReturn(Optional.of(project));
        assertEquals(1L, service.getProjectByLeadId(1L).getId());
    }

    @Test
    void testGetProjectsByClient() {
        Project p = Project.builder().id(1L).build();
        when(projectRepo.findByClientId(1L)).thenReturn(List.of(p));
        assertEquals(1, service.getProjectsByClient(1L).size());
    }

    @Test
    void testGetProjectsByStatus() {
        Project p = Project.builder().id(1L).status(Project.Status.ACTIVE).build();
        when(projectRepo.findByStatus(Project.Status.ACTIVE)).thenReturn(List.of(p));
        assertEquals(1, service.getProjectsByStatus(Project.Status.ACTIVE).size());
    }

    @Test
    void testGetContactPersonByProjectId_Success() {
        Project p = Project.builder().id(1L).build();
        ContactPerson cp = ContactPerson.builder()
                .id(5L)
                .project(p)
                .name("Abhi")
                .email("abhi@pm.com")
                .build();
        when(contactPersonRepo.findByProjectId(1L)).thenReturn(Optional.of(cp));
        ContactPersonDTO dto = service.getContactPersonByProjectId(1L);
        assertEquals("Abhi", dto.getName());
    }

    // ----------------- ❌ EXCEPTION TESTS -----------------

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.getProjectById(9L));
    }

    @Test
    void testUpdateProject_NotFound() {
        when(projectRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.updateProject(99L, new ProjectDTO()));
    }

    @Test
    void testUpdateProject_ClientNotFound() {
        Project p = Project.builder().id(1L).build();
        ProjectDTO dto = new ProjectDTO();
        dto.setClientId(404L);
        dto.setStatus("ACTIVE");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(p));
        when(clientRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ClientNotFoundException.class, () -> service.updateProject(1L, dto));
    }

    @Test
    void testUpdateProject_LeadNotFound() {
        Project p = Project.builder().id(1L).build();
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectLeadId(404L);
        dto.setStatus("ACTIVE");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(p));
        when(leadRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ProjectLeadNotFoundException.class, () -> service.updateProject(1L, dto));
    }

    @Test
    void testUpdateProject_RateCardNotFound() {
        Project p = Project.builder().id(1L).build();
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectRateCardId(404L);
        dto.setStatus("ACTIVE");
        when(projectRepo.findById(1L)).thenReturn(Optional.of(p));
        when(rateCardRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(RateCardNotFoundException.class, () -> service.updateProject(1L, dto));
    }


    @Test
    void testGetProjectByLeadId_NotFound() {
        when(projectRepo.findByProjectLeadId(111L)).thenReturn(Optional.empty());
        assertThrows(ProjectForLeadNotFoundException.class, () -> service.getProjectByLeadId(111L));
    }

    @Test
    void testGetContactPerson_NotFound() {
        when(contactPersonRepo.findByProjectId(50L)).thenReturn(Optional.empty());
        assertThrows(ContactPersonNotFoundException.class, () -> service.getContactPersonByProjectId(50L));
    }
}
