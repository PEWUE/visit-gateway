package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final MedicalClinicClient medicalClinicClient;

    public PageDto<AppointmentDto> getPatientAppointments(Long patientId, Pageable pageable) {
        return medicalClinicClient.getAppointments(null, patientId, pageable);
    }

    public AppointmentDto bookAppointment(BookAppointmentCommand command) {
        return medicalClinicClient.bookAppointment(command);
    }

    public PageDto<AppointmentDto> getFreeSlots(Long doctorId, Pageable pageable) {
        return medicalClinicClient.getFreeSlots(doctorId, pageable);
    }

    public PageDto<AppointmentDto> getFreeSlotsBySpecializationAndDate(String specialization, LocalDate date, Pageable pageable) {
        return medicalClinicClient.getFreeSlotsBySpecializationAndDate(specialization, date.format(DateTimeFormatter.ISO_DATE), pageable);
    }

    public PageDto<AppointmentDto> getDoctorsAppointments(Long doctorId, Pageable pageable) {
        return medicalClinicClient.getAppointments(doctorId, null, pageable);
    }

    public PageDto<AppointmentDto> getPatientAppointments(Long patientId, String specialization, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return medicalClinicClient.getPatientAppointmentsBySpecializationAndRange(
                patientId, specialization, from, to, pageable
        );
    }

    public PageDto<AppointmentDto> getFreeSlotsBySpecializationAndRange(String specialization, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return medicalClinicClient.getFreeSlotsBySpecializationAndRange(specialization, from, to, pageable);
    }

    public void cancelAppointment(Long appointmentId) {
        AppointmentDto appointmentDto = medicalClinicClient.findById(appointmentId);
        if (appointmentDto.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel past appointments");
        }
        medicalClinicClient.cancelAppointment(appointmentId);
    }
}
