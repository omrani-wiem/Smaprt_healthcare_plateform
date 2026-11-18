package main.java.com.healthcare.restservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull @Future LocalDateTime dateTime,
        @Size(max = 255) String reason
) {}