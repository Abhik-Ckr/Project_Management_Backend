package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.OpenPositionDTO;
import java.util.List;

public interface OpenPositionService {
    List<OpenPositionDTO> getAllOpenPositions();
    OpenPositionDTO getById(Long id);
    List<OpenPositionDTO> getByProjectId(Long projectId);
    OpenPositionDTO createOpenPosition(OpenPositionDTO dto);
    void deleteOpenPosition(Long id);
    int getTotalOpenPositions();
}
