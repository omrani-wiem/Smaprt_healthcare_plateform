package com.healthcare.telemetryservice.service;

import com.healthcare.telemetryservice.grpc.AlertEvent;
import com.healthcare.telemetryservice.grpc.VitalReading;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ThresholdDetector {

    public Optional<AlertEvent> detect(VitalReading r) {
        if (r.getFallDetected()) {
            return Optional.of(build(r, "CHUTE", "CRITICAL", "Chute détectée"));
        }
        if (r.getSpo2() > 0 && r.getSpo2() < 90) {
            return Optional.of(build(r, "DETRESSE_RESPIRATOIRE", "CRITICAL",
                    "Saturation en oxygène critique : " + r.getSpo2() + "%"));
        }
        if (r.getHeartRate() > 140) {
            return Optional.of(build(r, "TACHYCARDIE_SEVERE", "CRITICAL",
                    "Fréquence cardiaque critique : " + r.getHeartRate() + " bpm"));
        }
        if (r.getHeartRate() > 0 && r.getHeartRate() < 40) {
            return Optional.of(build(r, "BRADYCARDIE_SEVERE", "CRITICAL",
                    "Fréquence cardiaque trop basse : " + r.getHeartRate() + " bpm"));
        }
        return Optional.empty();
    }

    private AlertEvent build(VitalReading r, String type, String severity, String message) {
        return AlertEvent.newBuilder()
                .setPatientId(r.getPatientId())
                .setType(type)
                .setSeverity(severity)
                .setMessage(message)
                .setHeartRate(r.getHeartRate())
                .setSpo2(r.getSpo2())
                .setTimestampMs(System.currentTimeMillis())
                .build();
    }
}