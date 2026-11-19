package com.healthcare.orchestratorservice.repository;

import com.healthcare.orchestratorservice.model.Intervention;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, Long> {
    List<Intervention> findAllByOrderByCreatedAtDesc();
}