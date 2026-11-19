package com.healthcare.graphqlservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private String type;
    private String severity;
    private String message;
    private LocalDateTime createdAt;
}