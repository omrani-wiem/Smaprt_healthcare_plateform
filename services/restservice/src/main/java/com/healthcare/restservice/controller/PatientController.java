package com.healthcare.restservice.controller;

import com.healthcare.restservice.model.Patient;
import com.healthcare.restservice.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository repository;

    @GetMapping
    public List<Patient> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody Patient patient) {
        patient.setId(null);
        return repository.save(patient);
    }

    @PutMapping("/{id}")
    public Patient updateProfile(@PathVariable Long id, @RequestBody Patient data) {
        Patient patient = get(id);
        patient.setFirstName(data.getFirstName());
        patient.setLastName(data.getLastName());
        patient.setEmail(data.getEmail());
        patient.setPhone(data.getPhone());
        patient.setAddress(data.getAddress());
        return repository.save(patient);
    }
}