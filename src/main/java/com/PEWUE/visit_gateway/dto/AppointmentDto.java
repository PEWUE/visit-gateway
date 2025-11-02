package com.PEWUE.visit_gateway.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AppointmentDto(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
