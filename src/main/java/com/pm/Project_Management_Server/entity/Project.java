package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    @Enumerated(EnumType.STRING)
    private ProjectType type;
    private String department;
    //added new
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Double budget;

    public enum Status {
        ACTIVE, COMPLETED, ON_HOLD
    }
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

//    @OneToOne
//    @JoinColumn(name = "contact_person_id",nullable = true)
//    private ContactPerson contactPerson;

    @OneToOne
    @JoinColumn(name = "project_lead_id")
    private ProjectLead projectLead;

    @OneToOne
    @JoinColumn(name = "project_rate_card_id")
    private ProjectRateCard projectRateCard;

    public enum ProjectType {
        TNM, // Time and Material
        TFR  // Transfer
    }
}
