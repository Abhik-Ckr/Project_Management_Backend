package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ClientDTO;
import com.pm.Project_Management_Server.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO dto) {
        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }



    @GetMapping("/rating/{rating}")
    public List<ClientDTO> getByRating(@PathVariable int rating) {
        return clientService.getClientsByRating(rating);
    }

    @GetMapping("/search")
    public List<ClientDTO> searchByName(@RequestParam String name) {
        return clientService.searchClientsByName(name);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO updatedDto) {
        return clientService.updateClient(id, updatedDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientService.deleteClient(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
