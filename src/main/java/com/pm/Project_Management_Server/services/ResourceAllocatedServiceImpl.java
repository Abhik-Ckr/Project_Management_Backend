package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceAllocatedDTO;
import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
import com.pm.Project_Management_Server.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceAllocatedServiceImpl implements ResourceAllocatedService{
    private  final ProjectRepository projectRepository;
    private final ProjectRateCardRepository projectRateCardRepo;
    private final ResourceRepository resourceRepository;
    private final GlobalRateCardRepository globalRateCardRepo;
    private final ResourceAllocatedRepository resourceAllocatedRepository;

    @Override
    public double calculateBudgetById(Long allocationId) {
        ResourceAllocated allocation = resourceAllocatedRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Resource allocation not found"));

        LocalDate start = allocation.getStartDate();
        LocalDate end = allocation.getEndDate() != null ? allocation.getEndDate() : LocalDate.now();
        long daysWorked = ChronoUnit.DAYS.between(start, end) + 1;

        Project project = allocation.getProject();
        ResourceLevel level = allocation.getResource().getLevel();

        Optional<ProjectRateCard> projectRateCardOpt =
                projectRateCardRepo.findFirstByProjectIdAndLevelAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndActiveTrue(
                        project.getId(), level, start, end
                );

        double rate;

        if (projectRateCardOpt.isPresent()) {
            rate = projectRateCardOpt.get().getRate();
        } else {
            Optional<GlobalRateCard> globalRateCardOpt =
                    globalRateCardRepo.findFirstByLevelAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            level, start, end
                    );

            if (globalRateCardOpt.isEmpty()) {
                throw new RuntimeException("No valid rate card found for level: " + level);
            }

            rate = globalRateCardOpt.get().getRate();
        }
        // Apply 235/365 ratio as per rule
        double perDayRate = (rate * 235.0) / 365.0;
        return daysWorked * perDayRate;
    }

    @Override
    public void deallocateResource(Long allocationId) {
        ResourceAllocated allocation = resourceAllocatedRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Resource allocation not found"));

        // Set endDate to today if not already set
        if (allocation.getEndDate() == null) {
            allocation.setEndDate(LocalDate.now());
        }

        // Update resource's allocated status
        Resource resource = allocation.getResource();
        resource.setAllocated(false);

        // Save both updates
        resourceRepository.save(resource);
        resourceAllocatedRepository.save(allocation);
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
