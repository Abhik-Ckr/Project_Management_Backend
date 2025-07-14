package com.pm.Project_Management_Server.controllers;


import com.pm.Project_Management_Server.dto.ExitRequestDTO;
import com.pm.Project_Management_Server.services.ResourceServiceImpl;
import com.pm.Project_Management_Server.dto.ResourceDTO;
import com.pm.Project_Management_Server.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceServiceImpl resourceService;

    @PostMapping
    public ResponseEntity<ResourceDTO> createResource(@RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.addResource(dto));
    }
    @GetMapping
    public List<ResourceDTO> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceDTO>> getResourcesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(resourceService.getResourcesByProject(projectId));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ResourceDTO>> getResourcesByClient(@PathVariable Long clientId) {
        List<ResourceDTO> resources = resourceService.getResourcesByClientId(clientId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<ResourceDTO>> getResourcesByLevel(@PathVariable String level) {
        return ResponseEntity.ok(resourceService.getResourcesByLevel(level));
    }

    @GetMapping("/allocated")
    public ResponseEntity<List<ResourceDTO>> getAllocatedResources() {
        return ResponseEntity.ok(resourceService.getAllocatedResources());
    }

    @GetMapping("/unallocated")
    public ResponseEntity<List<ResourceDTO>> getUnallocatedResources() {
        return ResponseEntity.ok(resourceService.getUnallocatedResources());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> updateResource(@PathVariable Long id, @RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.updateResource(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/exit")
    public ResponseEntity<ResourceDTO> exitResource(@PathVariable Long id, @RequestBody ExitRequestDTO dto) {
        return ResponseEntity.ok(resourceService.exitResource(id, dto));
    }


}
