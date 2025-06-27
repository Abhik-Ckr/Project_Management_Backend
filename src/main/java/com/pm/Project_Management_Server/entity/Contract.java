package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;

@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int duration; //in months
    private double amountQuoted;

//    @OneToOne(mappedBy = "contract")
//    private Project project; will take it from Project Class -> SANTOSH
}
