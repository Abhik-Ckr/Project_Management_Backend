package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;

public interface ResourceAllocatedService {
    String deallocateResource(Long resourceId);

    String allocateResource(ResourceAllocationRequestDTO request);


}
