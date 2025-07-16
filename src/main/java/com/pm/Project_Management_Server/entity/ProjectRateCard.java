package com.pm.Project_Management_Server.entity;

import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ProjectRateCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @Enumerated(EnumType.STRING)
    private ResourceLevel level;
    private double rate;
    private boolean active;
    private LocalDate startDate;  // 📌 NEW
    private LocalDate endDate;    // 📌 NEW



    @PrePersist
    public void setDefaultDates() {
        if (this.startDate == null) this.startDate = LocalDate.now();
    }
    @PreUpdate
    public void deactivateIfExpired() {
        if (this.endDate != null && this.endDate.isBefore(LocalDate.now())) {
            this.active = false;
        }}
    public boolean getActive() { return active;
    }
}
