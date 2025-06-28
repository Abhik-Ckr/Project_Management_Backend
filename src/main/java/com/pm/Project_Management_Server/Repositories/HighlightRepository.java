package com.pm.Project_Management_Server.Repositories;



import com.pm.Project_Management_Server.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    // Optional: Get highlights by project
    List<Highlight> findByProjectId(Long projectId);
}
