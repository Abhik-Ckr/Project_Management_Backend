package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectLead;
import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectLeadRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.UserRepository;
import com.pm.Project_Management_Server.services.ProjectLeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectLeadServiceImpl implements ProjectLeadService {

    private final ProjectLeadRepository projectLeadRepo;
    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    @Override
    public ProjectLeadDTO assignLeadToProject(ProjectLeadDTO dto) {
        Project project = projectRepo.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));
        Optional<Users> userOpt = userRepo.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + dto.getUserId());
        }
        Users user = userOpt.get();
        // End previous lead assignment if exists
        Optional<ProjectLead> existingLead = projectLeadRepo.findByProjectAndEndDateIsNull(project);
        existingLead.ifPresent(lead -> {
            lead.setEndDate(LocalDate.now());
            projectLeadRepo.save(lead);
        });

        ProjectLead newLead = new ProjectLead();
        newLead.setName(dto.getName());
        newLead.setEmail(dto.getEmail());
        newLead.setProject(project);
        newLead.setUser(user);
        newLead.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now());
        newLead.setEndDate(null);

        ProjectLead saved = projectLeadRepo.save(newLead);
        return mapToDTO(saved);
    }

    @Override
    public ProjectLeadDTO endLeadAssignment(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        ProjectLead lead = projectLeadRepo.findByProjectAndEndDateIsNull(project)
                .orElseThrow(() -> new RuntimeException("No active lead for this project"));

        lead.setEndDate(LocalDate.now());
        projectLeadRepo.save(lead);
        return mapToDTO(lead);
    }




    @Override
    public ProjectLeadDTO getCurrentLeadForProject(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        ProjectLead lead = projectLeadRepo.findByProjectAndEndDateIsNull(project)
                .orElseThrow(() -> new RuntimeException("No active lead for this project"));

        return mapToDTO(lead);
    }

    @Override
    public List<ProjectLeadDTO> getAllLeads() {
        return projectLeadRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectLeadDTO> getProjectLeadByProjectId(Long projectId) {
        List<ProjectLead> leads = projectLeadRepo.findByProjectId(projectId);
        return leads.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectLeadDTO assignProjectLead(Long userId, Long projectId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        ProjectLead lead = new ProjectLead();
        lead.setName(user.getUserName());
        lead.setUser(user);
        lead.setEmail(user.getEmail());
        lead.setStartDate(LocalDate.now());
        lead.setProject(project);
        lead.setEndDate(null);
        ProjectLead savedLead = projectLeadRepo.save(lead);
        return mapToDTO(savedLead);
    }


    public ProjectLeadDTO mapToDTO(ProjectLead lead) {
        ProjectLeadDTO dto = new ProjectLeadDTO();
        dto.setId(lead.getId());
        dto.setUserId(lead.getUser().getId());
        dto.setName(lead.getUser().getUserName());
        dto.setEmail(lead.getEmail());
        dto.setProjectId(lead.getProject().getId());
        dto.setStartDate(lead.getStartDate());
        dto.setEndDate(lead.getEndDate());
        return dto;
    }




}
