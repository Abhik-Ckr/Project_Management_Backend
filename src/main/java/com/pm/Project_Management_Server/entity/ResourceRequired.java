package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ResourceRequired {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceLevel resourceLevel;

    private String expRange;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
