package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
@Data
@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String resourceName;
    @Enumerated(EnumType.STRING)
    private ResourceLevel level;


    private LocalDate startDate;

    private LocalDate endDate;



    private int exp;

    private boolean isAllocated;


    }

