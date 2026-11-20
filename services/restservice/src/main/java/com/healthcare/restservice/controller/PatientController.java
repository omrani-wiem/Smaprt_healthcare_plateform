package com.healthcare.restservice.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.healthcare.restservice.model.Patient;
import com.healthcare.restservice.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



import java.util.List;

@Tag(name = "Patients", description = "Gestion des patients")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    

    private final PatientRepository repository;
    @Operation(summary = "Lister les patients")
    @GetMapping
    public List<Patient> list() {
        return repository.findAll();
    }

    @Operation(summary = "Consulter un patient par ID")
    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
    }
    
    @Operation(summary = "Créer un patient")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody Patient patient) {
        patient.setId(null);
        return repository.save(patient);
    }

    @Operation(summary = "Mettre à jour le profil d'un patient")
    @PutMapping("/{id}")
    public Patient updateProfile(@PathVariable Long id, @RequestBody Patient data) {
        Patient patient = get(id);
        patient.setFirstName(data.getFirstName());
        patient.setLastName(data.getLastName());
        patient.setEmail(data.getEmail());
        patient.setPhone(data.getPhone());
        patient.setAddress(data.getAddress());
        patient.setNumeroCNAM(data.getNumeroCNAM());
        return repository.save(patient);
    }
}