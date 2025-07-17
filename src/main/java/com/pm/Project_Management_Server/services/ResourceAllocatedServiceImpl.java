package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceAllocatedDTO;
import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.Resource;
import com.pm.Project_Management_Server.entity.ResourceAllocated;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.ResourceAllocatedRepository;
import com.pm.Project_Management_Server.repositories.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceAllocatedServiceImpl implements ResourceAllocatedService{
    private  final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceAllocatedRepository resourceAllocatedRepository;
    @Override
    public String deallocateResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        if (!resource.isAllocated()) {
            throw new IllegalStateException("Resource is not currently allocated.");
        }

        ResourceAllocated allocation = resourceAllocatedRepository
                .findTopByResourceIdAndEndDateIsNullOrderByStartDateDesc(resourceId)
                .orElseThrow(() -> new RuntimeException("No active allocation found for this resource"));

        // Update allocation
        allocation.setEndDate(LocalDate.now());
        resourceAllocatedRepository.save(allocation);

        // Update resource
        resource.setAllocated(false);
        resourceRepository.save(resource);

        return "Resource deallocated successfully";
    }

    @Override
    public String allocateResource(ResourceAllocationRequestDTO request) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException(request.getResourceId()));

        if (resource.isAllocated()) {
            throw new IllegalStateException("Resource is already allocated.");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));

        // Create allocation
        ResourceAllocated allocation = ResourceAllocated.builder()
                .resource(resource)
                .project(project)
                .resourceName(resource.getResourceName())
                .level(resource.getLevel())
                .startDate(LocalDate.now())
                .endDate(null)
                .build();

        // Save and update status
        resourceAllocatedRepository.save(allocation);
        resource.setAllocated(true);
        resourceRepository.save(resource);

        return "Resource allocated successfully";
    }

    @Override
    public List<ResourceAllocatedDTO> getResourcesByClientId(Long clientId) {
        List<Project> projects = projectRepository.findByClientId(clientId);

        return resourceAllocatedRepository.findByProjectIn(projects).stream()
                .map(this::mapToDTO)
                .toList();
    }


    @Override
    public List<ResourceAllocatedDTO> getResourcesByProject(Long projectId) {
        List<ResourceAllocated> allocatedResources = resourceAllocatedRepository.findByProjectId(projectId);

        return allocatedResources.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public ResourceAllocatedDTO mapToDTO(ResourceAllocated allocated) {
        return ResourceAllocatedDTO.builder()
                .id(allocated.getId())
                .resourceName(allocated.getResourceName())
                .level(allocated.getLevel())
                .startDate(allocated.getStartDate())
                .endDate(allocated.getEndDate())
                .projectId(allocated.getProject().getId())
                .resourceId(allocated.getResource().getId())
                .build();
    }
    public ResourceAllocated mapToEntity(ResourceAllocatedDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getResourceId()));

        return ResourceAllocated.builder()
                .id(dto.getId())
                .resourceName(dto.getResourceName() != null ? dto.getResourceName() : resource.getResourceName())
                .level(dto.getLevel() != null ? dto.getLevel() : resource.getLevel())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .project(project)
                .resource(resource)
                .build();
    }

    @Override
    public ResourceAllocatedDTO allocateResource(ResourceAllocatedDTO dto) {
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getResourceId()));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        if (resource.isAllocated()) {
            throw new IllegalStateException("Resource is already allocated");
        }

        // Create new ResourceAllocated
        ResourceAllocated allocated = ResourceAllocated.builder()
                .resource(resource)
                .project(project)
                .resourceName(resource.getResourceName())
                .level(resource.getLevel())
                .startDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now())
                .endDate(dto.getEndDate())
                .build();

        // Mark resource as allocated
        resource.setAllocated(true);
        resourceRepository.save(resource);

        ResourceAllocated saved = resourceAllocatedRepository.save(allocated);
        return mapToDTO(saved);
    }



}
