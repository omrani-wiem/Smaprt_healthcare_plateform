package com.healthcare.orchestratorservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String alertType;
    private String severity;
    private String alertMessage;

    private boolean coverageOpen;
    private int coverageRate;
    private String coverageMessage;

    private Long appointmentId;
    private LocalDateTime createdAt;
}