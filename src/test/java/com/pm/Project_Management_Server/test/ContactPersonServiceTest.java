package com.pm.Project_Management_Server.test;

import com.pm.Project_Management_Server.dto.ContactPersonDTO;
import com.pm.Project_Management_Server.entity.ContactPerson;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.repositories.ContactPersonRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import com.pm.Project_Management_Server.services.ContactPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactPersonServiceTest {

    @Mock private ContactPersonRepository contactRepo;
    @Mock private ProjectRepository projectRepo;

    @InjectMocks private ContactPersonService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_WithProject() {
        Project project = Project.builder().id(10L).build();
        ContactPerson saved = ContactPerson.builder().id(1L).name("John").email("john@mail.com").project(project).build();

        ContactPersonDTO dto = new ContactPersonDTO(null, "John", "john@mail.com", null, 10L);

        when(projectRepo.findById(10L)).thenReturn(Optional.of(project));
        when(contactRepo.save(any())).thenReturn(saved);

        ContactPersonDTO result = service.create(dto);
        assertEquals("John", result.getName());
        assertEquals(10L, result.getProjectId());
    }

    @Test
    void testCreate_WithoutProject() {
        ContactPersonDTO dto = new ContactPersonDTO(null, "Jane", "jane@mail.com", null, null);
        ContactPerson saved = ContactPerson.builder().id(2L).name("Jane").email("jane@mail.com").build();

        when(contactRepo.save(any())).thenReturn(saved);

        ContactPersonDTO result = service.create(dto);
        assertEquals("Jane", result.getName());
        assertNull(result.getProjectId());
    }

    @Test
    void testGetById_Found() {
        ContactPerson cp = ContactPerson.builder().id(5L).name("Alice").email("alice@mail.com").build();
        when(contactRepo.findById(5L)).thenReturn(Optional.of(cp));

        Optional<ContactPersonDTO> result = service.getById(5L);
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void testGetById_NotFound() {
        when(contactRepo.findById(999L)).thenReturn(Optional.empty());

        Optional<ContactPersonDTO> result = service.getById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAll() {
        ContactPerson c1 = ContactPerson.builder().id(1L).name("A").build();
        ContactPerson c2 = ContactPerson.builder().id(2L).name("B").build();

        when(contactRepo.findAll()).thenReturn(List.of(c1, c2));

        List<ContactPersonDTO> result = service.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void testGetByName() {
        ContactPerson cp = ContactPerson.builder().id(1L).name("Shyam").email("shyam@mail.com").build();
        when(contactRepo.findByNameContainingIgnoreCase("shy")).thenReturn(List.of(cp));

        List<ContactPersonDTO> result = service.getByName("shy");
        assertEquals(1, result.size());
        assertEquals("Shyam", result.get(0).getName());
    }

    @Test
    void testUpdate_Success_WithProject() {
        ContactPerson existing = ContactPerson.builder().id(3L).name("Old").email("old@mail.com").build();
        Project newProject = Project.builder().id(42L).build();

        ContactPersonDTO dto = new ContactPersonDTO(3L, "New", "new@mail.com", null, 42L);

        when(contactRepo.findById(3L)).thenReturn(Optional.of(existing));
        when(projectRepo.findById(42L)).thenReturn(Optional.of(newProject));
        when(contactRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<ContactPersonDTO> result = service.update(3L, dto);
        assertTrue(result.isPresent());
        assertEquals("New", result.get().getName());
        assertEquals("new@mail.com", result.get().getEmail());
        assertEquals(42L, result.get().getProjectId());
    }

//    @Test
//    void testUpdate_NotFound() {
//        when(contactRepo.findById(99L)).thenReturn(Optional.empty());
//
//        Optional<ContactPersonDTO> result = service.update(99L, new ContactPersonDTO());
//        assertTrue(result.isEmpty());
//    }

    @Test
    void testDelete_Success() {
        when(contactRepo.existsById(4L)).thenReturn(true);

        boolean result = service.delete(4L);
        verify(contactRepo).deleteById(4L);
        assertTrue(result);
    }

    @Test
    void testDelete_NotFound() {
        when(contactRepo.existsById(8L)).thenReturn(false);

        boolean result = service.delete(8L);
        assertFalse(result);
    }
}
