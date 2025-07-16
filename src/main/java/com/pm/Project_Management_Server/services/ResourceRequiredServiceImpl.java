package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ResourceRequiredDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceRequired;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.RequirementNotFoundException;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.repositories.ResourceRequiredRepository;
import lombok.RequiredArgsConstructor;
import com.pm.Project_Management_Server.services.ResourceRequiredService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceRequiredServiceImpl implements ResourceRequiredService {

    private final ResourceRequiredRepository resourceRequiredRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ResourceRequiredDTO addRequirement(ResourceRequiredDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        ResourceRequired requirement = new ResourceRequired();
        requirement.setProject(project);
        requirement.setResourceLevel(dto.getLevel());
        requirement.setQuantity(dto.getQuantity());

        ResourceRequired saved = resourceRequiredRepository.save(requirement);

        return convertToDTO(saved);
    }

    @Override
    public List<ResourceRequiredDTO> getRequirementsByProject(Long projectId) {
        return resourceRequiredRepository.findByProjectId(projectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceRequiredDTO updateRequirement(Long id, ResourceRequiredDTO dto) {
        ResourceRequired existing = resourceRequiredRepository.findById(id)
                .orElseThrow(() -> new RequirementNotFoundException((id)));

        existing.setResourceLevel(dto.getLevel());
        existing.setQuantity(dto.getQuantity());

        ResourceRequired updated = resourceRequiredRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public void deleteRequirement(Long id) {
        if (!resourceRequiredRepository.existsById(id)) {
            throw new RequirementNotFoundException(id);
        }
        resourceRequiredRepository.deleteById(id);
    }

    private ResourceRequiredDTO convertToDTO(ResourceRequired requirement) {
        ResourceRequiredDTO dto = new ResourceRequiredDTO();
        dto.setId(requirement.getId());
        dto.setLevel(requirement.getResourceLevel());
        dto.setQuantity(requirement.getQuantity());
        dto.setProjectId(requirement.getProject().getId());
        return dto;
    }
}
