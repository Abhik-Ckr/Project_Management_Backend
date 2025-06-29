package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.CreateHighlightDTO;
import com.pm.Project_Management_Server.dto.HighlightDTO;

import java.util.List;

public interface HighlightService {
    HighlightDTO addHighlight(CreateHighlightDTO dto);
    List<HighlightDTO> getHighlightsByProject(Long projectId);
    void deleteHighlight(Long id);
}
