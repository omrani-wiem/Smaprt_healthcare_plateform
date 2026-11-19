package com.healthcare.soap_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Table(name = "assures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
public class Assure {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;


    @Column(nullable = false, unique = true)
    private String numeroCNAM;
    private boolean droitsActifs;
    private LocalDate dateFinDroits;
 
}
