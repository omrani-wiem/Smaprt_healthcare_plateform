
package com.healthcare.restservice.controller;

import com.healthcare.restservice.dto.AppointmentRequest;
import com.healthcare.restservice.dto.EmergencyAppointmentRequest;
import com.healthcare.restservice.model.Appointment;
import com.healthcare.restservice.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
    name = "Rendez-vous",
    description = "Prise, modification et annulation de rendez-vous"
)
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    // Réserver un rendez-vous
    @Operation(summary = "Réserver un rendez-vous")
    @ApiResponse(responseCode = "201", description = "Rendez-vous créé")
    @ApiResponse(responseCode = "409", description = "Créneau déjà réservé")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment create(
            @Valid @RequestBody AppointmentRequest request) {
        return service.create(request);
    }

    // Réserver un rendez-vous d'urgence
    @Operation(
        summary = "Réserver un rendez-vous d'urgence",
        description = "Appelé par l'orchestrateur"
    )
    @PostMapping("/emergency")
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment emergency(
            @Valid @RequestBody EmergencyAppointmentRequest request) {
        return service.createEmergency(request);
    }

    // Consulter les rendez-vous
    @GetMapping
    public List<Appointment> list(
            @RequestParam(required = false) Long patientId) {
        return patientId != null
                ? service.findByPatient(patientId)
                : service.findAll();
    }

    // Consulter un rendez-vous par ID
    @GetMapping("/{id}")
    public Appointment get(@PathVariable Long id) {
        return service.findById(id);
    }

    // Modifier un rendez-vous
    @PutMapping("/{id}")
    public Appointment update(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {
        return service.update(id, request);
    }

    // Annuler un rendez-vous
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}
