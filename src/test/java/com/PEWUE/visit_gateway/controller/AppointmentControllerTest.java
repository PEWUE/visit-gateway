package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.command.BookAppointmentCommand;
import com.PEWUE.visit_gateway.dto.AppointmentDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {
    @MockitoBean
    private AppointmentService appointmentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnPagedAppointmentDtosWhenDataCorrect() throws Exception {
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

        when(appointmentService.findAppointments(eq(a1.doctorId()), eq(a1.patientId()), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments")
                        .param("doctorId", a1.doctorId().toString())
                        .param("patientId", a1.patientId().toString())
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    void shouldReturnAppointmentDtoWhenBookingIsSuccessful() throws Exception {
        BookAppointmentCommand command = BookAppointmentCommand.builder()
                .appointmentId(2L)
                .patientId(5L)
                .build();
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(2L)
                .doctorId(4L)
                .patientId(5L)
                .startTime(LocalDateTime.of(2026, 5, 10, 8, 30))
                .endTime(LocalDateTime.of(2026, 5, 10, 9, 0))
                .build();

        when(appointmentService.book(command)).thenReturn(appointmentDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/appointments/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.doctorId").value(4))
                .andExpect(jsonPath("$.patientId").value(5))
                .andExpect(jsonPath("$.startTime").value("2026-05-10T08:30:00"))
                .andExpect(jsonPath("$.endTime").value("2026-05-10T09:00:00"));
    }

    @Test
    void cancelAppointment_ShouldReturnNoContent_WhenValid() throws Exception {
        Long appointmentId = 5L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/appointments/{appointmentId}", appointmentId))
                .andExpect(status().isNoContent());

        verify(appointmentService).cancel(appointmentId);
    }
}
