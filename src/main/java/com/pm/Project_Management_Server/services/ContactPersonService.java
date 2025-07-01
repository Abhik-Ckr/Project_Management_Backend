package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.entity.ContactPerson;
import com.pm.Project_Management_Server.repositories.ContactPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactPersonService {

    private final ContactPersonRepository contactPersonRepository;

    public Optional<ContactPersonDTO> getById(Long id) {
        return contactPersonRepository.findById(id).map(this::toDTO);
    }

    public List<ContactPersonDTO> getAll() {
        return contactPersonRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ContactPersonDTO> getByName(String name) {
        return contactPersonRepository.findByName(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ContactPersonDTO> getByEmail(String email) {
        return contactPersonRepository.findByEmail(email).map(this::toDTO);
    }

    public Optional<ContactPersonDTO> update(Long id, ContactPersonDTO dto) {
        return contactPersonRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            return toDTO(contactPersonRepository.save(existing));
        });
    }

    public boolean delete(Long id) {
        if (contactPersonRepository.existsById(id)) {
            contactPersonRepository.deleteById(id);
            return true;
        }
        return false;
    }


    private ContactPersonDTO toDTO(ContactPerson cp) {
        return new ContactPersonDTO(cp.getId(), cp.getName(), cp.getEmail(), null);
    }
}
