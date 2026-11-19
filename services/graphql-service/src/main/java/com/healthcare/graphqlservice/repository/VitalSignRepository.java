package com.healthcare.graphqlservice.repository;

import com.healthcare.graphqlservice.model.VitalSign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByPatientIdOrderByRecordedAtDesc(Long patientId, Pageable pageable);
}