package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ResourceAllocationRequestDTO;
import com.pm.Project_Management_Server.services.ResourceAllocatedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource-allocations")
@RequiredArgsConstructor
public class ResourceAllocatedController {

    private final ResourceAllocatedService resourceAllocatedService;

    @PostMapping
    public ResponseEntity<String> allocateResource(@RequestBody ResourceAllocationRequestDTO request) {
        String result = resourceAllocatedService.allocateResource(request);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{resourceId}/deallocate")
    public ResponseEntity<String> deallocateResource(@PathVariable Long resourceId) {
        String result = resourceAllocatedService.deallocateResource(resourceId);
        return ResponseEntity.ok(result);
    }
}
