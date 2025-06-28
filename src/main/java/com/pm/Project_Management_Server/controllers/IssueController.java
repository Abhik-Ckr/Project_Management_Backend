package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.Services.IssueService;
import com.pm.Project_Management_Server.dto.CreateIssueDTO;
import com.pm.Project_Management_Server.dto.IssueDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueDTO> createIssue(@RequestBody CreateIssueDTO dto) {
        IssueDTO created = issueService.createIssue(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<IssueDTO>> getIssuesByProject(
            @PathVariable Long projectId,
            @RequestParam(value = "severity", required = false) String severity) {
        if (severity != null) {
            return ResponseEntity.ok(issueService.getIssuesBySeverity(projectId, severity));
        } else {
            return ResponseEntity.ok(issueService.getIssuesByProject(projectId));
        }
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<IssueDTO> closeIssue(@PathVariable Long id) {
        IssueDTO closed = issueService.closeIssue(id);
        return ResponseEntity.ok(closed);
    }
}
