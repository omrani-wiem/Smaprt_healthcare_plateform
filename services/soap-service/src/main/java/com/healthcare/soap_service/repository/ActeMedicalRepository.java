package com.healthcare.soap_service.repository;

import com.healthcare.soap_service.model.ActeMedical;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActeMedicalRepository extends JpaRepository<ActeMedical, String> {
}