package com.healthcare.graphqlservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "treatments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String medication;
    private String dosage;
    private LocalDate startDate;
}