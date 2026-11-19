package com.healthcare.soap_service.service;

import com.healthcare.soap_service.model.*;
import com.healthcare.soap_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CnamService {

    private final AssureRepository assureRepository;
    private final ActeMedicalRepository acteRepository;
    private final FeuilleDeSoinsRepository feuilleRepository;

    public record Verification(boolean droitsOuverts, int taux, String motif) {}

    public Verification verifierDroits(Long patientId, String numeroCNAM, String codeActe) {
        var assure = assureRepository.findByPatientIdAndNumeroCNAM(patientId, numeroCNAM);
        if (assure.isEmpty()) {
            return new Verification(false, 0, "Assuré introuvable");
        }
        Assure a = assure.get();
        if (!a.isDroitsActifs() || a.getDateFinDroits().isBefore(LocalDate.now())) {
            return new Verification(false, 0, "Droits expirés ou inactifs");
        }
        var acte = acteRepository.findById(codeActe);
        if (acte.isEmpty()) {
            return new Verification(false, 0, "Acte inconnu : " + codeActe);
        }
        return new Verification(true, acte.get().getTauxRemboursement(), "Droits ouverts");
    }

    public FeuilleDeSoins emettreFeuille(Long patientId, String numeroCNAM, String codeActe,
                                         BigDecimal montant, LocalDate dateActe) {
        Verification v = verifierDroits(patientId, numeroCNAM, codeActe);
        if (!v.droitsOuverts()) {
            throw new DroitsException("Feuille refusée : " + v.motif());
        }
        BigDecimal rembourse = montant
                .multiply(BigDecimal.valueOf(v.taux()))
                .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        FeuilleDeSoins feuille = FeuilleDeSoins.builder()
                .numeroFeuille("FS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .patientId(patientId)
                .codeActe(codeActe)
                .dateActe(dateActe)
                .montant(montant)
                .montantRembourse(rembourse)
                .build();
        return feuilleRepository.save(feuille);
    }

    public List<FeuilleDeSoins> historique(Long patientId) {
        return feuilleRepository.findByPatientIdOrderByDateActeDesc(patientId);
    }
}