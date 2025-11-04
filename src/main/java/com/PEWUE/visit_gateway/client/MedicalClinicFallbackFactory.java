package com.PEWUE.visit_gateway.client;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.exception.AppointmentServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Component
public class MedicalClinicFallbackFactory implements FallbackFactory<MedicalClinicClient> {
    @Override
    public MedicalClinicClient create(Throwable cause) {
        return new MedicalClinicClient() {

            @Override
            public PageDto<AppointmentDto> getAppointments(Long doctorId, Long patientId, String specialization,
                                                           LocalDateTime from, LocalDateTime to,
                                                           Boolean freeSlots, Pageable pageable) {
                log.error("Fallback triggered for getAppointments", cause);
                return new PageDto<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0L, 0);
            }

            @Override
            public AppointmentDto findById(Long appointmentId) {
                log.error("Fallback triggered for findById with id {}", appointmentId, cause);
                return null;
            }

            @Override
            public AppointmentDto bookAppointment(BookAppointmentCommand command) {
                log.error("Fallback triggered for bookAppointment with command {}", command, cause);
                throw new AppointmentServiceUnavailableException("Appointment booking service is currently unavailable");
            }

            @Override
            public void cancelAppointment(Long appointmentId) {
                log.error("Fallback triggered for cancelAppointment with id {}", appointmentId, cause);
                throw new AppointmentServiceUnavailableException("Appointment cancellation service is currently unavailable");
            }

            @Override
            public PageDto<DoctorDto> getDoctors(String specialization, Pageable pageable) {
                log.error("Fallback triggered for getDoctors with specialization {}", specialization, cause);
                return new PageDto<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0L, 0);
            }
        };
    }
}
