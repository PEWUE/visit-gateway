package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.VisitGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/visits")
public class VisitGatewayController {
    private final VisitGatewayService visitGatewayService;

    @GetMapping("/patient/{patientId}")
    public PageDto<AppointmentDto> getPatientVisits(@PathVariable Long patientId, Pageable pageable) {
        return visitGatewayService.getPatientVisits(patientId, pageable);
    }
}
