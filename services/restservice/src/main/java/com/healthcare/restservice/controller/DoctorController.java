package com.healthcare.restservice.controller;

import com.healthcare.restservice.model.Doctor;
import com.healthcare.restservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository repository;

    @GetMapping
    public List<Doctor> list() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@RequestBody Doctor doctor) {
        doctor.setId(null);
        return repository.save(doctor);
    }
}