package com.healthcare.restservice.dto;

import jakarta.validation.constraints.NotNull;

public record EmergencyAppointmentRequest(
        @NotNull Long patientId,
        String specialty,
        String reason
) {}