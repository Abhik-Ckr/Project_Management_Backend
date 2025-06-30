package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.Resource;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ResourceService {

    ResourceDTO addResource(ResourceDTO dto);

    List<ResourceDTO> getResourcesByProject(Long projectId);

    List<ResourceDTO> getResourcesByLevel(String level);

    List<ResourceDTO> getAllocatedResources();

    List<ResourceDTO> getUnallocatedResources();

    ResourceDTO updateResource(Long id, ResourceDTO dto);
}
