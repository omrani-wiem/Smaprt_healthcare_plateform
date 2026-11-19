package com.healthcare.orchestratorservice.service;

import com.healthcare.orchestratorservice.client.CnamSoapClient;
import com.healthcare.orchestratorservice.client.CnamSoapClient.Coverage;
import com.healthcare.orchestratorservice.client.DashboardGraphQlClient;
import com.healthcare.orchestratorservice.client.RestPortalClient;
import com.healthcare.orchestratorservice.event.AlertRaisedEvent;
import com.healthcare.orchestratorservice.model.Intervention;
import com.healthcare.orchestratorservice.repository.InterventionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterventionWorkflow {

    private static final Duration COOLDOWN = Duration.ofMinutes(2);
    private static final String EMERGENCY_ACT = "CS";

    private final Map<Long, Instant> lastHandled = new ConcurrentHashMap<>();

    private final RestPortalClient portal;
    private final CnamSoapClient cnam;
    private final DashboardGraphQlClient dashboard;
    private final InterventionRepository repository;

    public void handle(AlertRaisedEvent event) {
        // Garde anti-doublon : une crise = 1 intervention, pas 10
        Instant last = lastHandled.get(event.patientId());
        if (last != null && Instant.now().isBefore(last.plus(COOLDOWN))) {
            log.info("Alerte ignorée (intervention déjà en cours) pour le patient {}", event.patientId());
            return;
        }
        log.info("Début de prise en charge : {} pour le patient {}", event.type(), event.patientId());

        // 1. Dossier patient (REST) : une erreur ici fait échouer le message -> nouvelle tentative, puis DLQ
        Map<?, ?> patient = portal.getPatient(event.patientId());
        String numeroCnam = (String) patient.get("numeroCNAM");

        // 2. Droits CNAM (SOAP) : jamais bloquant pour une urgence
        Coverage coverage = checkCoverage(event.patientId(), numeroCnam);

        // 3. Rendez-vous d'urgence (REST)
        Map<?, ?> appointment = portal.bookEmergency(event.patientId(),
                specialtyFor(event.type()), event.type() + " - " + event.message());
        Long appointmentId = ((Number) appointment.get("id")).longValue();

        // 4. On enregistre l'intervention ; la fiche GraphQL est assemblée à la demande
        Intervention saved = repository.save(Intervention.builder()
                .patientId(event.patientId())
                .alertType(event.type())
                .severity(event.severity())
                .alertMessage(event.message())
                .coverageOpen(coverage.open())
                .coverageRate(coverage.rate())
                .coverageMessage(coverage.reason())
                .appointmentId(appointmentId)
                .createdAt(LocalDateTime.now())
                .build());
        lastHandled.put(event.patientId(), Instant.now());
        log.info("Intervention {} créée (rendez-vous {}, couverture : {})",
                saved.getId(), appointmentId, coverage.reason());
    }

        public Map<String, Object> buildSheet(Intervention intervention) {
        Map<?, ?> patient = dashboard.patientSheet(intervention.getPatientId());
        Map<String, Object> sheet = new LinkedHashMap<>();
        sheet.put("intervention", intervention);
        sheet.put("patient", patient);
        sheet.put("degraded", patient == null);   // le dossier détaillé est indisponible
        return sheet;
    }

    private Coverage checkCoverage(Long patientId, String numeroCnam) {
        if (numeroCnam == null || numeroCnam.isBlank()) {
            return new Coverage(false, 0, "Numéro CNAM absent du dossier");
        }
        try {
            return cnam.verify(patientId, numeroCnam, EMERGENCY_ACT);
        } catch (Exception e) {
            log.warn("Vérification CNAM impossible : {}", e.getMessage());
            return new Coverage(false, 0, "Vérification CNAM indisponible");
        }
    }

    private String specialtyFor(String alertType) {
        return switch (alertType) {
            case "TACHYCARDIE_SEVERE", "BRADYCARDIE_SEVERE" -> "Cardiologie";
            case "DETRESSE_RESPIRATOIRE" -> "Pneumologie";
            default -> null;
        };
    }
}