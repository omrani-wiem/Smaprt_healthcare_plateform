package main.java.com.healthcare.restservice.repository;

import com.healthcare.restservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}

