package com.healthcare.soap_service.config;


import com.healthcare.soap_service.model.*;
import com.healthcare.soap_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDate;

@Component 
@RequiredArgsConstructor


public class DataSeeder implements CommandLineRunner{
    private final AssureRepository assureRepository;
    private final ActeMedicalRepository acteRepository;

    @Override
    public void run(String... args) {
        if (acteRepository.count() == 0) {
            acteRepository.save(new ActeMedical("C", "Consultation généraliste", new BigDecimal("30.000"), 80));
            acteRepository.save(new ActeMedical("CS", "Consultation spécialiste", new BigDecimal("50.000"), 80));
            acteRepository.save(new ActeMedical("K", "Acte chirurgical courant", new BigDecimal("200.000"), 50));
            acteRepository.save(new ActeMedical("B", "Analyse biologique", new BigDecimal("40.000"), 50));
        }
        if (assureRepository.count() == 0) {
            assureRepository.save(Assure.builder().patientId(1L).numeroCNAM("12345678")
                    .droitsActifs(true).dateFinDroits(LocalDate.of(2027, 12, 31)).build());
            assureRepository.save(Assure.builder().patientId(2L).numeroCNAM("87654321")
                    .droitsActifs(false).dateFinDroits(LocalDate.of(2025, 6, 30)).build());
        }
    }
}
