package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.ContactPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactPersonRepository extends JpaRepository<ContactPerson, Long> {
    List<ContactPerson> findByName(String name);
    Optional<ContactPerson> findByEmail(String email);

}
