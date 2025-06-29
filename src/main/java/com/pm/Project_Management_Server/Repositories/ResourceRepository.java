package com.pm.Project_Management_Server.Repositories;

import com.pm.Project_Management_Server.entity.Resource;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // Get all resources assigned to a specific project
    List<Resource> findByProjectId(Long projectId);

    // Get resources by level
    List<Resource> findByLevel(ResourceLevel level);

    // Get unallocated resources
    List<Resource> findByAllocatedFalse();

    // Get allocated resources
    List<Resource> findByAllocatedTrue();

}
