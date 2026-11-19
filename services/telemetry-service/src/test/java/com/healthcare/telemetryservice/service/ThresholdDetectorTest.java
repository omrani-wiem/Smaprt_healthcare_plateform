package com.healthcare.telemetryservice.service;

import com.healthcare.telemetryservice.grpc.VitalReading;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdDetectorTest {

    private final ThresholdDetector detector = new ThresholdDetector();

    private VitalReading.Builder normal() {
        return VitalReading.newBuilder().setPatientId(1).setHeartRate(80).setSpo2(97).setTemperature(36.8);
    }

    @Test
    void normalReadingRaisesNoAlert() {
        assertTrue(detector.detect(normal().build()).isEmpty());
    }

    @Test
    void lowOxygenIsRespiratoryDistress() {
        var alert = detector.detect(normal().setSpo2(88).build());
        assertEquals("DETRESSE_RESPIRATOIRE", alert.orElseThrow().getType());
    }

    @Test
    void highHeartRateIsSevereTachycardia() {
        var alert = detector.detect(normal().setHeartRate(170).build());
        assertEquals("TACHYCARDIE_SEVERE", alert.orElseThrow().getType());
    }

    @Test
    void fallIsCritical() {
        var alert = detector.detect(normal().setFallDetected(true).build());
        assertEquals("CHUTE", alert.orElseThrow().getType());
        assertEquals("CRITICAL", alert.orElseThrow().getSeverity());
    }
}