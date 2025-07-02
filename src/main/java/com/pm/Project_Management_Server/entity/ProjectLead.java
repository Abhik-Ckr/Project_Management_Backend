package com.pm.Project_Management_Server.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // One Project Lead is linked to one User (1:1)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    // One Project Lead is assigned to one Project (1:1)
    @OneToOne
    @JoinColumn(name = "project_id",  unique = true)
    private Project project;
}
