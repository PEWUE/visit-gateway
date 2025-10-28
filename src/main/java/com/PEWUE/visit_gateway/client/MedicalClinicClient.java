package com.PEWUE.visit_gateway.client;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.config.MedicalClinicClientConfiguration;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;


@FeignClient(name = "medical-clinic", url = "http://localhost:8080", configuration = MedicalClinicClientConfiguration.class, fallbackFactory = MedicalClinicFallbackFactory.class)
public interface MedicalClinicClient {

    @GetMapping("/appointments")
    PageDto<AppointmentDto> getAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            Pageable pageable
    );

    @PatchMapping("/appointments/book")
    AppointmentDto bookAppointment(@RequestBody BookAppointmentCommand command);

    @GetMapping("/appointments/free-slots/doctor")
    PageDto<AppointmentDto> getFreeSlots(@RequestParam Long doctorId, Pageable pageable);

    @GetMapping("/appointments/free-slots/specialization")
    PageDto<AppointmentDto> getFreeSlotsBySpecializationAndDate(@RequestParam String specialization, @RequestParam String date, Pageable pageable);
}
