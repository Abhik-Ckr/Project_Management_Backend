package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.dto.ResourceDeficitDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.exceptions.*;
import com.pm.Project_Management_Server.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private  final ProjectRepository projectRepository;
    private final ProjectRateCardRepository projectRateCardRepository;
    private final GlobalRateCardRepository globalRateCardRepository;
    private final HighlightRepository highlightRepo;
    private final ResourceRepository resourceRepo;
    private final ProjectRateCardRepository rateCardRepo;
    private final ProjectLeadRepository projectLeadRepo;
    private final ClientRepository clientRepo;
    private final ContactPersonRepository contactPersonRepo;
    private final ResourceRequiredRepository resourceRequiredRepo;
    private final ResourceAllocatedRepository resourceAllocatedRepo;

    // ---------- CRUD Operations using DTO ----------


    @Override
    public List<ResourceDeficitDTO> getResourceDeficitReport(Long projectId) {
        // Step 1: Get required resources per level
        List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(projectId);
        Map<ResourceLevel, Integer> requiredMap = requiredList.stream()
                .collect(Collectors.toMap(
                        ResourceRequired::getLevel,
                        ResourceRequired::getQuantity
                ));

        // Step 2: Get currently allocated resources per level for this project
        List<ResourceAllocated> allocatedList = resourceAllocatedRepo
                .findByProjectIdAndEndDateIsNull(projectId);
        Map<ResourceLevel, Long> allocatedMap = allocatedList.stream()
                .collect(Collectors.groupingBy(
                        ResourceAllocated::getLevel,
                        Collectors.counting()
                ));

        // Step 3: Build the deficit report
        Set<ResourceLevel> allLevels = new HashSet<>();
        allLevels.addAll(requiredMap.keySet());
        allLevels.addAll(allocatedMap.keySet());

        List<ResourceDeficitDTO> report = new ArrayList<>();

        for (ResourceLevel level : allLevels) {
            int required = requiredMap.getOrDefault(level, 0);
            int allocated = allocatedMap.getOrDefault(level, 0L).intValue();
            int deficit = required - allocated;

            report.add(ResourceDeficitDTO.builder()
                    .level(level.name())
                    .required(required)
                    .allocated(allocated)
                    .deficit(deficit)  // >0 = need more, <0 = over-allocated
                    .build());
        }

        return report;
    }

    @Override
    public int getTotalResourceDeficitCount(Long projectId) {
        // 1. Fetch required resources for the project
        List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(projectId);
        Map<ResourceLevel, Integer> requiredMap = requiredList.stream()
                .collect(Collectors.toMap(
                        ResourceRequired::getLevel,
                        ResourceRequired::getQuantity
                ));

        // 2. Fetch currently allocated resources (endDate is null)
        List<ResourceAllocated> allocatedList = resourceAllocatedRepo.findByProjectIdAndEndDateIsNull(projectId);
        Map<ResourceLevel, Long> allocatedMap = allocatedList.stream()
                .collect(Collectors.groupingBy(
                        ResourceAllocated::getLevel,
                        Collectors.counting()
                ));

        // 3. Calculate total deficit
        int totalDeficit = 0;
        for (Map.Entry<ResourceLevel, Integer> entry : requiredMap.entrySet()) {
            ResourceLevel level = entry.getKey();
            int required = entry.getValue();
            int allocated = allocatedMap.getOrDefault(level, 0L).intValue();
            int deficit = required - allocated;

            if (deficit > 0) {
                totalDeficit += deficit;
            }
        }

        return totalDeficit;
    }

    @Override
    public int getTotalResourcesRequired(Long projectId) {
        List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(projectId);
        return requiredList.stream()
                .mapToInt(ResourceRequired::getQuantity)
                .sum();
    }

    @Override
    public long countProjectsWithResourceDeficit() {
        List<Project> activeProjects = projectRepository.findByStatus(Project.Status.ACTIVE);
        long deficitCount = 0;

        for (Project project : activeProjects) {
            List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(project.getId());
            List<ResourceAllocated> allocatedList = resourceAllocatedRepo.findByProjectId(project.getId());

            Map<ResourceLevel, Long> requiredMap = requiredList.stream()
                    .collect(Collectors.groupingBy(ResourceRequired::getLevel, Collectors.summingLong(ResourceRequired::getQuantity)));

            Map<ResourceLevel, Long> allocatedMap = allocatedList.stream()
                    .collect(Collectors.groupingBy(ResourceAllocated::getLevel, Collectors.counting()));

            boolean hasDeficit = requiredMap.entrySet().stream()
                    .anyMatch(entry -> allocatedMap.getOrDefault(entry.getKey(), 0L) < entry.getValue());

            if (hasDeficit) {
                deficitCount++;
            }
        }

        return deficitCount;
    }



    @Override
    public double estimateCompletionCost(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        List<ResourceAllocated> allocations = resourceAllocatedRepo.findByProjectId(projectId);
        List<ProjectRateCard> projectRateCards = projectRateCardRepository.findByProjectId(projectId);
        List<GlobalRateCard> globalRateCards = globalRateCardRepository.findAll();

        double totalCost = 0.0;
        double workingDayRatio = 235.0 / 365.0;
        LocalDate projectEndDate = project.getEndDate();

        for (ResourceAllocated ra : allocations) {
            ResourceLevel level = ra.getLevel();
            LocalDate start = ra.getStartDate();
            LocalDate end = ra.getEndDate() != null ? ra.getEndDate() : projectEndDate;

            while (!start.isAfter(end)) {
                LocalDate currentDate = start;

                // 1. Try to find applicable ProjectRateCard
                Optional<ProjectRateCard> projectCardOpt = projectRateCards.stream()
                        .filter(card -> card.getLevel() == level &&
                                !card.getStartDate().isAfter(end) &&
                                (card.getEndDate() == null || !card.getEndDate().isBefore(currentDate))).min(Comparator.comparing(ProjectRateCard::getStartDate));

                double rate;
                LocalDate rateStart;
                LocalDate rateEnd;

                if (projectCardOpt.isPresent()) {
                    ProjectRateCard card = projectCardOpt.get();
                    rate = card.getRate();
                    rateStart = card.getStartDate();
                    rateEnd = card.getEndDate() != null ? card.getEndDate() : end;
                } else {
                    // 2. Fallback to time-bounded GlobalRateCard
                    Optional<GlobalRateCard> globalCardOpt = globalRateCards.stream()
                            .filter(card -> card.getLevel() == level &&
                                    !card.getStartDate().isAfter(end) &&
                                    (card.getEndDate() == null || !card.getEndDate().isBefore(currentDate))).min(Comparator.comparing(GlobalRateCard::getStartDate));

                    GlobalRateCard globalCard = globalCardOpt
                            .orElseThrow(() -> new RuntimeException("No global rate card found for level: " + level));

                    rate = globalCard.getRate();
                    rateStart = globalCard.getStartDate();
                    rateEnd = globalCard.getEndDate() != null ? globalCard.getEndDate() : end;
                }

                // 3. Calculate overlap of allocation and rate period
                LocalDate overlapStart = start.isAfter(rateStart) ? start : rateStart;
                LocalDate overlapEnd = end.isBefore(rateEnd) ? end : rateEnd;

                long days = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;

                if (days > 0) {
                    totalCost += days * workingDayRatio * rate;
                    start = overlapEnd.plusDays(1); // Move to next time segment
                } else {
                    break; // No valid overlap
                }
            }
        }

        return totalCost;
    }






    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }





    @Override
    public List<ProjectDTO> getProjectsOverBudget() {
        return projectRepository.findAll().stream()
                .filter(project ->
                        (project.getStatus() == Project.Status.ACTIVE || project.getStatus() == Project.Status.ON_HOLD)
                                && calculateBudgetSpent(project) > (project.getBudget() != null ? project.getBudget() : 0.0)
                )
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }




    @Override
    public long countProjectsOverBudget() {
        return getProjectsOverBudget().size();
    }


    @Override
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return mapToDTO(project);
    }




    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = mapToEntity(dto);
        return mapToDTO(projectRepository.save(project));
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        return projectRepository.findById(id).map(project -> {

            if (dto.getProjectName() != null) {
                project.setProjectName(dto.getProjectName());
            }

            if (dto.getDepartment() != null) {
                project.setDepartment(dto.getDepartment());
            }

            if (dto.getType() != null) {
                project.setType(dto.getType().name());
            }

            if (dto.getStatus() != null) {
                project.setStatus(Project.Status.valueOf(dto.getStatus()));
            }

            if (dto.getBudget() != null) {
                project.setBudget(dto.getBudget());
            }

            if (dto.getClientId() != null) {
                project.setClient(clientRepo.findById(dto.getClientId())
                        .orElseThrow(() -> new ClientNotFoundException(dto.getClientId())));
            }



            if (dto.getProjectRateCardId() != null) {
                project.setProjectRateCard(rateCardRepo.findById(dto.getProjectRateCardId())
                        .orElseThrow(() -> new RateCardNotFoundException(dto.getProjectRateCardId())));
            }

            Project updated = projectRepository.save(project);
            return mapToDTO(updated);

        }).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }



    @Override
    public List<ProjectDTO> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getProjectsByStatus(Project.Status status) {
        return projectRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateBudgetSpent(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        LocalDate today = LocalDate.now();
        LocalDate projectStart = project.getStartDate();
        LocalDate projectEnd = project.getEndDate() != null ? project.getEndDate() : today;

        if (projectStart == null) {
            throw new IllegalArgumentException("Project start date is required");
        }

        // Fetch all allocations for the project
        List<ResourceAllocated> allocations = resourceAllocatedRepo.findByProjectId(project.getId());

        // Fetch rate cards (project-specific and global)
        List<ProjectRateCard> projectCards = projectRateCardRepository.findByProjectId(project.getId());
        List<GlobalRateCard> globalCards = globalRateCardRepository.findAll(); // or filter active only

        double totalCost = 0.0;

        for (ResourceAllocated allocation : allocations) {
            ResourceLevel level = allocation.getLevel();
            LocalDate start = allocation.getStartDate();
            LocalDate end = allocation.getEndDate() != null ? allocation.getEndDate() : today;

            // Skip if allocation is fully beyond today
            if (start.isAfter(today)) continue;

            // Cap end date at today
            if (end.isAfter(today)) end = today;

            while (!start.isAfter(end)) {
                final LocalDate segmentStart = start;

                // 1. Find applicable project rate card for the level and date
                ProjectRateCard card = projectCards.stream()
                        .filter(c -> c.getLevel() == level &&
                                !c.getStartDate().isAfter(segmentStart) &&
                                (c.getEndDate() == null || !c.getEndDate().isBefore(segmentStart)))
                        .findFirst()
                        .orElse(null);

                double rate;
                LocalDate rateStart, rateEnd;

                if (card != null) {
                    rate = card.getRate();
                    rateStart = segmentStart;
                    rateEnd = card.getEndDate() != null ? card.getEndDate() : end;
                } else {
                    // 2. Fallback to global rate card
                    GlobalRateCard globalCard = globalCards.stream()
                            .filter(g -> g.getLevel() == level &&
                                    !g.getStartDate().isAfter(segmentStart) &&
                                    (g.getEndDate() == null || !g.getEndDate().isBefore(segmentStart)))
                            .findFirst()
                            .orElse(null);

                    if (globalCard == null) {
                        // No applicable rate found, skip segment
                        start = start.plusDays(1);
                        continue;
                    }

                    rate = globalCard.getRate();
                    rateStart = segmentStart;
                    rateEnd = globalCard.getEndDate() != null ? globalCard.getEndDate() : end;
                }

                // Determine the duration for which the rate is applied
                LocalDate segmentEnd = rateEnd.isBefore(end) ? rateEnd : end;

                long days = ChronoUnit.DAYS.between(segmentStart, segmentEnd.plusDays(1)); // inclusive
                double adjustedDays = (days * 235.0) / 365.0;

                totalCost += adjustedDays * rate;

                // Move start to next day after this segment
                start = segmentEnd.plusDays(1);
            }
        }

        return totalCost;
    }

    @Override
    public Double calculateBudgetSpentById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        return calculateBudgetSpent(project);
    }






    private ProjectDTO mapToDTO(Project project) {
        if (project == null) return null;

        return ProjectDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .type(Project.ProjectType.valueOf(project.getType())) // ProjectType enum
                .department(project.getDepartment())
                .status(project.getStatus() != null ? project.getStatus().name() : null)
                .budget(project.getBudget())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .clientId(project.getClient() != null ? project.getClient().getId() : null)
                .projectRateCardId(project.getProjectRateCard() != null ? project.getProjectRateCard().getId() : null)
                .build();
    }


    @Override
    public ContactPersonDTO getContactPersonByProjectId(Long projectId) {
        ContactPerson person = contactPersonRepo.findByProjectId(projectId)
                .orElseThrow(() -> new ContactPersonNotFoundException(projectId));
        return new ContactPersonDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),person.getPhone(),
                person.getProject().getId()
        );
    }

    @Override
    @Transactional
    public void updateProjectStatus(Long projectId, Project.Status newStatus) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        project.setStatus(newStatus);

        if (newStatus == Project.Status.COMPLETED) {
            LocalDate today = LocalDate.now();

            // Step 1: Release all allocated resources
            List<ResourceAllocated> allocations = resourceAllocatedRepo.findByProjectId(projectId);
            for (ResourceAllocated allocation : allocations) {
                if (allocation.getEndDate() == null) {
                    allocation.setEndDate(today);
                    resourceAllocatedRepo.save(allocation);
                }

                // Also set the Resource's allocated flag to false
                Resource resource = allocation.getResource();
                if (resource != null && resource.isAllocated()) {
                    resource.setAllocated(false);
                    resourceRepo.save(resource);
                }
            }

            // Step 2: End the Project Lead assignment
            // Step 2: End the Project Lead assignment
            List<ProjectLead> leads = projectLeadRepo.findByProjectId(projectId);

            leads.stream()
                    .filter(lead -> lead.getEndDate() == null) // active one
                    .findFirst()
                    .ifPresent(lead -> {
                        lead.setEndDate(today);
                        projectLeadRepo.save(lead);
                    });

        }

        Project updated = projectRepository.save(project);
        mapToDTO(updated);
    }

    @Override
    public ProjectDTO getProjectByLeadId(Long leadId) {
        ProjectLead projectLead = projectLeadRepo.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Project Lead not found with id: " + leadId));

        Project project = projectLead.getProject();

        return mapToDTO(project);
    }



    private Project mapToEntity(ProjectDTO dto) {
        return Project.builder()
                .id(dto.getId())
                .projectName(dto.getProjectName())
                .type(dto.getType() != null ? dto.getType().name() : null)  // Convert enum to String
                .department(dto.getDepartment())
                .status(dto.getStatus() != null
                        ? Project.Status.valueOf(dto.getStatus())
                        : Project.Status.ACTIVE)
                .budget(dto.getBudget())
                .startDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now())
                .endDate(dto.getEndDate())
                .client(dto.getClientId() != null
                        ? clientRepo.findById(dto.getClientId()).orElse(null)
                        : null)
                .projectRateCard(dto.getProjectRateCardId() != null
                        ? rateCardRepo.findById(dto.getProjectRateCardId()).orElse(null)
                        : null)
                .build();
    }



}
