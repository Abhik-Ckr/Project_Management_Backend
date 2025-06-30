package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRateCardRepository projectRateCardRepository;
    private final GlobalRateCardRepository globalRateCardRepository;
    private final HighlightRepository highlightRepo;
    private final ResourceRepository resourceRepo;
    private final ProjectRateCardRepository rateCardRepo;
    private final ProjectLeadRepository leadRepo;
    private final ClientRepository clientRepo;
    private final ContactPersonRepository contactPersonRepo;

    // ---------- CRUD Operations using DTO ----------

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProjectDTO> getProjectById(Long id) {
        return projectRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    public ProjectDTO createProject(ProjectDTO dto) {
        Project project = mapToEntity(dto);
        return mapToDTO(projectRepository.save(project));
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO dto) {
        return projectRepository.findById(id).map(project -> {
            project.setProjectName(dto.getProjectName());
            project.setDepartment(dto.getDepartment());
            project.setType(dto.getType());
            project.setStatus(Project.Status.valueOf(dto.getStatus()));
            project.setBudget(dto.getBudgets() != null ? dto.getBudgets().doubleValue() : null);

            if (dto.getClientId() != null) {
                project.setClient(clientRepo.findById(dto.getClientId())
                        .orElseThrow(() -> new RuntimeException("Client not found")));
            }

            if (dto.getContactPersonId() != null) {
                project.setContactPerson(contactPersonRepo.findById(dto.getContactPersonId())
                        .orElseThrow(() -> new RuntimeException("Contact person not found")));
            }

            if (dto.getProjectLeadId() != null) {
                project.setProjectLead(leadRepo.findById(dto.getProjectLeadId())
                        .orElseThrow(() -> new RuntimeException("Project lead not found")));
            }

            if (dto.getProjectRateCardId() != null) {
                project.setProjectRateCard(rateCardRepo.findById(dto.getProjectRateCardId())
                        .orElseThrow(() -> new RuntimeException("Rate card not found")));
            }

            return mapToDTO(projectRepository.save(project));
        }).orElseThrow(() -> new RuntimeException("Project not found"));
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
    public List<ProjectDTO> getProjectsOverBudget() {
        List<ProjectDTO> overBudget = new ArrayList<>();
        for (Project project : projectRepository.findAll()) {
            double spent = calculateBudgetSpent(project);
            double budget = project.getBudget() != null ? project.getBudget() : 0;
            if (spent > budget) {
                overBudget.add(mapToDTO(project));
            }
        }
        return overBudget;
    }
    @Override
    public long countProjectsOverBudget() {
        return getProjectsOverBudget().size();
    }

    @Override
    public double calculateBudgetSpent(Project project) {
        double totalCost = 0.0;
        List<Resource> resources = project.getResources();
        if (resources == null) return 0.0;
        for (Resource resource : resources) {
            int daysWorked = calculateDaysBetween(resource.getStartDate(), resource.getEndDate());
            double rate = getRateForResource(resource, project);
            totalCost += rate * daysWorked;
        }
        return totalCost;
    }

    // ---------- Helper Methods ----------

    private int calculateDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    private double getRateForResource(Resource resource, Project project) {
        return projectRateCardRepository
                .findByProjectIdAndLevel(project.getId(), resource.getLevel())
                .filter(ProjectRateCard::getIsActive)
                .map(ProjectRateCard::getRate)
                .orElseGet(() -> globalRateCardRepository
                        .findByLevel(resource.getLevel())
                        .map(GlobalRateCard::getRate)
                        .orElse(0.0));
    }

    private Project getProjectEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setType(project.getType());
        dto.setDepartment(project.getDepartment());
        dto.setStatus(project.getStatus() != null ? project.getStatus().name() : null);
        dto.setClientId(project.getClient() != null ? project.getClient().getId() : null);
        dto.setContactPersonId(project.getContactPerson() != null ? project.getContactPerson().getId() : null);
        dto.setProjectLeadId(project.getProjectLead() != null ? project.getProjectLead().getId() : null);
        dto.setProjectRateCardId(project.getProjectRateCard() != null ? project.getProjectRateCard().getId() : null);
        dto.setBudgets(project.getBudget() != null ? BigDecimal.valueOf(project.getBudget()) : null);
        dto.setResourceIds(project.getResources() != null
                ? project.getResources().stream().map(Resource::getId).collect(Collectors.toList())
                : List.of());
        dto.setHighlightIds(project.getHighlights() != null
                ? project.getHighlights().stream().map(Highlight::getId).collect(Collectors.toList())
                : List.of());
        return dto;
    }

    private Project mapToEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setType(dto.getType());
        project.setDepartment(dto.getDepartment());

        if (dto.getStatus() != null) {
            project.setStatus(Project.Status.valueOf(dto.getStatus()));
        }

        if (dto.getBudgets() != null) {
            project.setBudget(dto.getBudgets().doubleValue());
        }

        if (dto.getClientId() != null) {
            project.setClient(clientRepo.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found")));
        }

        if (dto.getContactPersonId() != null) {
            project.setContactPerson(contactPersonRepo.findById(dto.getContactPersonId())
                    .orElseThrow(() -> new RuntimeException("Contact person not found")));
        }

        if (dto.getProjectRateCardId() != null) {
            project.setProjectRateCard(rateCardRepo.findById(dto.getProjectRateCardId())
                    .orElseThrow(() -> new RuntimeException("Rate card not found")));
        }

        if (dto.getProjectLeadId() != null) {
            project.setProjectLead(leadRepo.findById(dto.getProjectLeadId())
                    .orElseThrow(() -> new RuntimeException("Lead not found")));
        }

        return project;
    }
}
