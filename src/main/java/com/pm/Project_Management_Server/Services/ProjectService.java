package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.Repositories.*;
import com.pm.Project_Management_Server.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRateCardRepository projectRateCardRepository;
    private final GlobalRateCardRepository globalRateCardRepository;
    private final ProjectRepository projectRepo;
    private final HighlightRepository highlightRepo;
    private final ResourceRepository resourceRepo;
    private final ProjectRateCardRepository rateCardRepo;
    private final ProjectLeadRepository leadRepo;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }


    public List<Project> getProjectsOverBudget() {
        List<Project> allProjects = projectRepository.findAll();
        List<Project> overBudget = new ArrayList<>();
        for (Project project : allProjects) {
            double spent = calculateBudgetSpent(project);
            if (spent > (project.getBudget() != null ? project.getBudget() : 0)) {
                overBudget.add(project);
            }}
        return overBudget;
    }

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

    private int calculateDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        return (int) ChronoUnit.DAYS.between(start,end);
    }

    private double getRateForResource(Resource resource, Project project) {
        Optional<ProjectRateCard> projectRate = projectRateCardRepository
                .findByProjectIdAndLevel(project.getId(), resource.getLevel());

        if (projectRate.isPresent() && Boolean.TRUE.equals(projectRate.get().getIsActive())) {
            return projectRate.get().getRate();
        }

        // Fallback to GlobalRateCard
        return globalRateCardRepository
                .findByLevel(resource.getLevel())
                .map(GlobalRateCard::getRate)
                .orElse(0.0);}


    public Project updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id).map(project ->{
            project.setProjectName(updatedProject.getProjectName());
            project.setDepartment(updatedProject.getDepartment());
            project.setType(updatedProject.getType());
            project.setStatus(updatedProject.getStatus());
            project.setBudget(updatedProject.getBudget());
            project.setClient(updatedProject.getClient());
            project.setContactPerson(updatedProject.getContactPerson());
            project.setProjectLead(updatedProject.getProjectLead());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsByClient(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    public List<Project> getProjectsByStatus(Project.Status status) {
        return projectRepository.findByStatus(status);
    }




    // ✅ Add Highlight
    public Project addHighlight(Long projectId, Highlight highlight) {
        Project project = getProject(projectId);
        highlight.setProject(project);
        highlightRepo.save(highlight);
        project.getHighlights().add(highlight);
        return projectRepo.save(project);
    }

    // ✅ Delete Highlight
    public Project removeHighlight(Long projectId, Long highlightId) {
        Project project = getProject(projectId);
        Highlight highlight = highlightRepo.findById(highlightId)
                .orElseThrow(() -> new RuntimeException("Highlight not found"));
        if (!highlight.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Highlight does not belong to project");
        }
        project.getHighlights().remove(highlight);
        highlightRepo.delete(highlight);
        return projectRepo.save(project);
    }

    // ✅ Add Resource
    public Project addResource(Long projectId, Resource resource) {
        Project project = getProject(projectId);
        resource.setProject(project);
        resourceRepo.save(resource);
        project.getResources().add(resource);
        return projectRepo.save(project);
    }

    // ✅ Remove Resource
    public Project removeResource(Long projectId, Long resourceId) {
        Project project = getProject(projectId);
        Resource resource = resourceRepo.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        if (!resource.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Resource does not belong to project");
        }
        project.getResources().remove(resource);
        resourceRepo.delete(resource);
        return projectRepo.save(project);
    }

    // ✅ Update Budget
    public Project updateBudget(Long projectId, double newBudget) {
        Project project = getProject(projectId);
        project.setBudget(newBudget);
        return projectRepo.save(project);
    }

    // ✅ Update Status
    public Project updateStatus(Long projectId, Project.Status status) {
        Project project = getProject(projectId);
        project.setStatus(status);
        return projectRepo.save(project);
    }

    // ✅ Update Project Rate Card
    public Project updateProjectRateCard(Long projectId, Long rateCardId) {
        Project project = getProject(projectId);
        ProjectRateCard rateCard = rateCardRepo.findById(rateCardId)
                .orElseThrow(() -> new RuntimeException("RateCard not found"));
        project.setProjectRateCard(rateCard);
        return projectRepo.save(project);
    }

    // ✅ Update Project Lead
    public Project updateProjectLead(Long projectId, Long leadId) {
        Project project = getProject(projectId);
        ProjectLead lead = leadRepo.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        project.setProjectLead(lead);
        return projectRepo.save(project);
    }

    // ✅ Utility method to fetch project
    private Project getProject(Long projectId) {
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}


