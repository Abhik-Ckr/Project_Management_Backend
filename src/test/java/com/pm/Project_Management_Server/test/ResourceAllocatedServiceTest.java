package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.ResourceAllocatedRepository;
import com.pm.Project_Management_Server.repositories.ResourceRepository;
import com.pm.Project_Management_Server.services.ResourceAllocatedServiceImpl;
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

public class ResourceAllocatedServiceTest {

    @Mock private ProjectRepository projectRepo;
    @Mock private ResourceRepository resourceRepo;
    @Mock private ResourceAllocatedRepository allocatedRepo;

    @InjectMocks private ResourceAllocatedServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllocateResource_Success() {
        Project project = Project.builder().id(1L).build();
        Resource resource = Resource.builder().id(2L).resourceName("Dev").level(ResourceLevel.SR).allocated(false).build();

        ResourceAllocationRequestDTO req = new ResourceAllocationRequestDTO();
        req.setProjectId(1L);
        req.setResourceId(2L);

        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));
        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(allocatedRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = service.allocateResource(req);
        assertEquals("Resource allocated successfully", result);
        assertTrue(resource.isAllocated());
    }

    @Test
    void testAllocateResource_AlreadyAllocated() {
        Resource resource = Resource.builder().id(2L).allocated(true).build();
        ResourceAllocationRequestDTO req = new ResourceAllocationRequestDTO();
        req.setProjectId(1L);
        req.setResourceId(2L);

        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));

        assertThrows(IllegalStateException.class, () -> service.allocateResource(req));
    }

    @Test
    void testAllocateResource_ResourceNotFound() {
        ResourceAllocationRequestDTO req = new ResourceAllocationRequestDTO();
        req.setProjectId(1L);
        req.setResourceId(999L);

        when(resourceRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.allocateResource(req));
    }

    @Test
    void testAllocateResource_ProjectNotFound() {
        Resource resource = Resource.builder().id(2L).allocated(false).build();
        ResourceAllocationRequestDTO req = new ResourceAllocationRequestDTO();
        req.setProjectId(1L);
        req.setResourceId(2L);

        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.allocateResource(req));
    }

    @Test
    void testDeallocateResource_Success() {
        Resource resource = Resource.builder().id(2L).allocated(true).build();
        ResourceAllocated alloc = ResourceAllocated.builder().id(10L).startDate(LocalDate.now()).endDate(null).build();

        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));
        when(allocatedRepo.findTopByResourceIdAndEndDateIsNullOrderByStartDateDesc(2L)).thenReturn(Optional.of(alloc));
        when(allocatedRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(resourceRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = service.deallocateResource(2L);
        assertEquals("Resource deallocated successfully", result);
        assertNotNull(alloc.getEndDate());
        assertFalse(resource.isAllocated());
    }

    @Test
    void testDeallocateResource_NotAllocated() {
        Resource resource = Resource.builder().id(2L).allocated(false).build();
        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));

        assertThrows(IllegalStateException.class, () -> service.deallocateResource(2L));
    }

    @Test
    void testDeallocateResource_ResourceNotFound() {
        when(resourceRepo.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.deallocateResource(404L));
    }

    @Test
    void testDeallocateResource_AllocationMissing() {
        Resource resource = Resource.builder().id(2L).allocated(true).build();
        when(resourceRepo.findById(2L)).thenReturn(Optional.of(resource));
        when(allocatedRepo.findTopByResourceIdAndEndDateIsNullOrderByStartDateDesc(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deallocateResource(2L));
    }

    @Test
    void testGetResourcesByClientId_ReturnsAllocatedResources() {
        Long clientId = 10L;

        Client client = Client.builder().id(clientId).build();
        Project p1 = Project.builder().id(1L).client(client).build();
        Project p2 = Project.builder().id(2L).client(client).build();

//        ResourceAllocated r1 = ResourceAllocated.builder().id(1L).resourceName("R1").project(p1).build();
//        ResourceAllocated r2 = ResourceAllocated.builder().id(2L).resourceName("R2").project(p2).build();

        Resource res1 = Resource.builder().id(100L).build();
        Resource res2 = Resource.builder().id(200L).build();

        ResourceAllocated r1 = ResourceAllocated.builder()
                .id(1L)
                .resource(res1)
                .resourceName("R1")
                .project(p1)
                .build();

        ResourceAllocated r2 = ResourceAllocated.builder()
                .id(2L)
                .resource(res2)
                .resourceName("R2")
                .project(p2)
                .build();


        when(projectRepo.findByClientId(clientId)).thenReturn(List.of(p1, p2));
        when(allocatedRepo.findByProjectIn(List.of(p1, p2))).thenReturn(List.of(r1, r2)); // ✅ FIXED

        var result = service.getResourcesByClientId(clientId);

        assertEquals(2, result.size());
        assertEquals("R1", result.get(0).getResourceName());
        assertEquals("R2", result.get(1).getResourceName());
    }


    @Test
    void testGetResourcesByClientId_NoProjects() {
        when(projectRepo.findByClientId(99L)).thenReturn(List.of());
        var result = service.getResourcesByClientId(99L);
        assertTrue(result.isEmpty());
    }
}
