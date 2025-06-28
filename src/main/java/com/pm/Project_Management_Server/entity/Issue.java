package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(length = 2000)
    private String description;

    private String createdBy;

    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    // Relation to Project
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public enum Severity {
        HIGH, URGENT, MEDIUM, LOW
    }

    public enum IssueStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
