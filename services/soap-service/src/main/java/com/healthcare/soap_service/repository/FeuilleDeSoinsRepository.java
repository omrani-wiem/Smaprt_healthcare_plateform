package com.healthcare.soap_service.repository;

import com.healthcare.soap_service.model.FeuilleDeSoins;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeuilleDeSoinsRepository extends JpaRepository<FeuilleDeSoins, Long> {
    List<FeuilleDeSoins> findByPatientIdOrderByDateActeDesc(Long patientId);
}
