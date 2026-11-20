package com.healthcare.restservice.controller;

import com.healthcare.restservice.dto.PrescriptionRequest;
import com.healthcare.restservice.model.Prescription;
import com.healthcare.restservice.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import java.time.LocalDate;
import java.util.List;

@Tag(name = "Ordonnances", description = "Gestion des ordonnances")
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionRepository prescriptions;
    private final PatientRepository patients;
    private final DoctorRepository doctors;


    @Operation(summary = "Consulter l'historique des ordonnances d'un patient")
    @GetMapping
    public List<Prescription> history(@RequestParam Long patientId) {
        return prescriptions.findByPatientIdOrderByIssuedOnDesc(patientId);
    }

    @Operation(summary = "Émettre une ordonnance")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prescription issue(@Valid @RequestBody PrescriptionRequest r) {
        var patient = patients.findById(r.patientId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
        var doctor = doctors.findById(r.doctorId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Médecin introuvable"));
        return prescriptions.save(Prescription.builder()
                .patient(patient).doctor(doctor)
                .medication(r.medication()).dosage(r.dosage())
                .durationDays(r.durationDays()).instructions(r.instructions())
                .issuedOn(LocalDate.now()).build());
    }
}