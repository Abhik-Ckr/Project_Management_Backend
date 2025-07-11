package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.CreateHighlightDTO;
import com.pm.Project_Management_Server.dto.HighlightDTO;
import com.pm.Project_Management_Server.entity.Highlight;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.exceptions.*;
import com.pm.Project_Management_Server.repositories.HighlightRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.HighlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HighlightServiceTest {

    @Mock
    private HighlightRepository highlightRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private HighlightServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddHighlight_Success() {
        CreateHighlightDTO dto = new CreateHighlightDTO();
        dto.setProjectId(1L);
        dto.setDescription("Demo highlight");

        Project project = new Project();
        project.setId(1L);

        Highlight saved = new Highlight();
        saved.setId(10L);
        saved.setDescription("Demo highlight");
        saved.setProject(project);
        saved.setCreatedOn(LocalDate.now());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(highlightRepository.save(any())).thenReturn(saved);

        HighlightDTO result = service.addHighlight(dto);
        assertEquals("Demo highlight", result.getDescription());
        assertEquals(1L, result.getProjectId());
    }

    @Test
    void testAddHighlight_DescriptionBlank() {
        CreateHighlightDTO dto = new CreateHighlightDTO();
        dto.setProjectId(1L);
        dto.setDescription("   ");

        assertThrows(InvalidHighlightDescriptionException.class, () -> service.addHighlight(dto));
    }

    @Test
    void testAddHighlight_ProjectNotFound() {
        CreateHighlightDTO dto = new CreateHighlightDTO();
        dto.setProjectId(99L);
        dto.setDescription("Highlight");

        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.addHighlight(dto));
    }

    @Test
    void testGetAllHighlights() {
        Project project = new Project();
        project.setId(1L);

        Highlight h1 = new Highlight();
        h1.setId(1L);
        h1.setProject(project);
        h1.setDescription("Highlight 1");
        h1.setCreatedOn(LocalDate.now());

        Highlight h2 = new Highlight();
        h2.setId(2L);
        h2.setProject(project);
        h2.setDescription("Highlight 2");
        h2.setCreatedOn(LocalDate.now());

        when(highlightRepository.findAll()).thenReturn(List.of(h1, h2));

        List<HighlightDTO> result = service.getAllHighlights();
        assertEquals(2, result.size());
        assertEquals("Highlight 1", result.get(0).getDescription());
    }

    @Test
    void testGetHighlightsByProject() {
        Project project = new Project();
        project.setId(2L);

        Highlight h = new Highlight();
        h.setId(1L);
        h.setProject(project);
        h.setDescription("Project Highlight");
        h.setCreatedOn(LocalDate.now());

        when(highlightRepository.findByProjectId(2L)).thenReturn(List.of(h));

        List<HighlightDTO> result = service.getHighlightsByProject(2L);
        assertEquals(1, result.size());
        assertEquals("Project Highlight", result.get(0).getDescription());
        assertEquals(2L, result.get(0).getProjectId());
    }

    @Test
    void testDeleteHighlight_Success() {
        when(highlightRepository.existsById(5L)).thenReturn(true);
        service.deleteHighlight(5L);
        verify(highlightRepository).deleteById(5L);
    }

    @Test
    void testDeleteHighlight_NotFound() {
        when(highlightRepository.existsById(999L)).thenReturn(false);
        assertThrows(HighlightNotFoundException.class, () -> service.deleteHighlight(999L));
    }

    @Test
    void testUpdateHighlight_Success() {
        Project project = new Project();
        project.setId(1L);

        Highlight existing = new Highlight();
        existing.setId(10L);
        existing.setProject(project);
        existing.setDescription("Old Desc");
        existing.setCreatedOn(LocalDate.of(2024, 1, 1));

        HighlightDTO dto = new HighlightDTO();
        dto.setProjectId(1L);
        dto.setDescription("Updated Desc");
        dto.setCreatedOn(LocalDate.of(2025, 7, 10));

        when(highlightRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(highlightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        HighlightDTO result = service.updateHighlight(10L, dto);
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(LocalDate.of(2025, 7, 10), result.getCreatedOn());
    }

    @Test
    void testUpdateHighlight_HighlightNotFound() {
        HighlightDTO dto = new HighlightDTO();
        dto.setProjectId(1L);
        dto.setDescription("New Desc");
        dto.setCreatedOn(LocalDate.now());

        when(highlightRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(HighlightNotFoundException.class, () -> service.updateHighlight(404L, dto));
    }

    @Test
    void testUpdateHighlight_ProjectNotFound() {
        Highlight existing = new Highlight();
        existing.setId(1L);

        HighlightDTO dto = new HighlightDTO();
        dto.setProjectId(999L);
        dto.setDescription("Doesn't Matter");
        dto.setCreatedOn(LocalDate.now());

        when(highlightRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.updateHighlight(1L, dto));
    }
}
