package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.services.GlobalRateCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/global-rate-cards")
@RequiredArgsConstructor
public class GlobalRateCardController {

    private final GlobalRateCardService service;

    @GetMapping
    public List<GlobalRateCardDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalRateCardDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<GlobalRateCardDTO> getByLevel(@PathVariable String level) {
        return ResponseEntity.ok(service.getByLevel(level));
    }

    @PostMapping
    public ResponseEntity<GlobalRateCardDTO> create(@Valid @RequestBody GlobalRateCardDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
