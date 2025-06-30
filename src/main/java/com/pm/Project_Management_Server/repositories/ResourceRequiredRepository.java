package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.ResourceRequired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRequiredRepository extends JpaRepository<ResourceRequired, Long> {
    List<ResourceRequired> findAllByProjectId(Long projectId);
} 