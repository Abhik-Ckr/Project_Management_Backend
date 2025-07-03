package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ClientDTO;
import com.pm.Project_Management_Server.entity.Client;
import com.pm.Project_Management_Server.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientDTO createClient(ClientDTO dto) {
        Client saved = clientRepository.save(toEntity(dto));
        return toDTO(saved);
    }

    public Optional<ClientDTO> getClientById(Long id) {
        return clientRepository.findById(id).map(this::toDTO);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }



    public List<ClientDTO> getClientsByRating(int rating) {
        return clientRepository.findByClientRatingGreaterThanEqual(rating)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ClientDTO> searchClientsByName(String keyword) {
        return clientRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<ClientDTO> updateClient(Long id, ClientDTO dto) {
        return clientRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setOnBoardedOn(dto.getOnBoardedOn());
            existing.setClientRating(dto.getClientRating());
            return toDTO(clientRepository.save(existing));
        });
    }

    public boolean deleteClient(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }



    private ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getOnBoardedOn(),
                client.getClientRating()
        );
    }

    private Client toEntity(ClientDTO dto) {
        return Client.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .onBoardedOn(dto.getOnBoardedOn())
                .clientRating(dto.getClientRating())
                .build();
    }

}
