package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ProjectRateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;

    @Enumerated(EnumType.STRING)
    private ResourceLevel level;
    private double hourlyRate;
    private boolean isActive;
    private LocalDateTime lastUpdated;

}
