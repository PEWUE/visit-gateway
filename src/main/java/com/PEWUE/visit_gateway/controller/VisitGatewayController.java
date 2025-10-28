package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.VisitGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
public class VisitGatewayController {
    private final VisitGatewayService visitGatewayService;

    @GetMapping("/patient/{patientId}")
    public PageDto<AppointmentDto> getPatientAppointments(@PathVariable Long patientId, Pageable pageable) {
        return visitGatewayService.getPatientAppointments(patientId, pageable);
    }

    @PatchMapping("/book")
    public AppointmentDto bookAppointment(@RequestBody BookAppointmentCommand command) {
        return visitGatewayService.bookAppointment(command);
    }

    @GetMapping("/free-slots")
    public PageDto<AppointmentDto> getFreeSlots(@RequestParam Long doctorId, Pageable pageable) {
        return visitGatewayService.getFreeSlots(doctorId, pageable);
    }
}
