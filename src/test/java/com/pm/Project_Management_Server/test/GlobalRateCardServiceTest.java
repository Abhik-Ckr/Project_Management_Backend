package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
import com.pm.Project_Management_Server.entity.GlobalRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import com.pm.Project_Management_Server.exceptions.RateCardAlreadyExistsException;
import com.pm.Project_Management_Server.exceptions.RateCardNotFoundException;
import com.pm.Project_Management_Server.repositories.GlobalRateCardRepository;
import com.pm.Project_Management_Server.services.GlobalRateCardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalRateCardServiceTest {

    @Mock
    private GlobalRateCardRepository repository;

    @InjectMocks
    private GlobalRateCardServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        GlobalRateCard card = new GlobalRateCard(1L, ResourceLevel.EXPERT, 3000);
        when(repository.findAll()).thenReturn(List.of(card));

        List<GlobalRateCardDTO> result = service.getAll();
        assertEquals(1, result.size());
        assertEquals(ResourceLevel.EXPERT, result.get(0).getLevel());
    }

    @Test
    void testGetById_Success() {
        GlobalRateCard card = new GlobalRateCard(1L, ResourceLevel.SR, 2500);
        when(repository.findById(1L)).thenReturn(Optional.of(card));

        GlobalRateCardDTO result = service.getById(1L);
        assertEquals(ResourceLevel.SR, result.getLevel());
        assertEquals(2500, result.getRate());
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RateCardNotFoundException.class, () -> service.getById(2L));
    }

    @Test
    void testGetByLevel_Success() {
        GlobalRateCard card = new GlobalRateCard(1L, ResourceLevel.JR, 1000);
        when(repository.findByLevel(ResourceLevel.JR)).thenReturn(Optional.of(card));

        GlobalRateCardDTO result = service.getByLevel("jr");
        assertEquals(1000, result.getRate());
        assertEquals(ResourceLevel.JR, result.getLevel());
    }

    @Test
    void testGetByLevel_NotFound() {
        when(repository.findByLevel(ResourceLevel.INTERMEDIATE)).thenReturn(Optional.empty());
        assertThrows(RateCardNotFoundException.class, () -> service.getByLevel("INTERMEDIATE"));
    }

    @Test
    void testCreate_Success() {
        GlobalRateCardDTO dto = new GlobalRateCardDTO(null, ResourceLevel.ADVANCE, 2200);

        when(repository.existsByLevel(ResourceLevel.ADVANCE)).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> {
            GlobalRateCard g = inv.getArgument(0);
            g.setId(1L);
            return g;
        });

        GlobalRateCardDTO result = service.create(dto);
        assertEquals(ResourceLevel.ADVANCE, result.getLevel());
        assertEquals(2200, result.getRate());
    }

    @Test
    void testCreate_DuplicateLevel() {
        GlobalRateCardDTO dto = new GlobalRateCardDTO(null, ResourceLevel.ADVANCE, 2200);
        when(repository.existsByLevel(ResourceLevel.ADVANCE)).thenReturn(true);

        assertThrows(RateCardAlreadyExistsException.class, () -> service.create(dto));
    }

    @Test
    void testDelete_Success() {
        when(repository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(RateCardNotFoundException.class, () -> service.delete(99L));
    }

    @Test
    void testGetByLevel_InvalidEnum_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.getByLevel("unknownLevel"));
    }
}
