package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Issue;
import com.pm.Project_Management_Server.entity.Issue.IssueStatus;
import com.pm.Project_Management_Server.entity.Issue.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    // Get all issues by project
    List<Issue> findByProjectId(Long projectId);

    // Filter by status
    List<Issue> findByStatus(IssueStatus status);


    // Filter by severity
    List<Issue> findBySeverity(Severity severity);
}
