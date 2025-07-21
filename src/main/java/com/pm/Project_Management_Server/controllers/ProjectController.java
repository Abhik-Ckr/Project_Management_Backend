package com.pm.Project_Management_Server.controllers;


import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.dto.ProjectStatusUpdateRequestDTO;
import com.pm.Project_Management_Server.dto.ResourceDeficitDTO;
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
    @GetMapping("/{projectId}/total-deficit")
    public ResponseEntity<Integer> getTotalDeficit(@PathVariable Long projectId) {
        int count = projectService.getTotalResourceDeficitCount(projectId);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/{projectId}/resource-deficit")
    public ResponseEntity<List<ResourceDeficitDTO>> getResourceDeficit(@PathVariable Long projectId) {
        List<ResourceDeficitDTO> result = projectService.getResourceDeficitReport(projectId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }
    @PutMapping("/projects/{projectId}/status")
    public ResponseEntity<String> updateProjectStatus(@PathVariable Long projectId,
                                                      @RequestBody ProjectStatusUpdateRequestDTO request) {
        projectService.updateProjectStatus(projectId, request.getNewStatus());
        return ResponseEntity.ok("Project status updated successfully.");
    }


    @GetMapping("/{projectId}/estimated-completion-cost")
    public ResponseEntity<Double> getEstimatedCompletionCost(@PathVariable Long projectId) {
        double cost = projectService.estimateCompletionCost(projectId);
        return ResponseEntity.ok(cost);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
    @GetMapping("/resourcedeficit-projects-count")
    public ResponseEntity<Long> getProjectsWithResourceDeficit() {
        long count = projectService.countProjectsWithResourceDeficit();
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }
    @GetMapping("/{projectId}/contact-person")
    public ResponseEntity<ContactPersonDTO> getContactPersonForProject(@PathVariable Long projectId) {
        ContactPersonDTO dto = projectService.getContactPersonByProjectId(projectId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }


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
    @GetMapping("/{projectId}/total-required")
    public ResponseEntity<Integer> getTotalResourcesRequired(@PathVariable Long projectId) {
        int totalRequired = projectService.getTotalResourcesRequired(projectId);
        return ResponseEntity.ok(totalRequired);
    }

}
