package com.PEWUE.visit_gateway.command;

import lombok.Builder;

@Builder
public record BookAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}
