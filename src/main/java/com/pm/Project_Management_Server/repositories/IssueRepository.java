package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findAllByProject_Id(Long projectId);
    List<Issue> findByProject_IdAndSeverity(Long projectId, Issue.Severity severity);
}
