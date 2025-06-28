package com.pm.Project_Management_Server.Services;



import com.pm.Project_Management_Server.Repositories.ProjectLeadRepository;
import com.pm.Project_Management_Server.Repositories.ProjectRepository;
import com.pm.Project_Management_Server.Repositories.UserRepository;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectLead;
import com.pm.Project_Management_Server.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectLeadService {

    private final ProjectLeadRepository projectLeadRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<ProjectLead> getAllProjectLeads() {
        return projectLeadRepository.findAll();
    }

    public Optional<ProjectLead> getById(Long id) {
        return projectLeadRepository.findById(id);
    }

    public ProjectLead assignLeadToProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectLead projectLead = ProjectLead.builder()
                .user(user)
                .project(project)
                .build();

        return projectLeadRepository.save(projectLead);
    }

    public void removeProjectLead(Long id) {
        projectLeadRepository.deleteById(id);
    }

    public Optional<ProjectLead> getByProjectId(Long projectId) {
        return projectLeadRepository.findByProjectId(projectId);
    }
}

