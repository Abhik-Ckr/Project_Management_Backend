package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Client;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.Project.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find project by exact name
    Optional<Project> findByProjectName(String projectName);

    // Search projects with partial match (case-insensitive)
    List<Project> findByProjectNameContainingIgnoreCase(String name);
    @Query("SELECT p.client FROM Project p WHERE p.id = :projectId")
    Optional<Client> findClientByProjectId(@Param("projectId") Long projectId);

    // Find all projects under a specific client
    List<Project> findByClientId(Long clientId);
    Optional<Project> findByProjectLeadId(Long projectLeadId);

    // Find projects by status
    List<Project> findByStatus(Status status);


}
