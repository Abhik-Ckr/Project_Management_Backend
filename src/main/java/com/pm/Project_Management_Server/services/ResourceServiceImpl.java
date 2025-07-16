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
    @Override
    public ResourceDTO addResource(ResourceDTO dto) {

        Resource resource = new Resource();
        BeanUtils.copyProperties(dto, resource);
        resource.setAllocated(true);
        Resource saved = resourceRepository.save(resource);
        ResourceDTO response = new ResourceDTO();
        BeanUtils.copyProperties(saved, response);
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
        Resource updated = resourceRepository.save(resource);
        return convertToDTO(updated);
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
                .allocated(resource.isAllocated())
                .build();
    }


}
