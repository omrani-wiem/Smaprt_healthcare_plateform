package com.healthcare.soap_service.service;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class DroitsException extends RuntimeException {
    public DroitsException(String message) {
        super(message);
    }
}