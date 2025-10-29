package com.PEWUE.visit_gateway.client;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.config.MedicalClinicClientConfiguration;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


@FeignClient(name = "medical-clinic", url = "http://localhost:8080", configuration = MedicalClinicClientConfiguration.class, fallbackFactory = MedicalClinicFallbackFactory.class)
public interface MedicalClinicClient {

    @GetMapping("/appointments")
    PageDto<AppointmentDto> getAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            Pageable pageable
    );

    @GetMapping("/appointments/{appointmentId}")
    AppointmentDto findById(@PathVariable Long appointmentId);

    @PatchMapping("/appointments/book")
    AppointmentDto bookAppointment(@RequestBody BookAppointmentCommand command);

    @GetMapping("/appointments/free-slots/doctor")
    PageDto<AppointmentDto> getFreeSlots(@RequestParam Long doctorId, Pageable pageable);

    @GetMapping("/appointments/free-slots/specialization")
    PageDto<AppointmentDto> getFreeSlotsBySpecializationAndDate(@RequestParam String specialization, @RequestParam String date, Pageable pageable);

    @DeleteMapping("/appointments/{appointmentId}")
    void cancelAppointment(@PathVariable Long appointmentId);

    @GetMapping("/appointments/patient/{patientId}/specialization-range")
    PageDto<AppointmentDto> getPatientAppointmentsBySpecializationAndRange(
            @PathVariable Long patientId,
            @RequestParam(required = false) String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable
    );

    @GetMapping("/appointments/free-slots/specialization-range")
    PageDto<AppointmentDto> getFreeSlotsBySpecializationAndRange(
            @RequestParam String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable
    );

    @GetMapping("/doctors")
    PageDto<DoctorDto> getDoctors(@RequestParam String specialization, Pageable pageable);
}
