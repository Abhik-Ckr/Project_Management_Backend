package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Resource;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByProjectId(Long projectId);

    List<Resource> findByLevel(ResourceLevel level);

    List<Resource> findByAllocated(boolean allocated);
}
