package com.healthcare.soap_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "actes_medicaux")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActeMedical {
    @Id
    private String code;             // lettre-clé : C, CS, K, B...
    private String libelle;
    private BigDecimal tarif;
    private int tauxRemboursement;   // 80 ou 50
}