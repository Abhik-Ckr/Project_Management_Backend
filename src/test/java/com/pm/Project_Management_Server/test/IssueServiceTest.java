package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.CreateIssueDTO;
import com.pm.Project_Management_Server.dto.IssueDTO;
import com.pm.Project_Management_Server.entity.Issue;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.exceptions.IssueNotFoundException;
import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
import com.pm.Project_Management_Server.exceptions.InvalidIssueSeverityException;
import com.pm.Project_Management_Server.repositories.IssueRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.IssueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private IssueServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateIssue_Success() {
        CreateIssueDTO dto = new CreateIssueDTO();
        dto.setProjectId(1L);
        dto.setSeverity("HIGH");
        dto.setDescription("Example issue");
        dto.setCreatedBy("Tester");

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(issueRepository.save(any())).thenAnswer(inv -> {
            Issue i = inv.getArgument(0);
            i.setId(10L);
            return i;
        });

        IssueDTO result = service.createIssue(dto);

        assertEquals("Example issue", result.getDescription());
        assertEquals("HIGH", result.getSeverity());
        assertEquals("OPEN", result.getStatus());
        assertEquals("Tester", result.getCreatedBy());
    }

    @Test
    void testCreateIssue_ProjectNotFound() {
        CreateIssueDTO dto = new CreateIssueDTO();
        dto.setProjectId(999L);
        dto.setSeverity("LOW");
        dto.setDescription("Missing project");

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.createIssue(dto));
    }

    @Test
    void testCreateIssue_InvalidSeverity() {
        CreateIssueDTO dto = new CreateIssueDTO();
        dto.setProjectId(1L);
        dto.setSeverity("INVALID");
        dto.setDescription("Invalid severity");

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(InvalidIssueSeverityException.class, () -> service.createIssue(dto));
    }

    @Test
    void testGetIssuesByProject() {
        Project project = new Project();
        project.setId(1L);

        Issue issue = new Issue();
        issue.setId(101L);
        issue.setProject(project);
        issue.setDescription("Proj issue");
        issue.setSeverity(Issue.Severity.MEDIUM);
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setCreatedBy("Dev");
        issue.setCreatedDate(LocalDate.now());

        when(issueRepository.findByProjectId(1L)).thenReturn(List.of(issue));

        List<IssueDTO> result = service.getIssuesByProject(1L);
        assertEquals(1, result.size());
        assertEquals("Proj issue", result.get(0).getDescription());
    }

    @Test
    void testGetIssuesBySeverity() {
        Issue issue = new Issue();
        issue.setId(5L);
        issue.setSeverity(Issue.Severity.LOW);
        issue.setDescription("Low sev");

        Project project = new Project();
        project.setId(2L);
        issue.setProject(project);
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setCreatedBy("QA");
        issue.setCreatedDate(LocalDate.now());

        when(issueRepository.findBySeverity(Issue.Severity.LOW)).thenReturn(List.of(issue));

        List<IssueDTO> result = service.getIssuesBySeverity("LOW");
        assertEquals(1, result.size());
        assertEquals("Low sev", result.get(0).getDescription());
    }

    @Test
    void testCloseIssue_Success() {
        Issue issue = new Issue();
        issue.setId(20L);
        issue.setStatus(Issue.IssueStatus.OPEN);

        Project project = new Project();
        project.setId(3L);
        issue.setProject(project);
        issue.setCreatedBy("A");
        issue.setDescription("To close");
        issue.setSeverity(Issue.Severity.HIGH);
        issue.setCreatedDate(LocalDate.now());

        when(issueRepository.findById(20L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IssueDTO result = service.closeIssue(20L);
        assertEquals("CLOSED", result.getStatus());
    }

    @Test
    void testCloseIssue_NotFound() {
        when(issueRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(IssueNotFoundException.class, () -> service.closeIssue(404L));
    }

    @Test
    void testGetAllIssues() {
        Issue issue1 = new Issue();
        issue1.setId(1L);
        issue1.setSeverity(Issue.Severity.LOW);
        issue1.setDescription("One");
        issue1.setStatus(Issue.IssueStatus.OPEN);
        issue1.setCreatedBy("Test");
        issue1.setCreatedDate(LocalDate.now());
        Project project = new Project();
        project.setId(1L);
        issue1.setProject(project);

        when(issueRepository.findAll()).thenReturn(List.of(issue1));

        List<IssueDTO> result = service.getAllIssues();
        assertEquals(1, result.size());
        assertEquals("One", result.get(0).getDescription());
    }

    @Test
    void testUpdateIssue_Success() {
        Issue issue = new Issue();
        issue.setId(1L);
        issue.setDescription("Old Desc");
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setSeverity(Issue.Severity.LOW);
        issue.setCreatedBy("OldUser");
        Project project = new Project();
        project.setId(1L);
        issue.setProject(project);

        IssueDTO dto = new IssueDTO();
        dto.setDescription("New Desc");
        dto.setStatus("CLOSED");
        dto.setSeverity("HIGH");
        dto.setCreatedBy("NewUser");

        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IssueDTO result = service.updateIssue(1L, dto);

        assertEquals("New Desc", result.getDescription());
        assertEquals("CLOSED", result.getStatus());
        assertEquals("HIGH", result.getSeverity());
        assertEquals("NewUser", result.getCreatedBy());
    }

    @Test
    void testUpdateIssue_NotFound() {
        IssueDTO dto = new IssueDTO();
        dto.setDescription("X");
        when(issueRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IssueNotFoundException.class, () -> service.updateIssue(99L, dto));
    }

    @Test
    void testDeleteIssue_Success() {
        when(issueRepository.existsById(10L)).thenReturn(true);
        service.deleteIssue(10L);
        verify(issueRepository).deleteById(10L);
    }

    @Test
    void testDeleteIssue_NotFound() {
        when(issueRepository.existsById(88L)).thenReturn(false);
        assertThrows(IssueNotFoundException.class, () -> service.deleteIssue(88L));
    }
}
