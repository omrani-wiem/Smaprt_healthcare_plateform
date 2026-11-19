package com.healthcare.graphqlservice.controller;

import com.healthcare.graphqlservice.model.*;
import com.healthcare.graphqlservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PatientProfileRepository patientRepository;
    private final VitalSignRepository vitalRepository;
    private final AlertRepository alertRepository;
    private final TreatmentRepository treatmentRepository;

    @QueryMapping
    public PatientProfile patient(@Argument Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<PatientProfile> patients(@Argument String service,
                                         @Argument String pathologie,
                                         @Argument RiskLevel risque) {
        Specification<PatientProfile> spec = (root, q, cb) -> cb.conjunction();
        if (service != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("service"), service));
        }
        if (pathologie != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("pathologie"), pathologie));
        }
        if (risque != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("risque"), risque));
        }
        return patientRepository.findAll(spec);
    }

    @SchemaMapping(typeName = "Patient", field = "vitals")
    public List<VitalSign> vitals(PatientProfile patient, @Argument int last) {
        return vitalRepository.findByPatientIdOrderByRecordedAtDesc(patient.getId(), PageRequest.of(0, last));
    }

    @SchemaMapping(typeName = "Patient", field = "alerts")
    public List<Alert> alerts(PatientProfile patient, @Argument int limit) {
        return alertRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId(), PageRequest.of(0, limit));
    }

    @SchemaMapping(typeName = "Patient", field = "treatments")
    public List<Treatment> treatments(PatientProfile patient) {
        return treatmentRepository.findByPatientId(patient.getId());
    }
}