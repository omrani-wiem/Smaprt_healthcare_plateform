package com.healthcare.graphqlservice.config;

import com.healthcare.graphqlservice.model.*;
import com.healthcare.graphqlservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PatientProfileRepository patients;
    private final VitalSignRepository vitals;
    private final AlertRepository alerts;
    private final TreatmentRepository treatments;

    @Override
    public void run(String... args) {
        if (patients.count() > 0) return;

        patients.save(new PatientProfile(1L, "Ahmed", "Trabelsi", 62, "Cardiologie", "Insuffisance cardiaque", RiskLevel.HIGH));
        patients.save(new PatientProfile(2L, "Leila", "Mansour", 45, "Pneumologie", "Asthme sévère", RiskLevel.MEDIUM));
        patients.save(new PatientProfile(3L, "Karim", "Jlassi", 71, "Cardiologie", "Arythmie", RiskLevel.CRITICAL));

        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 6; i++) {
            vitals.save(VitalSign.builder().patientId(1L).heartRate(80 + i * 3).spo2(97 - i / 2)
                    .temperature(36.8 + i * 0.1).systolic(130 + i).diastolic(85)
                    .recordedAt(now.minusMinutes(i * 10L)).build());
        }
        vitals.save(VitalSign.builder().patientId(2L).heartRate(95).spo2(91).temperature(37.2)
                .systolic(120).diastolic(80).recordedAt(now).build());
        vitals.save(VitalSign.builder().patientId(3L).heartRate(140).spo2(93).temperature(37.0)
                .systolic(150).diastolic(95).recordedAt(now).build());

        alerts.save(Alert.builder().patientId(1L).type("TACHYCARDIE").severity("HIGH")
                .message("Fréquence cardiaque élevée").createdAt(now.minusMinutes(5)).build());
        alerts.save(Alert.builder().patientId(3L).type("ARYTHMIE").severity("CRITICAL")
                .message("Rythme irrégulier détecté").createdAt(now.minusMinutes(2)).build());

        treatments.save(Treatment.builder().patientId(1L).medication("Bisoprolol").dosage("5 mg/jour")
                .startDate(LocalDate.of(2026, 9, 1)).build());
        treatments.save(Treatment.builder().patientId(2L).medication("Salbutamol").dosage("2 bouffées si besoin")
                .startDate(LocalDate.of(2026, 10, 15)).build());
    }
}