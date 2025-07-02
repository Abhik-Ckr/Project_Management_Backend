package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectLead;
import com.pm.Project_Management_Server.entity.User;
import com.pm.Project_Management_Server.repositories.ProjectLeadRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.UserRepository;
import com.pm.Project_Management_Server.services.ProjectLeadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectLeadServiceImpl implements ProjectLeadService {

    private final ProjectLeadRepository projectLeadRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProjectLeadDTO> getAllProjectLeads() {
        return projectLeadRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectLeadDTO getById(Long id) {
        ProjectLead lead = projectLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project lead not found"));
        return toDTO(lead);
    }

    @Override
    public ProjectLeadDTO assignLeadToProject(ProjectLeadDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectLead projectLead = ProjectLead.builder()
                .user(user)
                .project(project)
                .build();

        ProjectLead savedLead = projectLeadRepository.save(projectLead);
        return toDTO(savedLead);
    }

    @Override
    public void removeProjectLead(Long id) {
        if (!projectLeadRepository.existsById(id)) {
            throw new RuntimeException("Project lead not found");
        }
        projectLeadRepository.deleteById(id);
    }

    @Override
    public ProjectLeadDTO getByProjectId(Long projectId) {
        ProjectLead lead = projectLeadRepository.findByProject_Id(projectId)
                .orElseThrow(() -> new RuntimeException("Lead not assigned for project"));
        return toDTO(lead);
    }

    // 🔄 DTO Converter
    private ProjectLeadDTO toDTO(ProjectLead lead) {
        ProjectLeadDTO dto = new ProjectLeadDTO();
        dto.setId(lead.getId());
        dto.setUserId(lead.getUser().getId());
        dto.setProjectId(lead.getProject().getId());
        return dto;
    }
}
