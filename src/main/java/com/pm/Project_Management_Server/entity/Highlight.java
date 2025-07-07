package com.pm.Project_Management_Server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "project")
public class Highlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    @Column(length = 1000)
    private String description;

    private LocalDate createdOn;

    @PrePersist
    public void setCreatedOn() {
        this.createdOn = LocalDate.now();
    }
}
