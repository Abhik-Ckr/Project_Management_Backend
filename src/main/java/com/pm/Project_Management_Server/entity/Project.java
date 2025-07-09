package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String type;
    private String department;

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
}
