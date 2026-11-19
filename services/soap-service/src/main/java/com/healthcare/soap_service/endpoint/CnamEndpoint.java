package com.healthcare.soap_service.endpoint;

import com.healthcare.soapservice.generated.*;
import com.healthcare.soap_service.model.FeuilleDeSoins;
import com.healthcare.soap_service.service.CnamService;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@Endpoint
@RequiredArgsConstructor
public class CnamEndpoint {

    private static final String NS = "http://healthcare.com/soap/cnam";
    private final CnamService service;

    @PayloadRoot(namespace = NS, localPart = "verifierDroitsCNAMRequest")
    @ResponsePayload
    public VerifierDroitsCNAMResponse verifier(@RequestPayload VerifierDroitsCNAMRequest req) {
        var v = service.verifierDroits(req.getPatientId(), req.getNumeroCNAM(), req.getCodeActe());
        VerifierDroitsCNAMResponse res = new VerifierDroitsCNAMResponse();
        res.setDroitsOuverts(v.droitsOuverts());
        res.setTauxRemboursement(v.taux());
        res.setMotif(v.motif());
        return res;
    }

    @PayloadRoot(namespace = NS, localPart = "emettreFeuilleDeSoinsRequest")
    @ResponsePayload
    public EmettreFeuilleDeSoinsResponse emettre(@RequestPayload EmettreFeuilleDeSoinsRequest req) {
        LocalDate date = req.getDateActe().toGregorianCalendar().toZonedDateTime().toLocalDate();
        FeuilleDeSoins f = service.emettreFeuille(req.getPatientId(), req.getNumeroCNAM(),
                req.getCodeActe(), req.getMontant(), date);
        EmettreFeuilleDeSoinsResponse res = new EmettreFeuilleDeSoinsResponse();
        res.setNumeroFeuille(f.getNumeroFeuille());
        res.setMontantRembourse(f.getMontantRembourse());
        res.setResteACharge(f.getMontant().subtract(f.getMontantRembourse()));
        res.setStatut("CERTIFIEE");
        return res;
    }

    @PayloadRoot(namespace = NS, localPart = "consulterHistoriqueRequest")
    @ResponsePayload
    public ConsulterHistoriqueResponse historique(@RequestPayload ConsulterHistoriqueRequest req) {
        ConsulterHistoriqueResponse res = new ConsulterHistoriqueResponse();
        for (FeuilleDeSoins f : service.historique(req.getPatientId())) {
            Acte a = new Acte();
            a.setNumeroFeuille(f.getNumeroFeuille());
            a.setCodeActe(f.getCodeActe());
            a.setDateActe(toXmlDate(f.getDateActe()));
            a.setMontant(f.getMontant());
            a.setMontantRembourse(f.getMontantRembourse());
            res.getActe().add(a);
        }
        return res;
    }

    private XMLGregorianCalendar toXmlDate(LocalDate d) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(d.toString());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}