package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface RateCardService {
    List<GlobalRateCardDTO> getAllGlobalRates();
    List<ProjectRateCardDTO> getProjectRates(Long projectId);
    ProjectRateCardDTO overrideRate(Long projectId, String level, Double rate);
    void initializeProjectRatesFromGlobal(Long projectId);
}
