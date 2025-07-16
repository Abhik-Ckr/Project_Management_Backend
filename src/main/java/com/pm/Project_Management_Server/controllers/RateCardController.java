package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.services.RateCardService;
import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/ratecards")
@RequiredArgsConstructor
public class RateCardController {
    private final RateCardService rateCardService;
    @PostMapping
    public ResponseEntity<ProjectRateCardDTO> addRateCard(@RequestBody ProjectRateCardDTO request) {
        return ResponseEntity.ok(rateCardService.addRateCard(request));
    }

    @GetMapping("/global")
    public ResponseEntity<List<GlobalRateCardDTO>> getAllGlobalRates() {
        return ResponseEntity.ok(rateCardService.getAllGlobalRates());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ProjectRateCardDTO>> getProjectRates(@PathVariable Long projectId) {
        return ResponseEntity.ok(rateCardService.getProjectRates(projectId));
    }






}
