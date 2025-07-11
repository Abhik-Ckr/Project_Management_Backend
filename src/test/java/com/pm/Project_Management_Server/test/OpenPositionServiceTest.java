package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.OpenPositionDTO;
import com.pm.Project_Management_Server.entity.OpenPosition;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.exceptions.InvalidResourceLevelException;
import com.pm.Project_Management_Server.exceptions.OpenPositionNotFoundException;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.repositories.OpenPositionRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.OpenPositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenPositionServiceTest {

    @Mock
    private OpenPositionRepository openPositionRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private OpenPositionServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllOpenPositions() {
        Project project = new Project();
        project.setId(1L);

        OpenPosition op = new OpenPosition();
        op.setId(1L);
        op.setLevel(ResourceLevel.SR);
        op.setNumberRequired(2);
        op.setProject(project);

        when(openPositionRepository.findAll()).thenReturn(List.of(op));

        List<OpenPositionDTO> result = service.getAllOpenPositions();
        assertEquals(1, result.size());
        assertEquals(ResourceLevel.SR, result.get(0).getLevel());
        assertEquals(2, result.get(0).getNumberRequired());
    }

    @Test
    void testGetById_Success() {
        Project project = new Project();
        project.setId(2L);

        OpenPosition op = new OpenPosition();
        op.setId(2L);
        op.setLevel(ResourceLevel.EXPERT);
        op.setNumberRequired(3);
        op.setProject(project);

        when(openPositionRepository.findById(2L)).thenReturn(Optional.of(op));

        OpenPositionDTO result = service.getById(2L);
        assertEquals(3, result.getNumberRequired());
        assertEquals(ResourceLevel.EXPERT, result.getLevel());
        assertEquals(2L, result.getProjectId());
    }

    @Test
    void testGetById_NotFound() {
        when(openPositionRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(OpenPositionNotFoundException.class, () -> service.getById(100L));
    }

    @Test
    void testGetByProjectId() {
        Project project = new Project();
        project.setId(5L);

        OpenPosition op = new OpenPosition();
        op.setId(3L);
        op.setProject(project);
        op.setLevel(ResourceLevel.INTERMEDIATE);
        op.setNumberRequired(5);

        when(openPositionRepository.findByProjectId(5L)).thenReturn(List.of(op));

        List<OpenPositionDTO> result = service.getByProjectId(5L);
        assertEquals(1, result.size());
        assertEquals(ResourceLevel.INTERMEDIATE, result.get(0).getLevel());
        assertEquals(5L, result.get(0).getProjectId());
    }

    @Test
    void testCreateOpenPosition_Success() {
        OpenPositionDTO dto = new OpenPositionDTO();
        dto.setLevel(ResourceLevel.JR);
        dto.setNumberRequired(4);
        dto.setProjectId(10L);

        Project project = new Project();
        project.setId(10L);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(openPositionRepository.save(any())).thenAnswer(inv -> {
            OpenPosition saved = inv.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        OpenPositionDTO result = service.createOpenPosition(dto);
        assertEquals(ResourceLevel.JR, result.getLevel());
        assertEquals(4, result.getNumberRequired());
        assertEquals(10L, result.getProjectId());
    }

    @Test
    void testCreateOpenPosition_ProjectNotFound() {
        OpenPositionDTO dto = new OpenPositionDTO();
        dto.setProjectId(88L);
        dto.setLevel(ResourceLevel.ADVANCE);
        dto.setNumberRequired(2);

        when(projectRepository.findById(88L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.createOpenPosition(dto));
    }

    @Test
    void testCreateOpenPosition_NullLevel() {
        OpenPositionDTO dto = new OpenPositionDTO();
        dto.setProjectId(7L);
        dto.setLevel(null);
        dto.setNumberRequired(1);

        Project project = new Project();
        project.setId(7L);

        when(projectRepository.findById(7L)).thenReturn(Optional.of(project));

        assertThrows(InvalidResourceLevelException.class, () -> service.createOpenPosition(dto));
    }

    @Test
    void testDeleteOpenPosition_Success() {
        when(openPositionRepository.existsById(3L)).thenReturn(true);
        service.deleteOpenPosition(3L);
        verify(openPositionRepository).deleteById(3L);
    }

    @Test
    void testDeleteOpenPosition_NotFound() {
        when(openPositionRepository.existsById(500L)).thenReturn(false);
        assertThrows(OpenPositionNotFoundException.class, () -> service.deleteOpenPosition(500L));
    }

    @Test
    void testGetTotalOpenPositions() {
        OpenPosition op1 = new OpenPosition();
        op1.setNumberRequired(2);

        OpenPosition op2 = new OpenPosition();
        op2.setNumberRequired(5);

        OpenPosition op3 = new OpenPosition();
        op3.setNumberRequired(3);

        when(openPositionRepository.findAll()).thenReturn(List.of(op1, op2, op3));

        int total = service.getTotalOpenPositions();
        assertEquals(10, total);
    }
}
