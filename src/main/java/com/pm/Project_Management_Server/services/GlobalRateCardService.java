package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;

import java.util.List;

public interface GlobalRateCardService {
    List<GlobalRateCardDTO> getAll();
    GlobalRateCardDTO getById(Long id);
    GlobalRateCardDTO getByLevel(String level);
    GlobalRateCardDTO create(GlobalRateCardDTO dto);
    void delete(Long id);
}