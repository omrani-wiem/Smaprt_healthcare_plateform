package com.healthcare.telemetryservice.event;

public record AlertRaisedEvent(
        long patientId,
        String type,
        String severity,
        String message,
        int heartRate,
        int spo2,
        long timestampMs
) {}