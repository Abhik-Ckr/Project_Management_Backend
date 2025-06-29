package com.pm.Project_Management_Server.repositories;


import com.pm.Project_Management_Server.entity.ProjectRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRateCardRepository extends JpaRepository<ProjectRateCard, Long> {

    Optional<ProjectRateCard> findByProject_IdAndLevel(Long projectId,  ResourceLevel level);

    boolean existsByProject_IdAndLevel(Long projectId, ResourceLevel level);

    Optional<ProjectRateCard> findByProjectIdAndLevel(Long id, ResourceLevel level);
}
