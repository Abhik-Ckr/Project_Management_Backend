package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.Project;

import java.util.List;

public interface ProjectService {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(Long id);

    ProjectDTO createProject(ProjectDTO projectDTO);

    ProjectDTO updateProject(Long id, ProjectDTO updatedProjectDTO);

    void deleteProject(Long id);
    long countProjectsOverBudget();

    ProjectDTO getProjectByLeadId(Long leadId);

    List<ProjectDTO> getProjectsByClient(Long clientId);

    List<ProjectDTO> getProjectsByStatus(Project.Status status);

    List<ProjectDTO> getProjectsOverBudget();

    double calculateBudgetSpent(Project project);

    Double calculateBudgetSpentById(Long id);

    ContactPersonDTO getContactPersonByProjectId(Long projectId);

    // Optional: Keep advanced/relationship methods here as needed
}
