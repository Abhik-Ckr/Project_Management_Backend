package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.services.ProjectLeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-leads")
@RequiredArgsConstructor
public class ProjectLeadController {

    private final ProjectLeadService projectLeadService;

    // Assign a new project lead to a project
    @PostMapping
    public ResponseEntity<ProjectLeadDTO> assignLead(@RequestBody ProjectLeadDTO dto) {
        ProjectLeadDTO assignedLead = projectLeadService.assignLeadToProject(dto);
        return ResponseEntity.ok(assignedLead);
    }
    @GetMapping
    public ResponseEntity<List<ProjectLeadDTO>> getAllLeads() {
        return ResponseEntity.ok(projectLeadService.getAllLeads());
    }

    // End the current lead assignment for a project
    @PutMapping("/end/{projectId}")
    public ResponseEntity<ProjectLeadDTO> endLead(@PathVariable Long projectId) {
        ProjectLeadDTO endedLead = projectLeadService.endLeadAssignment(projectId);
        return ResponseEntity.ok(endedLead);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<List<ProjectLeadDTO>> getProjectLeadHistory(@PathVariable Long projectId) {
        List<ProjectLeadDTO> history = projectLeadService.getProjectLeadByProjectId(projectId);
        return ResponseEntity.ok(history);
    }
    @PostMapping("/assign/user/{userId}/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ProjectLeadDTO> assignProjectLead(@PathVariable Long userId,
                                                            @PathVariable Long projectId) {
        ProjectLeadDTO dto = projectLeadService.assignProjectLead(userId, projectId);
        return ResponseEntity.ok(dto);
    }
    // Get the current project lead for a specific project
    @GetMapping("/current/{projectId}")
    public ResponseEntity<ProjectLeadDTO> getCurrentLead(@PathVariable Long projectId) {
        ProjectLeadDTO currentLead = projectLeadService.getCurrentLeadForProject(projectId);
        return ResponseEntity.ok(currentLead);}

}
