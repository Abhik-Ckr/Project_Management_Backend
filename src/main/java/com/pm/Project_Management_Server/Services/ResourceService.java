package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.dto.ResourceDTO;

import java.util.List;

public interface ResourceService {

    ResourceDTO addResource(ResourceDTO dto);

    List<ResourceDTO> getResourcesByProject(Long projectId);

    List<ResourceDTO> getResourcesByLevel(String level);

    List<ResourceDTO> getAllocatedResources();

    List<ResourceDTO> getUnallocatedResources();

    ResourceDTO updateResource(Long id, ResourceDTO dto);
}
