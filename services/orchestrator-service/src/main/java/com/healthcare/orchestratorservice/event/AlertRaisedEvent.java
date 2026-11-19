package com.healthcare.orchestratorservice.event;

public record AlertRaisedEvent(
        long patientId,
        String type,
        String severity,
        String message,
        int heartRate,
        int spo2,
        long timestampMs
) {}