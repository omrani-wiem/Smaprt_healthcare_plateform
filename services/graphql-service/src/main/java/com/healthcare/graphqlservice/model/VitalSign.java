package com.healthcare.graphqlservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VitalSign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private int heartRate;
    private int spo2;
    private double temperature;
    private int systolic;
    private int diastolic;
    private LocalDateTime recordedAt;
}