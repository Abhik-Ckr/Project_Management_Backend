package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceDTO;

import java.util.List;

public interface ResourceService {

    ResourceDTO addResource(ResourceDTO dto);
    List<ResourceDTO> getAllResources();
    List<ResourceDTO> getResourcesByProject(Long projectId);

    List<ResourceDTO> getResourcesByLevel(String level);

    List<ResourceDTO> getAllocatedResources();

    List<ResourceDTO> getUnallocatedResources();

    ResourceDTO updateResource(Long id, ResourceDTO dto);
}
