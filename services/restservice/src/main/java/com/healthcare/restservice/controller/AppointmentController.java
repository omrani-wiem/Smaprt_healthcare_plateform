package com.healthcare.restservice.controller;

import com.healthcare.restservice.dto.AppointmentRequest;
import com.healthcare.restservice.dto.EmergencyAppointmentRequest;
import com.healthcare.restservice.model.Appointment;
import com.healthcare.restservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping
    public List<Appointment> list(@RequestParam(required = false) Long patientId) {
        return patientId != null ? service.findByPatient(patientId) : service.findAll();
    }

    @GetMapping("/{id}")
    public Appointment get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(@Valid @RequestBody AppointmentRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Appointment update(@PathVariable Long id,
                              @Valid @RequestBody AppointmentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }

        @PostMapping("/emergency")
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment emergency(@Valid @RequestBody EmergencyAppointmentRequest request) {
        return service.createEmergency(request);
    }
}