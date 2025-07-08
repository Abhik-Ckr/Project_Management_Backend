package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.dto.UserDTO;

import java.util.List;

public interface ProjectLeadService {

    List<ProjectLeadDTO> getAllProjectLeads();

    ProjectLeadDTO getById(Long id);



    void removeProjectLead(Long id);




    UserDTO getUserByProjectId(Long projectId);

    ProjectLeadDTO addProjectLead(ProjectLeadDTO projectLeadDTO);

    List<UserDTO> getAllProjectLeadUsers();
}
