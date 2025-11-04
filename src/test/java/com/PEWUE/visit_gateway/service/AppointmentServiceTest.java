package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {
    MedicalClinicClient medicalClinicClient;
    AppointmentService appointmentService;

    @BeforeEach
    void setup() {
        this.medicalClinicClient = mock(MedicalClinicClient.class);
        this.appointmentService = new AppointmentService(medicalClinicClient);
    }

    @Test
    void findAppointments_DataCorrect_AppointmentsReturned() {
        AppointmentDto a1 = AppointmentDto.builder()
                .id(1L)
                .doctorId(1L)
                .patientId(2L)
                .startTime(LocalDateTime.of(2025, 10, 1, 10, 45))
                .endTime(LocalDateTime.of(2025, 10, 1, 11, 15))
                .build();
        AppointmentDto a2 = AppointmentDto.builder()
                .id(2L)
                .doctorId(1L)
                .patientId(2L)
                .startTime(LocalDateTime.of(2025, 10, 1, 12, 45))
                .endTime(LocalDateTime.of(2025, 10, 1, 13, 15))
                .build();

        Pageable pageable = PageRequest.of(0, 2);
        PageDto<AppointmentDto> pageDto = new PageDto<>(
                List.of(a1, a2),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                2L,
                1
        );

        when(medicalClinicClient.getAppointments(
                eq(a1.doctorId()),
                eq(a1.patientId()),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
        )).thenReturn(pageDto);

        PageDto<AppointmentDto> result = appointmentService.findAppointments(
                a1.doctorId(),
                a1.patientId(),
                null,
                null,
                null,
                null,
                pageable
        );

        assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(2L, result.content().get(1).id()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 10, 45), result.content().get(0).startTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 11, 15), result.content().get(0).endTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 12, 45), result.content().get(1).startTime()),
                () -> assertEquals(LocalDateTime.of(2025, 10, 1, 13, 15), result.content().get(1).endTime()),
                () -> assertEquals(1L, result.content().get(0).doctorId()),
                () -> assertEquals(2L, result.content().get(0).patientId()),
                () -> assertEquals(1L, result.content().get(1).doctorId()),
                () -> assertEquals(2L, result.content().get(1).patientId())
        );
    }

    @Test
    void bookAppointment_SuccessfulBooking() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusMinutes(30);

        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .patientId(2L)
                .build();

        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(1L)
                .doctorId(1L)
                .patientId(2L)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        when(medicalClinicClient.findById(command.appointmentId())).thenReturn(appointmentDto);
        when(medicalClinicClient.bookAppointment(command)).thenReturn(appointmentDto);

        AppointmentDto result = appointmentService.book(command);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertTrue(result.startTime().isAfter(LocalDateTime.now()))
        );
        verify(medicalClinicClient).findById(command.appointmentId());
        verify(medicalClinicClient).bookAppointment(command);
    }

    @Test
    void book_AppointmentInPast_ThrowsBadRequestException() {
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .patientId(2L)
                .build();

        AppointmentDto pastAppointment = AppointmentDto.builder()
                .id(1L)
                .doctorId(1L)
                .patientId(2L)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                .build();

        when(medicalClinicClient.findById(command.appointmentId())).thenReturn(pastAppointment);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            appointmentService.book(command);
        });
        assertAll(
                () -> assertEquals("Cannot book past appointments", ex.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus())
        );
    }

    @Test
    void cancel_ValidFutureAppointment_Success() {
        Long appointmentId = 1L;
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(appointmentId)
                .doctorId(1L)
                .patientId(2L)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();

        when(medicalClinicClient.findById(appointmentId)).thenReturn(appointmentDto);

        appointmentService.cancel(appointmentId);

        verify(medicalClinicClient).findById(appointmentId);
        verify(medicalClinicClient).cancelAppointment(appointmentId);
    }

    @Test
    void cancel_PastAppointment_ThrowsBadRequestException() {
        Long appointmentId = 1L;
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(appointmentId)
                .doctorId(1L)
                .patientId(2L)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1).plusHours(1))
                .build();

        when(medicalClinicClient.findById(appointmentId)).thenReturn(appointmentDto);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            appointmentService.cancel(appointmentId);
        });
        assertAll(
                () -> assertEquals("Cannot cancel past appointments", ex.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus())
        );
    }


}
