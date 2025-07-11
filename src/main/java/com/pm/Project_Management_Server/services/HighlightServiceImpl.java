package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.exceptions.HighlightNotFoundException;
import com.pm.Project_Management_Server.exceptions.InvalidHighlightDescriptionException;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.repositories.HighlightRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.dto.CreateHighlightDTO;
import com.pm.Project_Management_Server.dto.HighlightDTO;
import com.pm.Project_Management_Server.entity.Highlight;
import com.pm.Project_Management_Server.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HighlightServiceImpl implements HighlightService{
    private final HighlightRepository highlightRepository;
    private final ProjectRepository projectRepository;

    @Override
    public HighlightDTO addHighlight(CreateHighlightDTO dto) {
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new InvalidHighlightDescriptionException();
        }
        
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        LocalDate today = LocalDate.now();
        // COMMENTED OUT FOR ALLOWING MULTIPLE HIGHLIGHTS!
//        boolean exists = highlightRepository.findByProjectId(project.getId())
//                .stream()
//                .anyMatch(h -> h.getCreatedOn().equals(today));
//
//        if (exists) {
//            throw new RuntimeException("A highlight already exists for this project on " + today);
//        }


        Highlight highlight = new Highlight();
        highlight.setProject(project);
        highlight.setDescription(dto.getDescription());
        highlight.setCreatedOn(today);
        
        Highlight saved = highlightRepository.save(highlight);
        return convertToDTO(saved);
    }

    @Override
    public List<HighlightDTO> getHighlightsByProject(Long projectId) {
        return highlightRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHighlight(Long id) {
        if (!highlightRepository.existsById(id)) {
            throw new HighlightNotFoundException(id);
        }
        highlightRepository.deleteById(id);
    }

    @Override
    public List<HighlightDTO> getAllHighlights() {
        List<Highlight> highlights = highlightRepository.findAll();
        return highlights.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public HighlightDTO updateHighlight(Long id, HighlightDTO dto) {
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new HighlightNotFoundException(id));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        highlight.setDescription(dto.getDescription());
        highlight.setCreatedOn(dto.getCreatedOn());
        highlight.setProject(project);

        Highlight saved = highlightRepository.save(highlight);
        return convertToDTO(saved); // Ensure you have this mapping logic
    }


    private HighlightDTO convertToDTO(Highlight highlight) {
        HighlightDTO dto = new HighlightDTO();
        dto.setId(highlight.getId());
        dto.setProjectId(highlight.getProject().getId());
        dto.setDescription(highlight.getDescription());
        dto.setCreatedOn(highlight.getCreatedOn());
        return dto;
    }
}
