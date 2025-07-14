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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    // ---------- CRUD Operations using DTO ----------

    @Override
    public List<ResourceDeficitDTO> getResourceDeficitReport(Long projectId) {
        // 1. Fetch required resources
        List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(projectId);

        // 2. Fetch allocated resources
        List<Resource> allocatedResources = resourceRepo.findByProjectId(projectId);

        // 3. Count actual allocations per level
        Map<ResourceLevel, Long> actualMap = allocatedResources.stream()
                .collect(Collectors.groupingBy(Resource::getLevel, Collectors.counting()));

        List<ResourceDeficitDTO> report = new ArrayList<>();

        for (ResourceRequired req : requiredList) {
            ResourceLevel level = req.getResourceLevel();
            int requiredQty = req.getQuantity();
            long actualQty = actualMap.getOrDefault(level, 0L);

            if (actualQty < requiredQty) {
                report.add(new ResourceDeficitDTO(level, "DEFICIT", requiredQty - (int) actualQty));
            } else if (actualQty > requiredQty) {
                report.add(new ResourceDeficitDTO(level, "EXCESS", (int) actualQty - requiredQty));
            }
        }

        return report;
    }

    @Override
    public int getTotalResourceDeficitCount(Long projectId) {
        List<ResourceRequired> requiredList = resourceRequiredRepo.findByProjectId(projectId);
        List<Resource> allocatedResources = resourceRepo.findByProjectId(projectId);

        Map<ResourceLevel, Long> actualMap = allocatedResources.stream()
                .collect(Collectors.groupingBy(Resource::getLevel, Collectors.counting()));

        int totalDeficit = 0;

        for (ResourceRequired req : requiredList) {
            int requiredQty = req.getQuantity();
            long actualQty = actualMap.getOrDefault(req.getResourceLevel(), 0L);

            if (actualQty < requiredQty) {
                totalDeficit += (int) (requiredQty - actualQty);
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
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Resource> resources = resourceRepo.findByProjectId(projectId);
        List<ProjectRateCard> rateCards = projectRateCardRepository.findByProjectId(projectId);

        LocalDate projectEnd = project.getEndDate();
        double totalCost = 0.0;

        for (Resource resource : resources) {
            LocalDate resStart = resource.getStartDate();
            LocalDate resEnd = resource.getEndDate() != null ? resource.getEndDate() : projectEnd;

            // Fetch rate cards for the resource's level and overlapping dates
            List<ProjectRateCard> applicableCards = rateCards.stream()
                    .filter(card -> card.getLevel() == resource.getLevel())
                    .filter(card -> !(card.getEndDate().isBefore(resStart) || card.getStartDate().isAfter(resEnd)))
                    .toList();

            for (ProjectRateCard card : applicableCards) {
                LocalDate overlapStart = resStart.isAfter(card.getStartDate()) ? resStart : card.getStartDate();
                LocalDate overlapEnd = resEnd.isBefore(card.getEndDate()) ? resEnd : card.getEndDate();

                long workingDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd.plusDays(1));
                if (workingDays > 0) {
                    totalCost += workingDays * card.getRate(); // No division, rate is already per day
                }
            }
        }

        return Math.round(totalCost * 100.0) / 100.0;
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
    public Double calculateBudgetSpentById(Project project) {
        return 0.0;
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
                project.setType(dto.getType());
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
        Long projectId=project.getId();
        List<Resource> resources = resourceRepo.findByProjectId(projectId);
        List<ProjectRateCard> rates = projectRateCardRepository.findByProjectId(projectId);

        double total = 0;
        double billingRatio = 235.0 / 365.0;

        for (Resource resource : resources) {
            for (ProjectRateCard rate : rates) {
                if (rate.getLevel() == resource.getLevel()
                        && !resource.getEndDate().isBefore(rate.getStartDate())
                        && !resource.getStartDate().isAfter(rate.getEndDate())) {

                    // Find overlapping duration
                    LocalDate overlapStart = resource.getStartDate().isAfter(rate.getStartDate()) ? resource.getStartDate() : rate.getStartDate();
                    LocalDate overlapEnd = resource.getEndDate().isBefore(rate.getEndDate()) ? resource.getEndDate() : rate.getEndDate();

                    long actualDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
                    double effectiveBillingDays = actualDays * billingRatio;

                    total += effectiveBillingDays * rate.getRate();
                }
            }
        }
        return total;
    }
    @Override
    public Double calculateBudgetSpentById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        return calculateBudgetSpent(project);
    }


    private Project getProjectEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }



    private ProjectDTO mapToDTO(Project project) {
        if (project == null) return null;

        return ProjectDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .type(project.getType()) // ProjectType enum
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
        if (dto == null) return null;

        Project project = new Project();
        project.setId(dto.getId());
        project.setProjectName(dto.getProjectName());
        project.setType(dto.getType()); // ProjectType enum
        project.setDepartment(dto.getDepartment());

        if (dto.getStatus() != null) {
            project.setStatus(Project.Status.valueOf(dto.getStatus()));
        }

        project.setBudget(dto.getBudget());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());

        if (dto.getClientId() != null) {
            Client client = new Client();
            client.setId(dto.getClientId());
            project.setClient(client);
        }

        if (dto.getProjectLeadId() != null) {
            ProjectLead lead = new ProjectLead();
            lead.setId(dto.getProjectLeadId());
            project.setProjectLead(lead);
        }

        if (dto.getProjectRateCardId() != null) {
            ProjectRateCard rateCard = new ProjectRateCard();
            rateCard.setId(dto.getProjectRateCardId());
            project.setProjectRateCard(rateCard);
        }

        return project;
    }


}
