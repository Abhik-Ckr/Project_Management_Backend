package com.pm.Project_Management_Server.controllers;


import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // ---------- Basic CRUD ----------

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }


    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Client-based Queries ----------

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(projectService.getProjectsByClient(clientId));
    }

    // ---------- Status-based Queries ----------

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectDTO>> getProjectsByStatus(@PathVariable Project.Status status) {
        return ResponseEntity.ok(projectService.getProjectsByStatus(status));
    }

    // ---------- Budget Checks ----------

    @GetMapping("/over-budget")
    public ResponseEntity<List<ProjectDTO>> getProjectsOverBudget() {
        return ResponseEntity.ok(projectService.getProjectsOverBudget());
    }

    @GetMapping("/count-over-budget")
    public ResponseEntity<Long> countProjectsOverBudget() {
        return ResponseEntity.ok(projectService.countProjectsOverBudget());
    }

    // ---------- Budget Spent for a Project ----------

    @GetMapping("/{id}/budget-spent")
    public ResponseEntity<Double> calculateBudgetSpent(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.calculateBudgetSpentById(id));
    }
}
