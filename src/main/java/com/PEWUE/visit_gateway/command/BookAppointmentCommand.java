package com.PEWUE.visit_gateway.command;

public record BookAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}
