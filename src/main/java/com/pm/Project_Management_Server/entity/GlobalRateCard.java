package com.pm.Project_Management_Server.entity;

import jakarta.persistence.*;

@Entity
public class GlobalRateCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private ResourceLevel level;
    //same or different level here?

    private double hourlyRate;

}
