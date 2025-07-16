package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.ProjectRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRateCardRepository extends JpaRepository<ProjectRateCard, Long> {

    List<ProjectRateCard> findByProjectId(Long projectId);

    Optional<ProjectRateCard> findByProjectIdAndLevel(Long projectId, ResourceLevel level);

    List<ProjectRateCard> findByProjectIdAndActiveTrue(Long projectId);

    List<ProjectRateCard> findByProjectIdAndLevelAndActiveTrue(Long projectId, ResourceLevel level);
}
