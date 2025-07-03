package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.services.ProjectLeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-leads")
@RequiredArgsConstructor
public class ProjectLeadController {

    private final ProjectLeadService projectLeadService;

    @GetMapping
    public List<ProjectLeadDTO> getAll() {
        return projectLeadService.getAllProjectLeads();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectLeadDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectLeadService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectLeadDTO> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectLeadService.getByProjectId(projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectLeadDTO> assignLead(@Valid @RequestBody ProjectLeadDTO dto) {
        return ResponseEntity.ok(projectLeadService.assignLeadToProject(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        projectLeadService.removeProjectLead(id);
        return ResponseEntity.noContent().build();
    }
}
