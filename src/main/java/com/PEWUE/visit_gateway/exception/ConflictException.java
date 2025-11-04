package com.PEWUE.visit_gateway.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends VisitGatewayException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
