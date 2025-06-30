package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.SearchResultDTO;
import com.pm.Project_Management_Server.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public SearchResultDTO search(@RequestParam("q") String keyword) {
        return searchService.globalSearch(keyword);
    }
}

