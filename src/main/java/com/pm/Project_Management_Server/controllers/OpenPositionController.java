package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.OpenPositionDTO;
import com.pm.Project_Management_Server.services.OpenPositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/open-positions")
@RequiredArgsConstructor
public class OpenPositionController {

    private final OpenPositionService openPositionService;

    // GET: Get all open positions
    @GetMapping
    public List<OpenPositionDTO> getAll() {
        return openPositionService.getAllOpenPositions();
    }

    // GET: Get open position by ID
    @GetMapping("/{id}")
    public ResponseEntity<OpenPositionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(openPositionService.getById(id));
    }

    // GET: Get open positions by project ID
    @GetMapping("/project/{projectId}")
    public List<OpenPositionDTO> getByProject(@PathVariable Long projectId) {
        return openPositionService.getByProjectId(projectId);
    }

    // POST: Create a new open position
    @PostMapping
    public ResponseEntity<OpenPositionDTO> create(@Valid @RequestBody OpenPositionDTO dto) {
        return ResponseEntity.ok(openPositionService.createOpenPosition(dto));
    }

    // PUT: Update an existing open position
    @PutMapping("/{id}")
    public ResponseEntity<OpenPositionDTO> update(@PathVariable Long id, @Valid @RequestBody OpenPositionDTO dto) {
        return ResponseEntity.ok(openPositionService.updateOpenPosition(id, dto));
    }

    // DELETE: Delete an open position by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        openPositionService.deleteOpenPosition(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Get the total count of open positions (sum of numberRequired across all)
    @GetMapping("/count")
    public int getTotalOpenPositions() {
        return openPositionService.getTotalOpenPositions();
    }
}
