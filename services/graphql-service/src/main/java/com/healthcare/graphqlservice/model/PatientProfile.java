package com.healthcare.graphqlservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patient_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PatientProfile {
    @Id
    private Long id;                 // même identifiant que dans le service REST

    private String firstName;
    private String lastName;
    private int age;
    private String service;          // service hospitalier
    private String pathologie;

    @Enumerated(EnumType.STRING)
    private RiskLevel risque;
}