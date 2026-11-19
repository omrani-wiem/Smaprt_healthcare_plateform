package com.healthcare.soap_service.repository;

import com.healthcare.soap_service.model.Assure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AssureRepository extends JpaRepository<Assure, Long> {
    Optional<Assure> findByPatientIdAndNumeroCNAM(Long patientId, String numeroCNAM);
}