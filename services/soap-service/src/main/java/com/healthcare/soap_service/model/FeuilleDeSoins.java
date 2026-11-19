package com.healthcare.soap_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "feuilles_de_soins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeuilleDeSoins {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numeroFeuille;

    private Long patientId;
    private String codeActe;
    private LocalDate dateActe;

    @Column(precision = 12, scale = 3)
    private BigDecimal montant;

    @Column(precision = 12, scale = 3)
    private BigDecimal montantRembourse;
}