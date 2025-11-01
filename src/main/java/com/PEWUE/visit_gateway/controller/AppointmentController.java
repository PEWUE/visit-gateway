package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.ErrorMessageDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(
            summary = "Get a list of appointments using dynamic filters",
            description = "Returns a paginated list of appointments. Supports filtering by doctor, patient, specialization, date/time range and available slots (where patientId is null). All filter parameters are optional."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of appointments matching filters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping
    public PageDto<AppointmentDto> findAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Boolean freeSlots,
            @ParameterObject Pageable pageable) {
        log.info("GET /appointments called with doctorId={}, patientId={}, specialization={}, from={}, to={}, freeSlots={}, pageable={}",
                doctorId, patientId, specialization, from, to, freeSlots, pageable);
        return appointmentService.findAppointments(
                doctorId, patientId, specialization, from, to, freeSlots, pageable);
    }

    @Operation(
            summary = "Book an appointment",
            description = "Books an appointment using provided data. Returns the booked appointment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment successfully booked",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/book")
    public AppointmentDto bookAppointment(@RequestBody BookAppointmentCommand command) {
        log.info("PATCH /appointments/book called with command: {}", command);
        return appointmentService.bookAppointment(command);
    }

    @Operation(summary = "Cancel an appointment")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Appointment successfully cancelled"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot cancel past appointments or invalid appointment ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAppointment(@PathVariable Long appointmentId) {
        log.info("DELETE /appointments/{} called", appointmentId);
        appointmentService.cancelAppointment(appointmentId);
    }
}
