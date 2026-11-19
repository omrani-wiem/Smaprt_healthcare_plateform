package com.healthcare.orchestratorservice.client;

import com.healthcare.orchestratorservice.service.Resilient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class DashboardGraphQlClient {

    private static final String QUERY = """
            query($id: ID!) {
              patient(id: $id) {
                firstName lastName age service pathologie risque
                vitals(last: 3) { heartRate spo2 temperature recordedAt }
                alerts(limit: 3) { type severity message createdAt }
                treatments { medication dosage }
              }
            }""";

    private final RestClient rest;
    private final Resilient resilient;

    public DashboardGraphQlClient(@LoadBalanced RestClient.Builder builder, Resilient resilient) {
        this.rest = builder.baseUrl("http://graphql-service").build();
        this.resilient = resilient;
    }

    public Map<?, ?> patientSheet(Long patientId) {
        return resilient.call("graphql-service", () -> {
            Map<?, ?> response = rest.post().uri("/graphql")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("query", QUERY, "variables", Map.of("id", String.valueOf(patientId))))
                    .retrieve().body(Map.class);
            if (response == null || !(response.get("data") instanceof Map<?, ?> data)) {
                return null;
            }
            return (Map<?, ?>) data.get("patient");
        }, () -> null);
    }
}