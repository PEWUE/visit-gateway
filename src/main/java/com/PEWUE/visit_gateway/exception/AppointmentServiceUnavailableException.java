package com.PEWUE.visit_gateway.exception;

import org.springframework.http.HttpStatus;

public class AppointmentServiceUnavailableException extends VisitGatewayException {
    public AppointmentServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
