package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
public class Highlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(length = 1000)
    private String description;
    private LocalDate createdOn;


}
