package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.dto.OpenPositionDTO;
import com.pm.Project_Management_Server.entity.OpenPosition;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.repositories.OpenPositionRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.OpenPositionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenPositionServiceImpl implements OpenPositionService {

    private final OpenPositionRepository openPositionRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<OpenPositionDTO> getAllOpenPositions() {
        return openPositionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OpenPositionDTO getById(Long id) {
        OpenPosition op = openPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Open position not found"));
        return toDTO(op);
    }

    @Override
    public List<OpenPositionDTO> getByProjectId(Long projectId) {
        return openPositionRepository.findByProjectId(projectId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OpenPositionDTO createOpenPosition(OpenPositionDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (dto.getLevel() == null) {
            throw new RuntimeException("Resource level cannot be null");
        }

        OpenPosition op = OpenPosition.builder()
                .level(dto.getLevel())
                .numberRequired(dto.getNumberRequired())
                .project(project)
                .build();

        OpenPosition saved = openPositionRepository.save(op);
        return toDTO(saved);
    }

    @Override
    public void deleteOpenPosition(Long id) {
        if (!openPositionRepository.existsById(id)) {
            throw new RuntimeException("Open position not found");
        }
        openPositionRepository.deleteById(id);
    }

    @Override
    public int getTotalOpenPositions() {
        return openPositionRepository.findAll().stream()
                .mapToInt(OpenPosition::getNumberRequired)
                .sum();
    }

    private OpenPositionDTO toDTO(OpenPosition entity) {
        OpenPositionDTO dto = new OpenPositionDTO();
        dto.setId(entity.getId());
        dto.setLevel(entity.getLevel());
        dto.setNumberRequired(entity.getNumberRequired());
        dto.setProjectId(entity.getProject().getId());
        return dto;
    }
}
