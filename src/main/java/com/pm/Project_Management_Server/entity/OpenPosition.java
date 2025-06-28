package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;

public class OpenPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceLevel level;

    private int numberRequired;

    @ManyToMany
    private Project project;
}
