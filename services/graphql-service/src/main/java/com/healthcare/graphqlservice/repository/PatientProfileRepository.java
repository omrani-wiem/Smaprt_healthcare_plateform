package com.healthcare.graphqlservice.repository;

import com.healthcare.graphqlservice.model.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PatientProfileRepository
        extends JpaRepository<PatientProfile, Long>, JpaSpecificationExecutor<PatientProfile> {
}