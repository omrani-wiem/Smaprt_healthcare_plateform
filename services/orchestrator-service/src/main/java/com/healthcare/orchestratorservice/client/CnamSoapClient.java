package com.healthcare.orchestratorservice.client;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Component
public class CnamSoapClient {

    private static final String NS = "http://healthcare.com/soap/cnam";

    public record Coverage(boolean open, int rate, String reason) {}

    private final RestClient rest;

    public CnamSoapClient(@LoadBalanced RestClient.Builder builder) {
        this.rest = builder.baseUrl("http://soap-service").build();
    }

    public Coverage verify(Long patientId, String numeroCnam, String codeActe) {
        String envelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cn="%s">
                  <soapenv:Body>
                    <cn:verifierDroitsCNAMRequest>
                      <cn:patientId>%d</cn:patientId>
                      <cn:numeroCNAM>%s</cn:numeroCNAM>
                      <cn:codeActe>%s</cn:codeActe>
                    </cn:verifierDroitsCNAMRequest>
                  </soapenv:Body>
                </soapenv:Envelope>""".formatted(NS, patientId, escape(numeroCnam), escape(codeActe));

        String xml = rest.post().uri("/ws")
                .contentType(MediaType.TEXT_XML)
                .body(envelope)
                .retrieve().body(String.class);
        return parse(xml);
    }

    private Coverage parse(String xml) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document d = f.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            return new Coverage(
                    Boolean.parseBoolean(text(d, "droitsOuverts")),
                    Integer.parseInt(text(d, "tauxRemboursement")),
                    text(d, "motif"));
        } catch (Exception e) {
            throw new IllegalStateException("Réponse SOAP illisible", e);
        }
    }

    private String text(Document d, String tag) {
        NodeList n = d.getElementsByTagNameNS(NS, tag);
        return n.getLength() == 0 ? "" : n.item(0).getTextContent();
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}