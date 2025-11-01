package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final MedicalClinicClient medicalClinicClient;

    public PageDto<AppointmentDto> findAppointments(
            Long doctorId,
            Long patientId,
            String specialization,
            LocalDateTime from,
            LocalDateTime to,
            Boolean freeSlots,
            Pageable pageable) {
        return medicalClinicClient.getAppointments(
                doctorId, patientId, specialization, from, to, freeSlots, pageable);
    }

    public AppointmentDto bookAppointment(BookAppointmentCommand command) {
        return medicalClinicClient.bookAppointment(command);
    }

    public void cancelAppointment(Long appointmentId) {
        AppointmentDto appointmentDto = medicalClinicClient.findById(appointmentId);
        if (appointmentDto.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel past appointments");
        }
        medicalClinicClient.cancelAppointment(appointmentId);
    }
}
