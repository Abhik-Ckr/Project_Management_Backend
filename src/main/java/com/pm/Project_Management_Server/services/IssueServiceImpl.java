package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.exceptions.InvalidIssueSeverityException;
import com.pm.Project_Management_Server.exceptions.IssueNotFoundException;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.repositories.IssueRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.dto.CreateIssueDTO;
import com.pm.Project_Management_Server.dto.IssueDTO;
import com.pm.Project_Management_Server.entity.Issue;
import com.pm.Project_Management_Server.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService{
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;

    @Override
    public IssueDTO createIssue(CreateIssueDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(dto.getProjectId()));

        Issue.Severity severity;
        try {
            severity = Issue.Severity.valueOf(dto.getSeverity().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidIssueSeverityException(dto.getSeverity());
        }

        Issue issue = new Issue();
        issue.setProject(project);
        issue.setSeverity(severity);
        issue.setDescription(dto.getDescription());
        issue.setCreatedBy(dto.getCreatedBy());
        issue.setCreatedDate(LocalDate.now());
        issue.setStatus(Issue.IssueStatus.OPEN);
        Issue saved = issueRepository.save(issue);
        return convertToDTO(saved);
    }

    @Override
    public List<IssueDTO> getIssuesByProject(Long projectId) {
        return issueRepository.findByProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> getIssuesBySeverity(String severity) {
        Issue.Severity severityEnum = Issue.Severity.valueOf(severity.toUpperCase());
        return issueRepository.findBySeverity(severityEnum).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    @Override
    public IssueDTO closeIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new IssueNotFoundException(id));
        
        issue.setStatus(Issue.IssueStatus.CLOSED);
        Issue saved = issueRepository.save(issue);
        return convertToDTO(saved);
    }

    @Override
    public List<IssueDTO> getAllIssues() {
        List<Issue> issues = issueRepository.findAll();
        return issues.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public IssueDTO updateIssue(Long id, IssueDTO dto) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new IssueNotFoundException(id));

        if (dto.getSeverity() != null) {
            try {
                issue.setSeverity(Issue.Severity.valueOf(dto.getSeverity().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new InvalidIssueSeverityException(dto.getSeverity());
            }
        }

        if (dto.getDescription() != null) {
            issue.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            issue.setStatus(Issue.IssueStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        if (dto.getCreatedBy() != null) {
            issue.setCreatedBy(dto.getCreatedBy());
        }

        issue.setUpdatedDate(LocalDate.now()); // Optional if you use @PreUpdate

        Issue saved = issueRepository.save(issue);

        return convertToDTO(saved);
    }


    @Override
    public void deleteIssue(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new IssueNotFoundException(id);
        }
        issueRepository.deleteById(id);
    }



    private IssueDTO convertToDTO(Issue issue) {
        IssueDTO dto = new IssueDTO();
        dto.setId(issue.getId());
        dto.setProjectId(issue.getProject().getId());
        dto.setSeverity(issue.getSeverity().name());
        dto.setDescription(issue.getDescription());
        dto.setCreatedBy(issue.getCreatedBy());
        dto.setCreatedDate(issue.getCreatedDate());
        dto.setStatus(issue.getStatus().name());
        return dto;
    }
}
