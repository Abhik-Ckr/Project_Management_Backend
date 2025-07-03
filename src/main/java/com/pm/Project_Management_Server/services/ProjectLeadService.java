package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;

import java.util.List;

public interface ProjectLeadService {

    List<ProjectLeadDTO> getAllProjectLeads();

    ProjectLeadDTO getById(Long id);



    void removeProjectLead(Long id);

    ProjectLeadDTO getByProjectId(Long projectId);
}
