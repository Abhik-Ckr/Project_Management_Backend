package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface RateCardService {
    List<GlobalRateCardDTO> getAllGlobalRates();
    List<ProjectRateCardDTO> getProjectRates(Long projectId);
    ProjectRateCardDTO addRateCard(ProjectRateCardDTO request);
}
