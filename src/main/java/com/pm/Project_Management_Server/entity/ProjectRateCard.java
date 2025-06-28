package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class ProjectRateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private ResourceLevel level;
    private double Rate;
    private boolean isActive;
    private LocalDateTime lastUpdated;


    public Boolean getIsActive() {
        return isActive;
    }

}
