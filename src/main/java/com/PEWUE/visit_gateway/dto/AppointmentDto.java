package com.PEWUE.visit_gateway.dto;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
