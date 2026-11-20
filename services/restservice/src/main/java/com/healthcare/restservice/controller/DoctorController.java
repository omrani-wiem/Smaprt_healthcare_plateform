package com.healthcare.restservice.controller;

import com.healthcare.restservice.model.Doctor;
import com.healthcare.restservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Médecins", description = "Gestion des médecins")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository repository;

    @GetMapping
    public List<Doctor> list() {
        return repository.findAll();
    }
    

    @Operation (summary = "Consulter un médecin par ID")
    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
   
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@RequestBody Doctor doctor) {
        doctor.setId(null);
        return repository.save(doctor);
    }
}