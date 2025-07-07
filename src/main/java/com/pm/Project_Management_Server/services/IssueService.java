package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.CreateIssueDTO;
import com.pm.Project_Management_Server.dto.IssueDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IssueService {
    IssueDTO createIssue(CreateIssueDTO dto);
    List<IssueDTO> getIssuesByProject(Long projectId);


    List<IssueDTO> getIssuesBySeverity(String severity);

    IssueDTO closeIssue(Long id);

    List<IssueDTO> getAllIssues();
}
