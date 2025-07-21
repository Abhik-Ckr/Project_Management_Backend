package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String name;
    private String email;

    @Column(nullable = false)
    private LocalDate startDate;
    private LocalDate endDate; // null if ongoing
}

