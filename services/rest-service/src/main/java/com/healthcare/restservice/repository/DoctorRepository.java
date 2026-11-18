package main.java.com.healthcare.restservice.repository;

import com.healthcare.restservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}