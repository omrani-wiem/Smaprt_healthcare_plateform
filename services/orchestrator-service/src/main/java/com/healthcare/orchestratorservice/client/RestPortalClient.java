package com.healthcare.orchestratorservice.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class RestPortalClient {

    public record EmergencyRequest(Long patientId, String specialty, String reason) {}

    private final RestClient rest;

    public RestPortalClient(@LoadBalanced RestClient.Builder builder) {
        this.rest = builder.baseUrl("http://rest-service").build();
    }

    public Map<?, ?> getPatient(Long id) {
        return rest.get().uri("/api/patients/{id}", id).retrieve().body(Map.class);
    }

    public Map<?, ?> bookEmergency(Long patientId, String specialty, String reason) {
        return rest.post().uri("/api/appointments/emergency")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmergencyRequest(patientId, specialty, reason))
                .retrieve().body(Map.class);
    }
}