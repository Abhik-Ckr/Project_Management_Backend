package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    List<ProjectDTO> getAllProjects();

    Optional<ProjectDTO> getProjectById(Long id);

    ProjectDTO createProject(ProjectDTO projectDTO);

    ProjectDTO updateProject(Long id, ProjectDTO updatedProjectDTO);

    void deleteProject(Long id);
    long countProjectsOverBudget();
    List<ProjectDTO> getProjectsByClient(Long clientId);

    List<ProjectDTO> getProjectsByStatus(Project.Status status);

    List<ProjectDTO> getProjectsOverBudget();

    double calculateBudgetSpent(Project project);

    // Optional: Keep advanced/relationship methods here as needed
}
