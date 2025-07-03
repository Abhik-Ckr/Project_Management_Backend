package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "project")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;

    @Enumerated(EnumType.STRING)
    private ResourceLevel level;

    private LocalDate startDate;
    private LocalDate endDate;

    private int exp;
    private boolean allocated;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
