package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitGatewayService {
    private final MedicalClinicClient medicalClinicClient;

    public PageDto<AppointmentDto> getPatientAppointments(Long patientId, Pageable pageable) {
        return medicalClinicClient.getAppointments(null, patientId, pageable);
    }

    public AppointmentDto bookAppointment(BookAppointmentCommand command) {
        return medicalClinicClient.bookAppointment(command);
    }
}
