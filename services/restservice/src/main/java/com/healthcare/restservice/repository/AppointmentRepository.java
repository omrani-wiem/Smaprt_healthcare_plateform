package com.healthcare.restservice.repository;

import com.healthcare.restservice.model.Appointment;
import com.healthcare.restservice.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    boolean existsByDoctorIdAndDateTimeAndStatusNot(
            Long doctorId, LocalDateTime dateTime, AppointmentStatus status);
}