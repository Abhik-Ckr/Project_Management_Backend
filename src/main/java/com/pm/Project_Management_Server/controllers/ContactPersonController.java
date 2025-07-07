package com.pm.Project_Management_Server.controllers;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.services.ContactPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact-persons")
@RequiredArgsConstructor
public class ContactPersonController {

    private final ContactPersonService contactPersonService;

    @GetMapping("/{id}")
    public ResponseEntity<ContactPersonDTO> getById(@PathVariable Long id) {
        return contactPersonService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContactPersonDTO> create(@RequestBody ContactPersonDTO dto) {
        return ResponseEntity.ok(contactPersonService.create(dto));
    }

    @GetMapping
    public List<ContactPersonDTO> getAll() {
        return contactPersonService.getAll();
    }

    @GetMapping("/name/{name}")
    public List<ContactPersonDTO> getByName(@PathVariable String name) {
        return contactPersonService.getByName(name);
    }



    @PutMapping("/{id}")
    public ResponseEntity<ContactPersonDTO> update(@PathVariable Long id, @RequestBody ContactPersonDTO dto) {
        return contactPersonService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (contactPersonService.delete(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
