package com.healthcare.restservice.service;


import com.healthcare.restservice.dto.AppointmentRequest;
import com.healthcare.restservice.model.*;
import com.healthcare.restservice.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;


@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    
    public List<Appointment>  findAll() {
        return appointmentRepository.findAll();

    }

    public List<Appointment> findByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }


    public Appointment findById(Long id) {
         return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rendez-vous introuvable : " + id));
    }

    public Appointment create(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient introuvable"));
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médecin introuvable"));

        boolean slotTaken = appointmentRepository
                .existsByDoctorIdAndDateTimeAndStatusNot(
                        doctor.getId(), request.dateTime(), AppointmentStatus.CANCELLED);
        if (slotTaken) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ce créneau est déjà réservé");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(request.dateTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.reason())
                .build();
        return appointmentRepository.save(appointment);
    }

    public Appointment update(Long id, AppointmentRequest request) {
        Appointment existing = findById(id);
        boolean slotTaken = appointmentRepository
                .existsByDoctorIdAndDateTimeAndStatusNot(
                        request.doctorId(), request.dateTime(), AppointmentStatus.CANCELLED)
                && !(existing.getDoctor().getId().equals(request.doctorId())
                     && existing.getDateTime().equals(request.dateTime()));
        if (slotTaken) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ce créneau est déjà réservé");
        }
        existing.setDoctor(doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Médecin introuvable")));
        existing.setDateTime(request.dateTime());
        existing.setReason(request.reason());
        return appointmentRepository.save(existing);
    }

    public void cancel(Long id) {
        Appointment appointment = findById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }
}
