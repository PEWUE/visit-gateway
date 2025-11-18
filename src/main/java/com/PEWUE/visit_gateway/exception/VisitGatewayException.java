package com.PEWUE.visit_gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VisitGatewayException extends RuntimeException {
    private final HttpStatus status;

    public VisitGatewayException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
