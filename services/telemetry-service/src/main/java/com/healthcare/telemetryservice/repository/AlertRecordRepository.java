package com.healthcare.telemetryservice.repository;

import com.healthcare.telemetryservice.model.AlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
}