package com.pm.Project_Management_Server.repositories;


import com.pm.Project_Management_Server.entity.ProjectLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectLeadRepository extends JpaRepository<ProjectLead, Long> {

    Optional<ProjectLead> findByUser_Id(Long userId);

    Optional<ProjectLead> findByProject_Id(Long projectId);

    Optional<ProjectLead> findByProjectId(Long projectId);
}

