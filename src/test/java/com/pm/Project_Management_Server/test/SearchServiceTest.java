package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ClientDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.dto.SearchResultDTO;
import com.pm.Project_Management_Server.entity.Client;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.exceptions.NoSearchResultsFoundException;
import com.pm.Project_Management_Server.repositories.ClientRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock private ProjectRepository projectRepo;
    @Mock private ClientRepository clientRepo;

    @InjectMocks private SearchServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGlobalSearch_ReturnsResults() {
        Project p = Project.builder().id(1L).projectName("Test Project").build();
        Client c = Client.builder().id(2L).name("Test Client").build();

        when(projectRepo.findByProjectNameContainingIgnoreCase("test")).thenReturn(List.of(p));
        when(clientRepo.findByNameContainingIgnoreCase("test")).thenReturn(List.of(c));

        SearchResultDTO result = service.globalSearch("test");

        assertEquals(1, result.getProjects().size());
        assertEquals(1, result.getClients().size());
        assertEquals("Test Project", result.getProjects().get(0).getProjectName());
        assertEquals("Test Client", result.getClients().get(0).getName());
    }

    @Test
    void testGlobalSearch_OnlyProjects() {
        Project p = Project.builder().id(1L).projectName("Alpha").build();

        when(projectRepo.findByProjectNameContainingIgnoreCase("alpha")).thenReturn(List.of(p));
        when(clientRepo.findByNameContainingIgnoreCase("alpha")).thenReturn(Collections.emptyList());

        SearchResultDTO result = service.globalSearch("alpha");

        assertEquals(1, result.getProjects().size());
        assertEquals(0, result.getClients().size());
    }

    @Test
    void testGlobalSearch_OnlyClients() {
        Client c = Client.builder().id(2L).name("Beta Corp").build();

        when(projectRepo.findByProjectNameContainingIgnoreCase("beta")).thenReturn(Collections.emptyList());
        when(clientRepo.findByNameContainingIgnoreCase("beta")).thenReturn(List.of(c));

        SearchResultDTO result = service.globalSearch("beta");

        assertEquals(0, result.getProjects().size());
        assertEquals(1, result.getClients().size());
    }

    @Test
    void testGlobalSearch_NoResults_ThrowsException() {
        when(projectRepo.findByProjectNameContainingIgnoreCase("zzz")).thenReturn(Collections.emptyList());
        when(clientRepo.findByNameContainingIgnoreCase("zzz")).thenReturn(Collections.emptyList());

        assertThrows(NoSearchResultsFoundException.class, () -> service.globalSearch("zzz"));
    }
}
