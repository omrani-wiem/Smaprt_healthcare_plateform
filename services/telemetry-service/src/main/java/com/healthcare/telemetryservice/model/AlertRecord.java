package com.healthcare.telemetryservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String type;
    private String severity;
    private String message;
    private int heartRate;
    private int spo2;
    private LocalDateTime createdAt;
}