package com.pm.Project_Management_Server.Repositories;


import com.pm.Project_Management_Server.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByClientId(Long clientId);

    List<Project> findByStatus(Project.Status status);

    List<Project> findByProjectLead_Id(Long projectLeadId);

    List<Project> findByDepartment(String department);
}
