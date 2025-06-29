package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.OpenPosition;

import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenPositionRepository extends JpaRepository<OpenPosition, Long> {

    List<OpenPosition> findByProjectId(Long projectId);

    List<OpenPosition> findByLevel(ResourceLevel level);

    List<OpenPosition> findByNumberRequiredGreaterThan(int count);
}
