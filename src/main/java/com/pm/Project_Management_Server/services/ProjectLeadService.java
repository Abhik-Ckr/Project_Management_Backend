package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.entity.ProjectLead;

import java.util.List;

public interface ProjectLeadService {
    ProjectLeadDTO assignLeadToProject(ProjectLeadDTO dto);
    ProjectLeadDTO endLeadAssignment(Long projectId);
    ProjectLeadDTO getCurrentLeadForProject(Long projectId);
    List<ProjectLeadDTO> getAllLeads();

    List<ProjectLeadDTO> getProjectLeadByProjectId(Long projectId);

    ProjectLeadDTO assignProjectLead(Long userId, Long projectId);
}
