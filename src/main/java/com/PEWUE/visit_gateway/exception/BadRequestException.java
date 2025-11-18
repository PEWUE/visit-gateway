package com.PEWUE.visit_gateway.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends VisitGatewayException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
