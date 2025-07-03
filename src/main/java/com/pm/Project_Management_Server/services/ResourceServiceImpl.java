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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ResourceDTO addResource(ResourceDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Resource resource = new Resource();
        BeanUtils.copyProperties(dto, resource);
        resource.setProject(project);
        resource.setAllocated(true);

        Resource saved = resourceRepository.save(resource);

        ResourceDTO response = new ResourceDTO();
        BeanUtils.copyProperties(saved, response);
        response.setProjectId(project.getId());
        return response;
    }

    @Override
    public List<ResourceDTO> getResourcesByProject(Long projectId) {
        return resourceRepository.findByProjectId(projectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceDTO> getResourcesByLevel(String level) {
        ResourceLevel resourceLevel = ResourceLevel.valueOf(level.toUpperCase());
        return resourceRepository.findByLevel(resourceLevel)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceDTO> getAllocatedResources() {
        return resourceRepository.findByAllocated(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResourceDTO> getUnallocatedResources() {
        return resourceRepository.findByAllocated(false)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceDTO updateResource(Long id, ResourceDTO dto) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        BeanUtils.copyProperties(dto, resource, "id", "project");
        Resource updated = resourceRepository.save(resource);
        return convertToDTO(updated);
    }

    private ResourceDTO convertToDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        BeanUtils.copyProperties(resource, dto);
        dto.setProjectId(resource.getProject().getId());
        return dto;
    }
}
