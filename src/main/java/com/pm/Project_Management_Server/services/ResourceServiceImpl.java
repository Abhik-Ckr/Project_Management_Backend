package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.Resource;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));
        Resource resource = new Resource();
        BeanUtils.copyProperties(dto, resource);
        resource.setProject(project);
        resource.setAllocated(true);
        Resource saved = resourceRepository.save(resource);
        ResourceDTO response = new ResourceDTO();
        BeanUtils.copyProperties(saved, response);
        response.setProjectId(project.getId());
        response.setActualEndDate(saved.getActualEndDate());
        response.setExited(saved.isExited());
        return response;
    }

    @Override
    public List<ResourceDTO> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();
        return resources.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
//        ResourceLevel resourceLevel = ResourceLevel.valueOf(level.toUpperCase());
//        return resourceRepository.findByLevel(resourceLevel)
//                .stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
        try {
            ResourceLevel resourceLevel = ResourceLevel.valueOf(level.toUpperCase());
            return resourceRepository.findByLevel(resourceLevel)
                    .stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource level: " + level);
        }
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
        resource.setActualEndDate(dto.getActualEndDate());
        resource.setExited(dto.isExited());
        Resource updated = resourceRepository.save(resource);
        return convertToDTO(updated);
    }

    @Override
    public List<ResourceDTO> getResourcesByClientId(Long clientId) {
        List<Resource> resources = resourceRepository.findByProject_Client_Id(clientId);
        return resources.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        resourceRepository.deleteById(id);
    }


    private ResourceDTO convertToDTO(Resource resource) {
        return ResourceDTO.builder()
                .id(resource.getId())
                .resourceName(resource.getResourceName())
                .level(resource.getLevel())
                .startDate(resource.getStartDate())
                .endDate(resource.getEndDate())
                .allocated(resource.isAllocated())
                .actualEndDate(resource.getActualEndDate())
                .exited(resource.isExited())
                .projectId(resource.getProject() != null ? resource.getProject().getId() : null)
                .build();
    }


}
