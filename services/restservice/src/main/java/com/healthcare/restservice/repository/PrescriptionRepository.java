package com.healthcare.restservice.repository;

import com.healthcare.restservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientIdOrderByIssuedOnDesc(Long patientId);
}