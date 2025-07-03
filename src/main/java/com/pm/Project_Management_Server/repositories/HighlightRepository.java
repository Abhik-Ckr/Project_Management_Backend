package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    // Get highlights for a specific project
    List<Highlight> findByProjectId(Long projectId);
}
