package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


// uncomment the below code after creating the project entity
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

