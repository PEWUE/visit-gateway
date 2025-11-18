package com.PEWUE.visit_gateway.integration;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.config.TestConfig;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 8082)
public class IntegrationTest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    WireMockServer wireMockServer;

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

        ResponseEntity<String> response = testRestTemplate
                .getForEntity("http://localhost:8081/appointments?size=2&page=0", String.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(objectMapper.writeValueAsString(page), response.getBody())
        );
    }

    @Test
    void shouldBookAppointment() throws JsonProcessingException {
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(5L)
                .patientId(43L)
                .build();
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(5L)
                .doctorId(12L)
                .patientId(43L)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0))
                .build();

        wireMockServer.stubFor(get("/appointments/5")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(appointmentDto))));

        wireMockServer.stubFor(patch(urlEqualTo("/appointments/book"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(command)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(appointmentDto))));

        ResponseEntity<AppointmentDto> response = testRestTemplate.exchange(
                "http://localhost:8081/appointments/book",
                HttpMethod.PATCH,
                new HttpEntity<>(command),
                AppointmentDto.class
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(5L, response.getBody().id()),
                () -> assertEquals(12L, response.getBody().doctorId()),
                () -> assertEquals(43L, response.getBody().patientId())
        );
    }

    @Test
    void shouldCancelAppointment() throws JsonProcessingException {
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(5L)
                .doctorId(12L)
                .patientId(43L)
                .startTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(45).withSecond(0).withNano(0))
                .endTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(45).withSecond(0).withNano(0))
                .build();

        wireMockServer.stubFor(get("/appointments/5")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(appointmentDto))));

        wireMockServer.stubFor(delete("/appointments/5")
                .willReturn(aResponse()
                        .withStatus(204)));

        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:8081/appointments/5",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode())
        );
    }
}
