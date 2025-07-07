package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "project")
public class ResourceRequired {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResourceLevel resourceLevel;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


}
