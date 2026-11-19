package com.healthcare.restservice.repository;

import com.healthcare.restservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
        Optional<Doctor> findFirstBySpecialty(String specialty);
}