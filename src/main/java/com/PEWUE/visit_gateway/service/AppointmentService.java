package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
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
        log.debug("Finding appointments with filters doctorId={}, patientId={}, specialization={}, from={}, to={}, freeSlots={}, pageable={}",
                doctorId, patientId, specialization, from, to, freeSlots, pageable);
        return medicalClinicClient.getAppointments(
                doctorId, patientId, specialization, from, to, freeSlots, pageable);
    }

    public AppointmentDto bookAppointment(BookAppointmentCommand command) {
        log.debug("Booking appointment with data: {}", command);
        AppointmentDto appointmentDto = medicalClinicClient.findById(command.appointmentId());
        if (appointmentDto.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book past appointments");
        }
        AppointmentDto booked = medicalClinicClient.bookAppointment(command);
        log.debug("Appointment booked with id: {}", booked.id());
        return booked;
    }

    public void cancelAppointment(Long appointmentId) {
        log.debug("Cancelling appointment with id: {}", appointmentId);
        AppointmentDto appointmentDto = medicalClinicClient.findById(appointmentId);
        if (appointmentDto.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel past appointments");
        }
        medicalClinicClient.cancelAppointment(appointmentId);
        log.debug("Appointment with id {} cancelled", appointmentId);
    }
}
