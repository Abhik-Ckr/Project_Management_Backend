package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.Repositories.GlobalRateCardRepository;
import com.pm.Project_Management_Server.Repositories.ProjectRateCardRepository;
import com.pm.Project_Management_Server.Repositories.ProjectRepository;
import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
import com.pm.Project_Management_Server.entity.GlobalRateCard;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RateCardServiceImpl implements RateCardService {
    
    @Autowired
    private GlobalRateCardRepository globalRateCardRepository;
    
    @Autowired
    private ProjectRateCardRepository projectRateCardRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Override
    public List<GlobalRateCardDTO> getAllGlobalRates() {
        return globalRateCardRepository.findAll().stream()
                .map(this::toGlobalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRateCardDTO> getProjectRates(Long projectId) {
        return projectRateCardRepository.findAll().stream()
                .filter(rateCard -> rateCard.getProject() != null && rateCard.getProject().getId().equals(projectId))
                .map(this::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectRateCardDTO overrideRate(Long projectId, String level, Double rate) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        
        ResourceLevel lvl;
        try {
            lvl = ResourceLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
        
        // Deactivate any existing active rate for this project+level
        Optional<ProjectRateCard> existingOpt = projectRateCardRepository.findByProject_IdAndLevel(projectId, lvl);
        if (existingOpt.isPresent() && existingOpt.get().getIsActive()) {
            ProjectRateCard existing = existingOpt.get();
            existing.setIsActive(false);
            projectRateCardRepository.save(existing);
        }
        
        ProjectRateCard prc = existingOpt.orElse(new ProjectRateCard());
        prc.setProject(project);
        prc.setLevel(lvl);
        prc.setRate(rate);
        prc.setIsActive(true);
        prc.setLastUpdated(LocalDateTime.now());
        
        ProjectRateCard saved = projectRateCardRepository.save(prc);
        return toProjectDTO(saved);
    }

    @Override
    public void initializeProjectRatesFromGlobal(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        
        List<GlobalRateCard> globalRates = globalRateCardRepository.findAll();
        for (GlobalRateCard grc : globalRates) {
            ProjectRateCard prc = new ProjectRateCard();
            prc.setProject(project);
            prc.setLevel(grc.getLevel());
            prc.setRate(grc.getRate());
            prc.setIsActive(true);
            prc.setLastUpdated(LocalDateTime.now());
            projectRateCardRepository.save(prc);
        }
    }
    
    private GlobalRateCardDTO toGlobalDTO(GlobalRateCard globalRateCard) {
        GlobalRateCardDTO dto = new GlobalRateCardDTO();
        dto.setId(globalRateCard.getId());
        dto.setLevel(globalRateCard.getLevel().name());
        dto.setHourlyRate(java.math.BigDecimal.valueOf(globalRateCard.getRate()));
        return dto;
    }
    
    private ProjectRateCardDTO toProjectDTO(ProjectRateCard projectRateCard) {
        ProjectRateCardDTO dto = new ProjectRateCardDTO();
        dto.setId(projectRateCard.getId());
        dto.setProjectId(projectRateCard.getProject() != null ? projectRateCard.getProject().getId() : null);
        dto.setLevel(projectRateCard.getLevel().name());
        dto.setHourlyRate(java.math.BigDecimal.valueOf(projectRateCard.getRate()));
        dto.setIsActive(projectRateCard.getIsActive());
        dto.setLastUpdated(projectRateCard.getLastUpdated());
        return dto;
    }
}
