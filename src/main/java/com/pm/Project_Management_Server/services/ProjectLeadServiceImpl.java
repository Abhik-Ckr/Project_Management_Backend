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
    public void removeProjectLead(Long id) {
        if (!projectLeadRepository.existsById(id)) {
            throw new RuntimeException("Project lead not found");
        }
        projectLeadRepository.deleteById(id);
    }

    @Override
    public ProjectLeadDTO getByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectLead lead = project.getProjectLead();
        if (lead == null) {
            throw new RuntimeException("No lead assigned to this project");
        }

        return toDTO(lead);
    }

    // 🔄 DTO Converter
    private ProjectLeadDTO toDTO(ProjectLead lead) {
        if (lead == null || lead.getUser() == null) {
            throw new IllegalArgumentException("ProjectLead or associated User is null");
        }

        return ProjectLeadDTO.builder()
                .id(lead.getId())
                .userId(lead.getUser().getId())
                .build();
    }

}
