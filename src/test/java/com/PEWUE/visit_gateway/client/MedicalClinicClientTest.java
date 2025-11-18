package com.PEWUE.visit_gateway.client;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.exception.BadRequestException;
import com.PEWUE.visit_gateway.exception.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWireMock(port = 8082)
public class MedicalClinicClientTest {
    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    MedicalClinicClient medicalClinicClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnPageOfAppointments() throws JsonProcessingException {
        List<AppointmentDto> appointments = List.of(
                AppointmentDto.builder().id(1L).doctorId(2L).patientId(3L).build(),
                AppointmentDto.builder().id(2L).doctorId(5L).patientId(5L).build()
        );
        Pageable pageable = PageRequest.of(0, 2);
        PageDto<AppointmentDto> page = PageDto.<AppointmentDto>builder()
                .content(appointments)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(2L)
                .totalPages(1)
                .build();

        wireMockServer.stubFor(get(urlPathEqualTo("/appointments"))
                .withQueryParam("size", equalTo("2"))
                .withQueryParam("page", equalTo("0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(page))));

        var result = medicalClinicClient.getAppointments(null, null, null, null, null, null, pageable);

        assertAll(
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals(2L, result.content().get(0).doctorId()),
                () -> assertEquals(3L, result.content().get(0).patientId()),
                () -> assertEquals(2L, result.content().get(1).id()),
                () -> assertEquals(5L, result.content().get(1).doctorId()),
                () -> assertEquals(5L, result.content().get(1).patientId()),
                () -> assertEquals(0, result.pageNumber()),
                () -> assertEquals(2, result.pageSize()),
                () -> assertEquals(2L, result.totalElements())
        );
    }

    @Test
    void shouldReturnAppointmentById() throws JsonProcessingException {
        AppointmentDto appointment = AppointmentDto.builder().id(1L).doctorId(2L).patientId(3L).build();

        wireMockServer.stubFor(get(urlEqualTo("/appointments/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(appointment))));

        AppointmentDto result = medicalClinicClient.findById(1L);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(2L, result.doctorId()),
                () -> assertEquals(3L, result.patientId())
        );
    }

    @Test
    void shouldThrowNotFoundForFindById() {
        wireMockServer.stubFor(get(urlEqualTo("/appointments/99"))
                .willReturn(aResponse()
                        .withStatus(404)));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> medicalClinicClient.findById(99L));

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, ex.getStatus())
        );
    }

    @Test
    void shouldBookAppointment() throws JsonProcessingException {
        BookAppointmentCommand command = BookAppointmentCommand.builder().appointmentId(10L).patientId(3L).build();
        AppointmentDto booked = AppointmentDto.builder().id(10L).doctorId(2L).patientId(3L).build();

        wireMockServer.stubFor(patch(urlEqualTo("/appointments/book"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(booked))));

        AppointmentDto result = medicalClinicClient.bookAppointment(command);

        assertAll(
                () -> assertEquals(10L, result.id()),
                () -> assertEquals(2L, result.doctorId()),
                () -> assertEquals(3L, result.patientId())
        );
    }

    @Test
    void shouldFailBooking() throws JsonProcessingException {
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(1L)
                .patientId(2L)
                .build();

        wireMockServer.stubFor(patch(urlEqualTo("/appointments/book"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"message\": \"Cannot book past appointments\"}")));


        BadRequestException ex = assertThrows(BadRequestException.class, () -> medicalClinicClient.bookAppointment(command));

        assertAll(
                ()->assertEquals("Cannot book past appointments", ex.getMessage()),
                ()->assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus())
        );
    }
}
