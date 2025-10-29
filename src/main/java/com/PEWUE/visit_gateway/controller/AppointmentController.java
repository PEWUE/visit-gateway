package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/patient/{patientId}")
    public PageDto<AppointmentDto> getPatientAppointments(@PathVariable Long patientId, Pageable pageable) {
        return appointmentService.getPatientAppointments(patientId, pageable);
    }

    @PatchMapping("/book")
    public AppointmentDto bookAppointment(@RequestBody BookAppointmentCommand command) {
        return appointmentService.bookAppointment(command);
    }

    @GetMapping("/free-slots/doctor")
    public PageDto<AppointmentDto> getFreeSlots(@RequestParam Long doctorId, Pageable pageable) {
        return appointmentService.getFreeSlots(doctorId, pageable);
    }

    @GetMapping("/free-slots/specialization")
    public PageDto<AppointmentDto> getFreeSlotsBySpecializationAndDate(
            @RequestParam String specialization,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return appointmentService.getFreeSlotsBySpecializationAndDate(specialization, date, pageable);
    }

    @GetMapping("/doctor/{doctorId}")
    public PageDto<AppointmentDto> getAppointmentsForDoctor(@PathVariable Long doctorId, Pageable pageable) {
        return appointmentService.getDoctorsAppointments(doctorId, pageable);
    }

    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
    }

}
