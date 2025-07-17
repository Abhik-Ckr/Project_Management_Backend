package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ClientDTO;
import com.pm.Project_Management_Server.entity.Client;
import com.pm.Project_Management_Server.repositories.ClientRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @Mock private ClientRepository clientRepo;
    @Mock private ProjectRepository projectRepo;

    @InjectMocks private ClientService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateClient() {
        ClientDTO dto = new ClientDTO(null, "TCS", "client@tcs.com", LocalDate.now(), 4);
        Client saved = Client.builder().id(1L).name("TCS").email("client@tcs.com").onBoardedOn(LocalDate.now()).clientRating(4).build();

        when(clientRepo.save(any())).thenReturn(saved);

        ClientDTO result = service.createClient(dto);
        assertNotNull(result.getId());
        assertEquals("TCS", result.getName());
    }

    @Test
    void testGetClientById_Found() {
        Client c = Client.builder().id(1L).name("Infosys").email("infy@infy.com").clientRating(5).build();
        when(clientRepo.findById(1L)).thenReturn(Optional.of(c));

        Optional<ClientDTO> result = service.getClientById(1L);
        assertTrue(result.isPresent());
        assertEquals("Infosys", result.get().getName());
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<ClientDTO> result = service.getClientById(99L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllClients() {
        Client c1 = Client.builder().id(1L).name("A").build();
        Client c2 = Client.builder().id(2L).name("B").build();

        when(clientRepo.findAll()).thenReturn(List.of(c1, c2));

        List<ClientDTO> result = service.getAllClients();
        assertEquals(2, result.size());
    }

    @Test
    void testGetClientByProjectId_Found() {
        Client client = Client.builder().id(7L).name("Zoho").build();
        when(projectRepo.findClientByProjectId(100L)).thenReturn(Optional.of(client));

        Optional<ClientDTO> result = service.getClientByProjectId(100L);
        assertTrue(result.isPresent());
        assertEquals("Zoho", result.get().getName());
    }

    @Test
    void testGetClientByProjectId_NotFound() {
        when(projectRepo.findClientByProjectId(404L)).thenReturn(Optional.empty());

        Optional<ClientDTO> result = service.getClientByProjectId(404L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetClientsByRating() {
        Client c1 = Client.builder().clientRating(5).build();
        Client c2 = Client.builder().clientRating(4).build();

        when(clientRepo.findByClientRatingGreaterThanEqual(4)).thenReturn(List.of(c1, c2));

        List<ClientDTO> result = service.getClientsByRating(4);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchClientsByName() {
        Client c = Client.builder().name("Oracle").build();
        when(clientRepo.findByNameContainingIgnoreCase("ora")).thenReturn(List.of(c));

        List<ClientDTO> result = service.searchClientsByName("ora");
        assertEquals("Oracle", result.get(0).getName());
    }

    @Test
    void testUpdateClient_Success() {
        Client existing = Client.builder()
                .id(10L)
                .name("OldName")
                .email("old@mail.com")
                .clientRating(2)
                .build();

        ClientDTO dto = new ClientDTO(10L, "NewName", "new@mail.com", LocalDate.now(), 5);

        when(clientRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(clientRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<ClientDTO> result = service.updateClient(10L, dto);
        assertTrue(result.isPresent());
        assertEquals("NewName", result.get().getName());
        assertEquals("new@mail.com", result.get().getEmail());
        assertEquals(5, result.get().getClientRating());
    }

//    @Test
//    void testUpdateClient_NotFound() {
//        when(clientRepo.findById(404L)).thenReturn(Optional.empty());
//
//        Optional<ClientDTO> result = service.updateClient(404L, new ClientDTO());
//        assertTrue(result.isEmpty());
//    }

    @Test
    void testDeleteClient_Success() {
        when(clientRepo.existsById(3L)).thenReturn(true);
        boolean deleted = service.deleteClient(3L);

        verify(clientRepo).deleteById(3L);
        assertTrue(deleted);
    }

    @Test
    void testDeleteClient_NotFound() {
        when(clientRepo.existsById(5L)).thenReturn(false);
        boolean deleted = service.deleteClient(5L);
        assertFalse(deleted);
    }
}
