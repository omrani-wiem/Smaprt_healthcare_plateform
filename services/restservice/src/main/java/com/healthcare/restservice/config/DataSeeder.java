package com.healthcare.restservice.config;

import com.healthcare.restservice.model.*;
import com.healthcare.restservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final PrescriptionRepository prescriptions;

    @Override
    public void run(String... args) {
        if (doctors.count() == 0) {
            doctors.save(Doctor.builder().firstName("Sami").lastName("Ben Ali").specialty("Cardiologie").build());
            doctors.save(Doctor.builder().firstName("Nadia").lastName("Gharbi").specialty("Pneumologie").build());
            doctors.save(Doctor.builder().firstName("Youssef").lastName("Hamdi").specialty("Médecine générale").build());
        }
        if (patients.count() == 0) {
            // ids 1 et 2 : identiques à ceux des services SOAP et GraphQL.
            // Le patient 3 (Karim) n'existe volontairement pas ici : il sert au test de la DLQ.
            var ahmed = patients.save(Patient.builder().firstName("Ahmed").lastName("Trabelsi")
                    .email("ahmed@mail.com").phone("20123456").numeroCNAM("12345678").build());
            patients.save(Patient.builder().firstName("Leila").lastName("Mansour")
                    .email("leila@mail.com").phone("20999888").numeroCNAM("87654321").build());

            var cardio = doctors.findAll().get(0);
            prescriptions.save(Prescription.builder().patient(ahmed).doctor(cardio)
                    .medication("Bisoprolol").dosage("5 mg, 1 fois par jour").durationDays(90)
                    .instructions("Le matin, pendant le repas").issuedOn(LocalDate.now().minusDays(30)).build());
        }
    }
}