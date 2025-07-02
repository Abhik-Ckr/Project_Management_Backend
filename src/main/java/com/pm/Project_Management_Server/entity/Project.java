package com.pm.Project_Management_Server.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resource> resources;
    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Highlight> highlights;

    @OneToOne
    @JoinColumn(name = "project_rate_card_id")
    private ProjectRateCard projectRateCard;

    private Double budget;

    @OneToOne
    @JoinColumn(name = "contact_person_id")
    private ContactPerson contactPerson;

    @JsonManagedReference
   @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Issue> issues;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private ProjectLead projectLead;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpenPosition> openPositions;

    public void addResource(Resource resource) {
        resources.add(resource);
        resource.setProject(this); // maintain bidirectional integrity
    }

//    @OneToOne
//    @JoinColumn(name = "contract_id")
//    private Contract contract;

    public enum Status {
        OPEN,
        IN_PROGRESS,
        COMPLETED,
        ON_HOLD,
        CANCELLED
    }
}
