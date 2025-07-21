package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ResourceAllocatedDTO;
import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.dto.ResourceDTO;
import com.pm.Project_Management_Server.services.ResourceAllocatedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-allocations")
@RequiredArgsConstructor
public class ResourceAllocatedController {

    private final ResourceAllocatedService resourceAllocatedService;

    @PostMapping("/allocate")
    public ResponseEntity<String> allocateResource(@RequestBody ResourceAllocationRequestDTO request) {
        String result = resourceAllocatedService.allocateResource(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert")
    public ResponseEntity<ResourceAllocatedDTO> allocateResource(@RequestBody ResourceAllocatedDTO dto) {
        ResourceAllocatedDTO saved = resourceAllocatedService.allocateResource(dto);
        return ResponseEntity.ok(saved);
    }


    @PostMapping("/deallocate/{allocationId}")
    public ResponseEntity<String> deallocateResource(@PathVariable Long allocationId) {
        resourceAllocatedService.deallocateResource(allocationId);
        return ResponseEntity.ok("Resource deallocated successfully.");
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ResourceAllocatedDTO>> getByClient(@PathVariable Long clientId) {
        List<ResourceAllocatedDTO> list = resourceAllocatedService.getResourcesByClientId(clientId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceAllocatedDTO>> getResourcesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(resourceAllocatedService.getResourcesByProject(projectId));
    }
}
