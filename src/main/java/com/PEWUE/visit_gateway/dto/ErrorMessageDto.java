package com.PEWUE.visit_gateway.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorMessageDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
