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

    @GetMapping
    public List<OpenPositionDTO> getAll() {
        return openPositionService.getAllOpenPositions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpenPositionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(openPositionService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    public List<OpenPositionDTO> getByProject(@PathVariable Long projectId) {
        return openPositionService.getByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<OpenPositionDTO> create(@Valid @RequestBody OpenPositionDTO dto) {
        return ResponseEntity.ok(openPositionService.createOpenPosition(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        openPositionService.deleteOpenPosition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public int getTotalOpenPositions() {
        return openPositionService.getTotalOpenPositions();
    }
}
