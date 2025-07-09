package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.*;
import com.pm.Project_Management_Server.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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

    // ---------- CRUD Operations using DTO ----------

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public double calculateBudgetSpent(Project project) {
        List<Resource> resources = resourceRepo.findByProjectId(project.getId());
        double totalSpent = 0.0;

        for (Resource resource : resources) {
            if (resource.getStartDate() != null) {
                // Use endDate if available, otherwise use today's date
                LocalDate endDate = (resource.getEndDate() != null)
                        ? resource.getEndDate()
                        : LocalDate.now();

                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        resource.getStartDate(), endDate);

                double rate = projectRateCardRepository
                        .findByProjectIdAndLevel(project.getId(), resource.getLevel())
                        .filter(ProjectRateCard::getActive)
                        .map(ProjectRateCard::getRate)
                        .orElseGet(() -> globalRateCardRepository
                                .findByLevel(resource.getLevel())
                                .map(GlobalRateCard::getRate)
                                .orElse(0.0));

                totalSpent += days * rate;
            }
        }

        return totalSpent;
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
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
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
            project.setProjectName(dto.getProjectName());
            project.setDepartment(dto.getDepartment());
            project.setType(dto.getType());
            project.setStatus(Project.Status.valueOf(dto.getStatus()));
            project.setBudget(dto.getBudget() != null ? dto.getBudget() : null);
            if (dto.getClientId() != null) {
                project.setClient(clientRepo.findById(dto.getClientId())
                        .orElseThrow(() -> new RuntimeException("Client not found")));
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
    public ProjectDTO getProjectByLeadId(Long leadId) {
        return projectRepository.findByProjectLeadId(leadId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("No project found for this lead"));
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

    private Project getProjectEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
    @Override
    public Double calculateBudgetSpentById(Long projectId) {
        Project project = getProjectEntity(projectId);
        return calculateBudgetSpent(project);
    }


    private ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setProjectName(project.getProjectName());
        dto.setType(project.getType());
        dto.setDepartment(project.getDepartment());
        dto.setStatus(project.getStatus() != null ? project.getStatus().name() : null);
        dto.setBudget(project.getBudget());
        dto.setClientId(project.getClient() != null ? project.getClient().getId() : null);
        dto.setProjectLeadId(project.getProjectLead() != null ? project.getProjectLead().getId() : null);
        dto.setProjectRateCardId(project.getProjectRateCard() != null ? project.getProjectRateCard().getId() : null);
        return dto;
    }

    @Override
    public ContactPersonDTO getContactPersonByProjectId(Long projectId) {
        ContactPerson person = contactPersonRepo.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("No contact person found for project ID: " + projectId));
        return new ContactPersonDTO(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getProject().getId()
        );
    }

    private Project mapToEntity(ProjectDTO dto) {
        Project.ProjectBuilder builder = Project.builder()
                .id(dto.getId())
                .projectName(dto.getProjectName())
                .type(dto.getType())
                .department(dto.getDepartment())
                .budget(dto.getBudget());

        if (dto.getStatus() != null) {
            builder.status(Project.Status.valueOf(dto.getStatus()));
        }

        if (dto.getClientId() != null) {
            Client client = clientRepo.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            builder.client(client);
        }

        if (dto.getProjectLeadId() != null) {
            ProjectLead projectLead = leadRepo.findById(dto.getProjectLeadId())
                    .orElseThrow(() -> new RuntimeException("Project Lead not found"));
            builder.projectLead(projectLead);
        }

        if (dto.getProjectRateCardId() != null) {
            ProjectRateCard rateCard = projectRateCardRepository.findById(dto.getProjectRateCardId())
                    .orElseThrow(() -> new RuntimeException("Project Rate Card not found"));
            builder.projectRateCard(rateCard);
        }

        return builder.build();
    }

}
