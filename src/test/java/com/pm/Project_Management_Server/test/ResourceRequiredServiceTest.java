package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ResourceRequiredDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceRequired;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.RequirementNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.ResourceRequiredRepository;
import com.pm.Project_Management_Server.services.ResourceRequiredServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceRequiredServiceTest {

    @Mock
    private ResourceRequiredRepository requirementRepo;

    @Mock
    private ProjectRepository projectRepo;

    @InjectMocks
    private ResourceRequiredServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------- Service Method Tests ------------

    @Test
    void testAddRequirement_Success() {
        Project project = Project.builder().id(1L).build();
        ResourceRequiredDTO dto = new ResourceRequiredDTO();
        dto.setProjectId(1L);
        dto.setLevel(ResourceLevel.SR);
        dto.setQuantity(5);

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(requirementRepo.save(any())).thenAnswer(inv -> {
            ResourceRequired r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        ResourceRequiredDTO result = service.addRequirement(dto);
        assertEquals(100L, result.getId());
        assertEquals(ResourceLevel.SR, result.getLevel());
        assertEquals(5, result.getQuantity());
        assertEquals(1L, result.getProjectId());
    }

    @Test
    void testGetRequirementsByProject() {
        Project p = Project.builder().id(1L).build();
        ResourceRequired r1 = new ResourceRequired();
        r1.setId(10L);
        r1.setProject(p);
        r1.setResourceLevel(ResourceLevel.JR);
        r1.setQuantity(3);

        when(requirementRepo.findByProjectId(1L)).thenReturn(List.of(r1));

        List<ResourceRequiredDTO> result = service.getRequirementsByProject(1L);
        assertEquals(1, result.size());
        assertEquals(ResourceLevel.JR, result.get(0).getLevel());
        assertEquals(3, result.get(0).getQuantity());
    }

    @Test
    void testUpdateRequirement_Success() {
        ResourceRequired existing = new ResourceRequired();
        existing.setId(10L);
        existing.setResourceLevel(ResourceLevel.JR);
        existing.setQuantity(2);
        existing.setProject(Project.builder().id(1L).build());

        ResourceRequiredDTO dto = new ResourceRequiredDTO();
        dto.setLevel(ResourceLevel.EXPERT);
        dto.setQuantity(7);

        when(requirementRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(requirementRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResourceRequiredDTO result = service.updateRequirement(10L, dto);
        assertEquals(ResourceLevel.EXPERT, result.getLevel());
        assertEquals(7, result.getQuantity());
    }

    @Test
    void testDeleteRequirement_Success() {
        when(requirementRepo.existsById(99L)).thenReturn(true);
        doNothing().when(requirementRepo).deleteById(99L);

        assertDoesNotThrow(() -> service.deleteRequirement(99L));
        verify(requirementRepo, times(1)).deleteById(99L);
    }

    // ------------ Exception Tests -------------

    @Test
    void testAddRequirement_ProjectNotFound() {
        ResourceRequiredDTO dto = new ResourceRequiredDTO();
        dto.setProjectId(404L);

        when(projectRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> service.addRequirement(dto));
    }

    @Test
    void testUpdateRequirement_RequirementNotFound() {
        ResourceRequiredDTO dto = new ResourceRequiredDTO();
        when(requirementRepo.findById(88L)).thenReturn(Optional.empty());

        assertThrows(RequirementNotFoundException.class, () -> service.updateRequirement(88L, dto));
    }

    @Test
    void testDeleteRequirement_RequirementNotFound() {
        when(requirementRepo.existsById(77L)).thenReturn(false);
        assertThrows(RequirementNotFoundException.class, () -> service.deleteRequirement(77L));
    }
}
