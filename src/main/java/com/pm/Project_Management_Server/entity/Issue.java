package com.pm.Project_Management_Server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "project")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(length = 2000)
    private String description;

    private String createdBy;

    private LocalDate createdDate;

    private LocalDate updatedDate;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    public enum Severity {
        HIGH, URGENT, MEDIUM, LOW
    }

    public enum IssueStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDate.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDate.now();
    }
}
