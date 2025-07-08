package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ProjectLeadDTO;
import com.pm.Project_Management_Server.dto.UserDTO;
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

    // 🔍 Get all project leads
    // ➕ Add a new project lead
    @PostMapping
    public ResponseEntity<ProjectLeadDTO> addProjectLead(@RequestBody ProjectLeadDTO projectLeadDTO) {
        ProjectLeadDTO savedLead = projectLeadService.addProjectLead(projectLeadDTO);
        return ResponseEntity.ok(savedLead);
    }


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllProjectLeads() {
        List<UserDTO> leads = projectLeadService.getAllProjectLeadUsers();
        return ResponseEntity.ok(leads);
    }


    // 🔍 Get a project lead by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectLeadDTO> getLeadById(@PathVariable Long id) {
        ProjectLeadDTO lead = projectLeadService.getById(id);
        return ResponseEntity.ok(lead);
    }

    // 🔍 Get a project lead by project ID
    @GetMapping("/project/{projectId}")
    public ResponseEntity<UserDTO> getLeadByProjectId(@PathVariable Long projectId) {
        UserDTO lead = projectLeadService.getUserByProjectId(projectId);
        return ResponseEntity.ok(lead);
    }

    // ➕ Assign a lead to a project


    // ❌ Remove a project lead by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeLead(@PathVariable Long id) {
        projectLeadService.removeProjectLead(id);
        return ResponseEntity.noContent().build();
    }
}
