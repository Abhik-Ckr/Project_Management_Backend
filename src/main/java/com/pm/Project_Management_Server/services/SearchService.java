package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.SearchResultDTO;

public interface SearchService {
    SearchResultDTO globalSearch(String keyword);
}
