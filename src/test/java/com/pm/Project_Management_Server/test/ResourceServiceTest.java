//package com.pm.Project_Management_Server.test;
//
//import com.pm.Project_Management_Server.dto.ResourceDTO;
//import com.pm.Project_Management_Server.entity.Client;
//import com.pm.Project_Management_Server.entity.Project;
//import com.pm.Project_Management_Server.entity.Resource;
//import com.pm.Project_Management_Server.entity.ResourceLevel;
//import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
//import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
//import com.pm.Project_Management_Server.repositories.ProjectRepository;
//import com.pm.Project_Management_Server.repositories.ResourceRepository;
//import com.pm.Project_Management_Server.services.ResourceServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ResourceServiceTest {
//
//    @Mock private ResourceRepository resourceRepo;
//    @Mock private ProjectRepository projectRepo;
//
//    @InjectMocks private ResourceServiceImpl service;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testAddResource_Success() {
//        Project project = Project.builder().id(1L).build();
//        ResourceDTO dto = new ResourceDTO();
//        dto.setProjectId(1L);
//        dto.setResourceName("Dev");
//        dto.setLevel(ResourceLevel.JR);
//        dto.setStartDate(LocalDate.now());
//        dto.setAllocated(true);
//
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(resourceRepo.save(any())).thenAnswer(inv -> {
//            Resource r = inv.getArgument(0);
//            r.setId(10L);
//            return r;
//        });
//
//        ResourceDTO result = service.addResource(dto);
//        assertNotNull(result.getId());
//        assertEquals(1L, result.getProjectId());
//    }
//
//
//    @Test
//    void testGetAllResources() {
//        Resource r1 = new Resource(); r1.setId(1L); r1.setProject(Project.builder().id(1L).build());
//        Resource r2 = new Resource(); r2.setId(2L); r2.setProject(Project.builder().id(1L).build());
//        when(resourceRepo.findAll()).thenReturn(List.of(r1, r2));
//
//        List<ResourceDTO> result = service.getAllResources();
//        assertEquals(2, result.size());
//    }
//
//
//
//    @Test
//    void testGetResourcesByLevel_Success() {
//        Resource r = new Resource(); r.setLevel(ResourceLevel.JR); r.setProject(Project.builder().id(1L).build());
//        when(resourceRepo.findByLevel(ResourceLevel.JR)).thenReturn(List.of(r));
//
//        List<ResourceDTO> result = service.getResourcesByLevel("jr");
//        assertEquals(1, result.size());
//        assertEquals(ResourceLevel.JR, result.get(0).getLevel());
//    }
//
//    @Test
//    void testGetResourcesByLevel_InvalidLevel() {
//        assertThrows(IllegalArgumentException.class, () -> service.getResourcesByLevel("UNKNOWN"));
//    }
//
//    @Test
//    void testGetAllocatedResources() {
//        Resource r = new Resource(); r.setAllocated(true); r.setProject(Project.builder().id(1L).build());
//        when(resourceRepo.findByAllocated(true)).thenReturn(List.of(r));
//
//        List<ResourceDTO> result = service.getAllocatedResources();
//        assertTrue(result.get(0).isAllocated());
//    }
//
//    @Test
//    void testGetUnallocatedResources() {
//        Resource r = new Resource(); r.setAllocated(false); r.setProject(Project.builder().id(1L).build());
//        when(resourceRepo.findByAllocated(false)).thenReturn(List.of(r));
//
//        List<ResourceDTO> result = service.getUnallocatedResources();
//        assertFalse(result.get(0).isAllocated());
//    }
//
//    @Test
//    void testUpdateResource_Success() {
//        Resource existing = new Resource();
//        existing.setId(1L);
//        existing.setProject(Project.builder().id(1L).build());
//
//        ResourceDTO dto = new ResourceDTO();
//        dto.setId(1L);
//        dto.setResourceName("Dev");
//        dto.setLevel(ResourceLevel.SR);
//        dto.setStartDate(LocalDate.now());
//        dto.setAllocated(false);
//        dto.setProjectId(1L);
//
//        when(resourceRepo.findById(1L)).thenReturn(Optional.of(existing));
//        when(resourceRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        ResourceDTO result = service.updateResource(1L, dto);
//        assertEquals("Dev", result.getResourceName());
//    }
//
//
//    @Test
//    void testUpdateResource_NotFound() {
//        when(resourceRepo.findById(10L)).thenReturn(Optional.empty());
//        assertThrows(RuntimeException.class, () -> service.updateResource(10L, new ResourceDTO()));
//    }
//
//    @Test
//    void testGetResourcesByClientId() {
//        Client client = new Client();
//        client.setId(22L);
//
//        Project project = Project.builder()
//                .id(1L)
//                .client(client)
//                .build();
//
//        Resource r = new Resource();
//        r.setProject(project);
//
//        when(resourceRepo.findByProject_Client_Id(22L)).thenReturn(List.of(r));
//
//        List<ResourceDTO> result = service.getResourcesByClientId(22L);
//        assertEquals(1, result.size());
//    }
//
//
//    @Test
//    void testDeleteResource_Success() {
//        when(resourceRepo.existsById(1L)).thenReturn(true);
//        service.deleteResource(1L);
//        verify(resourceRepo, times(1)).deleteById(1L);
//    }
//
//    @Test
//    void testDeleteResource_NotFound() {
//        when(resourceRepo.existsById(5L)).thenReturn(false);
//        assertThrows(ResourceNotFoundException.class, () -> service.deleteResource(5L));
//    }
//
//    @Test
//    void testAddResource_ProjectNotFound() {
//        ResourceDTO dto = new ResourceDTO(); dto.setProjectId(99L);
//        when(projectRepo.findById(99L)).thenReturn(Optional.empty());
//        assertThrows(ProjectNotFoundException.class, () -> service.addResource(dto));
//    }
//}
