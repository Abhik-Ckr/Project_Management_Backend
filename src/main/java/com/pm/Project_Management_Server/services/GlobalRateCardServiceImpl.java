package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.entity.GlobalRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.repositories.GlobalRateCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GlobalRateCardServiceImpl implements GlobalRateCardService {

    private final GlobalRateCardRepository repository;

    @Override
    public List<GlobalRateCardDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GlobalRateCardDTO getById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Rate card not found"));
    }

    @Override
    public GlobalRateCardDTO getByLevel(String levelStr) {
        ResourceLevel level = ResourceLevel.valueOf(levelStr.toUpperCase());
        return repository.findByLevel(level)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Level not found: " + levelStr));
    }

    @Override
    public GlobalRateCardDTO create(GlobalRateCardDTO dto) {
        ResourceLevel level = ResourceLevel.valueOf(dto.getLevel().toUpperCase());

        if (repository.existsByLevel(level)) {
            throw new RuntimeException("Rate already exists for level: " + level);
        }

        GlobalRateCard entity = new GlobalRateCard();
        entity.setLevel(level);
        entity.setRate(dto.getHourlyRate().doubleValue());

        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Rate card not found");
        }
        repository.deleteById(id);
    }

    private GlobalRateCardDTO toDTO(GlobalRateCard entity) {
        return new GlobalRateCardDTO(
                entity.getId(),
                entity.getLevel().name(),
                java.math.BigDecimal.valueOf(entity.getRate())
        );
    }
}
