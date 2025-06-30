package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.dto.ResourceRequiredDTO;

import java.util.List;

public interface ResourceRequiredService {

    ResourceRequiredDTO addRequirement(ResourceRequiredDTO dto);

    List<ResourceRequiredDTO> getRequirementsByProject(Long projectId);

    ResourceRequiredDTO updateRequirement(Long id, ResourceRequiredDTO dto);

}
