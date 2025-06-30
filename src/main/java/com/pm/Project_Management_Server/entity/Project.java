package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> Resources;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Highlight> Highlights;



    @OneToOne
    @JoinColumn(name = "project_rate_card_id")
    private ProjectRateCard projectRateCard;

    private Double budget;


    @ManyToOne
    @JoinColumn(name = "contact_person_id")
    private ContactPerson contactPerson;


   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Issue> issues;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private ProjectLead projectLead;



    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;


    public enum Status {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        ON_HOLD,
        CANCELLED
    }
}
