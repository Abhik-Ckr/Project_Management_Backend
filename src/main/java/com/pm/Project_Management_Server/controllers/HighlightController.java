package com.pm.Project_Management_Server.controllers;


import com.pm.Project_Management_Server.Services.HighlightService;
import com.pm.Project_Management_Server.dto.CreateHighlightDTO;
import com.pm.Project_Management_Server.dto.HighlightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/highlights")
@RequiredArgsConstructor
public class HighlightController {
    private final HighlightService highlightService;

    @PostMapping
    public ResponseEntity<HighlightDTO> addHighlight(@RequestBody CreateHighlightDTO dto) {
        HighlightDTO created = highlightService.addHighlight(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<HighlightDTO>> getHighlightsByProject(@PathVariable Long projectId) {
        List<HighlightDTO> highlights = highlightService.getHighlightsByProject(projectId);
        return ResponseEntity.ok(highlights);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHighlight(@PathVariable Long id) {
        highlightService.deleteHighlight(id);
        return ResponseEntity.noContent().build();
    }
}
