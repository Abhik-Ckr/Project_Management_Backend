package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.ResourceAllocated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAllocatedRepository extends JpaRepository<ResourceAllocated, Long> {
    List<ResourceAllocated> findByProjectId(Long projectId);
    List<ResourceAllocated> findByResourceId(Long resourceId);
    List<ResourceAllocated> findByProjectIdAndEndDateIsNull(Long projectId);

    Optional<ResourceAllocated> findTopByResourceIdAndEndDateIsNullOrderByStartDateDesc(Long resourceId);
}

