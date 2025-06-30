package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ResourceRequiredDTO;
import com.pm.Project_Management_Server.services.ResourceRequiredService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-requirements")
@RequiredArgsConstructor
public class ResourceRequiredController {

    private final ResourceRequiredService resourceRequiredService;

    @PostMapping
    public ResponseEntity<ResourceRequiredDTO> createRequirement(@RequestBody ResourceRequiredDTO dto) {
        return ResponseEntity.ok(resourceRequiredService.addRequirement(dto));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceRequiredDTO>> getRequirementsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(resourceRequiredService.getRequirementsByProject(projectId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceRequiredDTO> updateRequirement(@PathVariable Long id, @RequestBody ResourceRequiredDTO dto) {
        return ResponseEntity.ok(resourceRequiredService.updateRequirement(id, dto));
    }

}
