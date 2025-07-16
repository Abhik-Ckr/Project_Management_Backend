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
    private final ProjectLeadRepository leadRepo;
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
                        ResourceRequired::getResourceLevel,
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
                        ResourceRequired::getResourceLevel,
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

    public double estimateCompletionCost(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        List<ResourceAllocated> allAllocations = resourceAllocatedRepo.findByProjectId(projectId);
        List<ProjectRateCard> projectRateCards = projectRateCardRepository.findByProjectId(projectId);
        List<GlobalRateCard> globalRateCards = globalRateCardRepository.findAll();

        LocalDate projectEndDate = project.getEndDate();
        double totalCost = 0.0;
        double workingDayRatio = 235.0 / 365.0;

        for (ResourceAllocated ra : allAllocations) {
            ResourceLevel level = ra.getLevel();
            LocalDate start = ra.getStartDate();
            LocalDate end = ra.getEndDate() != null ? ra.getEndDate() : projectEndDate;

            while (!start.isAfter(end)) {
                LocalDate searchStart = start;  // effectively final
                ProjectRateCard applicableCard = projectRateCards.stream()
                        .filter(card -> card.getLevel() == level &&
                                !card.getStartDate().isAfter(end) &&
                                (card.getEndDate() == null || !card.getEndDate().isBefore(searchStart)))
                        .findFirst()
                        .orElse(null);


                double rate;
                LocalDate rateStart;
                LocalDate rateEnd;

                if (applicableCard != null) {
                    rate = applicableCard.getRate();
                    rateStart = applicableCard.getStartDate();
                    rateEnd = applicableCard.getEndDate() != null ? applicableCard.getEndDate() : end;
                } else {
                    GlobalRateCard globalCard = globalRateCards.stream()
                            .filter(g -> g.getLevel() == level)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("No global rate card for " + level));
                    rate = globalCard.getRate();
                    rateStart = start;
                    rateEnd = end;
                }

                // Calculate overlap between allocation and rate card period
                LocalDate overlapStart = start.isAfter(rateStart) ? start : rateStart;
                LocalDate overlapEnd = end.isBefore(rateEnd) ? end : rateEnd;
                long days = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;

                if (days > 0) {
                    totalCost += days * workingDayRatio * rate;
                    start = overlapEnd.plusDays(1); // move start forward
                } else {
                    break;
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

            if (dto.getProjectLeadId() != null) {
                project.setProjectLead(leadRepo.findById(dto.getProjectLeadId())
                        .orElseThrow(() -> new ProjectLeadNotFoundException(dto.getProjectLeadId())));
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
    public ProjectDTO getProjectByLeadId(Long leadId) {
        return projectRepository.findByProjectLeadId(leadId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ProjectForLeadNotFoundException(leadId));
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
        if (project == null) return 0.0;

        Long projectId = project.getId();
        LocalDate today = LocalDate.now();
        double totalCost = 0.0;

        List<ResourceAllocated> allocations = resourceAllocatedRepo.findByProjectId(projectId);
        List<ProjectRateCard> rateCards = projectRateCardRepository.findByProjectId(projectId);

        for (ResourceAllocated allocation : allocations) {
            ResourceLevel level = allocation.getLevel();
            LocalDate start = allocation.getStartDate();
            LocalDate end = allocation.getEndDate() != null ? allocation.getEndDate() : today;

            if (end.isAfter(today)) {
                end = today; // only till today
            }

            final LocalDate finalEnd = end;

            while (!start.isAfter(finalEnd)) {
                final LocalDate segmentStart = start;

                ProjectRateCard applicableCard = rateCards.stream()
                        .filter(card -> card.getLevel() == level &&
                                !card.getStartDate().isAfter(finalEnd) &&
                                (card.getEndDate() == null || !card.getEndDate().isBefore(segmentStart)))
                        .findFirst()
                        .orElse(null);

                if (applicableCard == null) {
                    break; // No rate card found for this segment
                }

                double rate = applicableCard.getRate();
                LocalDate rateStart = applicableCard.getStartDate().isAfter(start) ? applicableCard.getStartDate() : start;
                LocalDate rateEnd = applicableCard.getEndDate() != null && applicableCard.getEndDate().isBefore(end)
                        ? applicableCard.getEndDate() : end;

                long days = ChronoUnit.DAYS.between(rateStart, rateEnd.plusDays(1));
                double adjustedDays = days * (235.0 / 365.0); // adjusted for 235 working days

                totalCost += adjustedDays * rate;

                start = rateEnd.plusDays(1); // move to next segment
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
                .projectLeadId(project.getProjectLead() != null ? project.getProjectLead().getId() : null)
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
                .projectLead(dto.getProjectLeadId() != null
                        ? leadRepo.findById(dto.getProjectLeadId()).orElse(null)
                        : null)
                .projectRateCard(dto.getProjectRateCardId() != null
                        ? rateCardRepo.findById(dto.getProjectRateCardId()).orElse(null)
                        : null)
                .build();
    }



}
