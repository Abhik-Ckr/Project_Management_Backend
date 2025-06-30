package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
    private String duration;

    @OneToOne
    @JoinColumn(name = "resouce_req_id")
    private ResourceRequired resourceRequired;
    private Double amountQuoted;
}
