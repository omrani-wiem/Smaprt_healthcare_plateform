package com.healthcare.soap_service.service;

import com.healthcare.soap_service.model.ActeMedical;
import com.healthcare.soap_service.model.Assure;
import com.healthcare.soap_service.repository.ActeMedicalRepository;
import com.healthcare.soap_service.repository.AssureRepository;
import com.healthcare.soap_service.repository.FeuilleDeSoinsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CnamServiceTest {

    @Mock AssureRepository assures;
    @Mock ActeMedicalRepository actes;
    @Mock FeuilleDeSoinsRepository feuilles;
    @InjectMocks CnamService service;

    @Test
    void activeInsuredGetsReimbursementRate() {
        when(assures.findByPatientIdAndNumeroCNAM(1L, "12345678")).thenReturn(Optional.of(
                Assure.builder().patientId(1L).numeroCNAM("12345678")
                        .droitsActifs(true).dateFinDroits(LocalDate.now().plusYears(1)).build()));
        when(actes.findById("CS")).thenReturn(Optional.of(
                new ActeMedical("CS", "Consultation spécialiste", new BigDecimal("50.000"), 80)));

        var result = service.verifierDroits(1L, "12345678", "CS");

        assertTrue(result.droitsOuverts());
        assertEquals(80, result.taux());
    }

    @Test
    void expiredRightsAreRejected() {
        when(assures.findByPatientIdAndNumeroCNAM(2L, "87654321")).thenReturn(Optional.of(
                Assure.builder().patientId(2L).numeroCNAM("87654321")
                        .droitsActifs(false).dateFinDroits(LocalDate.of(2025, 6, 30)).build()));

        var result = service.verifierDroits(2L, "87654321", "CS");

        assertFalse(result.droitsOuverts());
        assertEquals("Droits expirés ou inactifs", result.motif());
    }
}