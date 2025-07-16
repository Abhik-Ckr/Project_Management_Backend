package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.repositories.GlobalRateCardRepository;
import com.pm.Project_Management_Server.repositories.ProjectRateCardRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
import com.pm.Project_Management_Server.entity.GlobalRateCard;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public ProjectRateCardDTO addRateCard(ProjectRateCardDTO request) {
        // 1. Fetch the project
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));

        // 2. Deactivate existing rate card for the same level (if any)
        List<ProjectRateCard> existingCards = projectRateCardRepository
                .findByProjectIdAndLevelAndActiveTrue(request.getProjectId(), request.getLevel());

        for (ProjectRateCard oldCard : existingCards) {
            oldCard.setActive(false);
            oldCard.setEndDate(LocalDate.now().minusDays(1)); // end previous one
            projectRateCardRepository.save(oldCard);
        }

        // 3. Create new rate card
        ProjectRateCard newCard = ProjectRateCard.builder()
                .project(project)
                .level(request.getLevel())
                .rate(request.getRate())
                .active(true)
                .startDate(LocalDate.now()) // Set current date as start
                .build();

        ProjectRateCard saved = projectRateCardRepository.save(newCard);

        // 4. Return mapped DTO
        return toProjectRateCardDTO(saved);
    }



    @Override
    public List<GlobalRateCardDTO> getAllGlobalRates() {
        return globalRateCardRepository.findAll().stream()
                .map(this::toGlobalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectRateCardDTO> getProjectRates(Long projectId) {
        List<ProjectRateCard> projectRateCards = projectRateCardRepository.findByProjectId(projectId);

        if (!projectRateCards.isEmpty()) {
            return projectRateCards.stream()
                    .map(this::toProjectRateCardDTO)
                    .collect(Collectors.toList());
        }

        // Fallback: fetch global rate cards
        List<GlobalRateCard> globalRateCards = globalRateCardRepository.findAll();

        return globalRateCards.stream()
                .map(this::toProjectRateCardDTO)
                .collect(Collectors.toList());
    }



    private GlobalRateCardDTO toGlobalDTO(GlobalRateCard globalRateCard) {
        GlobalRateCardDTO dto = new GlobalRateCardDTO();
        dto.setId(globalRateCard.getId());
        dto.setLevel(globalRateCard.getLevel());
        dto.setRate(globalRateCard.getRate());
        return dto;
    }


    private ProjectRateCardDTO toProjectRateCardDTO(ProjectRateCard card) {
        return ProjectRateCardDTO.builder()
                .id(card.getId())
                .projectId(card.getProject().getId())
                .level(card.getLevel())
                .rate(card.getRate())
                .active(card.getActive())
                .startDate(card.getStartDate())
                .endDate(card.getEndDate())
                .build();
    }
    private ProjectRateCardDTO toProjectRateCardDTO(GlobalRateCard globalCard) {
        return ProjectRateCardDTO.builder()
                .id(globalCard.getId())
                .projectId(null)  // clearly indicates global fallback
                .level(globalCard.getLevel())
                .rate(globalCard.getRate())
                .active(true) // or false if you want to mark differently// since global might not have this
                .build();
    }
    private ProjectRateCard toProjectRateCardEntity(ProjectRateCardDTO dto, Project project) {
        return ProjectRateCard.builder()
                .id(dto.getId())
                .project(project)
                .level(dto.getLevel())
                .rate(dto.getRate())
                .active(dto.getActive())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }

}
