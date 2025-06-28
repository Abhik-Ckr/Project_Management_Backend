package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

// uncomment the below code after creating the project entity
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;

    private String resourceName;

    private LocalDate startDate;
    private LocalDate endDate;

    private int allocationPercentage = 100;

    private int exp;

    private boolean isAllocated;

}
