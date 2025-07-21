package com.pm.Project_Management_Server.repositories;


import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ProjectLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectLeadRepository extends JpaRepository<ProjectLead, Long> {

    Optional<ProjectLead> findByUser_Id(Long userId);
    List<ProjectLead> findByEndDateIsNull(); // active project leads


    Optional<ProjectLead> findByProjectAndEndDateIsNull(Project project);

    List<ProjectLead> findByProjectId(Long projectId);


}

