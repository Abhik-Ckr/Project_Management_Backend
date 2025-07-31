package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceAllocatedDTO;
import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
import com.pm.Project_Management_Server.repositories.*;
import java.time.DayOfWeek;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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

        // Calculate actual working days (weekdays only)
        long workingDays = start.datesUntil(end.plusDays(1))
                .filter(date -> {
                    DayOfWeek day = date.getDayOfWeek();
                    return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
                }).count();

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

        // Do NOT average using 235/365 — just multiply actual working days with rate
        return workingDays * rate;
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

    @Override
    public double calculateEstimatedCostPerResource(Long allocationId) {
        ResourceAllocated allocation = resourceAllocatedRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid allocation ID"));

        Project project = allocation.getProject();
        List<ProjectRateCard> projectRateCards = projectRateCardRepo.findByProjectId(project.getId());
        List<GlobalRateCard> globalRateCards = globalRateCardRepo.findAll();

        ResourceLevel level = allocation.getLevel();
        LocalDate start = allocation.getStartDate();
        LocalDate end = allocation.getEndDate() != null ? allocation.getEndDate() : project.getEndDate();

        double totalCost = 0.0;
        double workingDayRatio = 235.0 / 365.0;

        while (!start.isAfter(end)) {
            LocalDate currentDate = start;

            // Step 1: Try to get matching project rate card
            Optional<ProjectRateCard> projectCardOpt = projectRateCards.stream()
                    .filter(card -> card.getLevel().equals(level) &&
                            !card.getStartDate().isAfter(currentDate) &&
                            (card.getEndDate() == null || !card.getEndDate().isBefore(currentDate)))
                    .min(Comparator.comparing(ProjectRateCard::getStartDate));

            double rate;
            LocalDate rateStart;
            LocalDate rateEnd;

            if (projectCardOpt.isPresent()) {
                ProjectRateCard card = projectCardOpt.get();
                rate = card.getRate();
                rateStart = card.getStartDate();
                rateEnd = card.getEndDate() != null ? card.getEndDate() : end;
            } else {
                // Step 2: Fallback to global rate card
                Optional<GlobalRateCard> globalCardOpt = globalRateCards.stream()
                        .filter(card -> card.getLevel().equals(level) &&
                                !card.getStartDate().isAfter(currentDate) &&
                                (card.getEndDate() == null || !card.getEndDate().isBefore(currentDate)))
                        .min(Comparator.comparing(GlobalRateCard::getStartDate));

                if (!globalCardOpt.isPresent()) {
                    throw new RuntimeException("No rate card found for level: " + level + " on " + currentDate);
                }

                GlobalRateCard globalCard = globalCardOpt.get();
                rate = globalCard.getRate();
                rateStart = globalCard.getStartDate();
                rateEnd = globalCard.getEndDate() != null ? globalCard.getEndDate() : end;
            }

            // Step 3: Compute overlap of allocation period and rate card period
            LocalDate overlapStart = start.isAfter(rateStart) ? start : rateStart;
            LocalDate overlapEnd = end.isBefore(rateEnd) ? end : rateEnd;

            long days = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;

            if (days > 0) {
                totalCost += days * workingDayRatio * rate;
                start = overlapEnd.plusDays(1); // advance start to next rate period
            } else {
                break; // no overlap
            }
        }

        return totalCost;
    }

}
