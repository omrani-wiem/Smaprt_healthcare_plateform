package com.healthcare.restservice.dto;

import jakarta.validation.constraints.*;

public record PrescriptionRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotBlank String medication,
        @NotBlank String dosage,
        @Min(1) @Max(365) int durationDays,
        @Size(max = 255) String instructions
) {}