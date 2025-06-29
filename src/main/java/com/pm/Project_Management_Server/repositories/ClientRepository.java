package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    List<Client> findByLoyalty(boolean loyalty);

    List<Client> findByClientRatingGreaterThanEqual(int rating);

    List<Client> findByNameContainingIgnoreCase(String keyword);
}
