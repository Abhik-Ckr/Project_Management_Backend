package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceAllocatedDTO;
import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.dto.ResourceDTO;

import java.util.List;

import java.util.List;

public interface ResourceAllocatedService {
    String deallocateResource(Long resourceId);

    String allocateResource(ResourceAllocationRequestDTO request);

    List<ResourceAllocatedDTO> getResourcesByClientId(Long clientId);

    List<ResourceAllocatedDTO> getResourcesByProject(Long projectId);
}
