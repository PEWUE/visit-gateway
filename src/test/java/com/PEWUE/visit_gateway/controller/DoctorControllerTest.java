package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.DoctorService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @MockitoBean
    private DoctorService doctorService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPagedDoctorDtosWhenDataCorrect() throws Exception {
        DoctorDto d1 = DoctorDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .specialization("cardiologist")
                .build();

        DoctorDto d2 = DoctorDto.builder()
                .id(2L)
                .firstName("Ann")
                .lastName("Smith")
                .specialization("cardiologist")
                .build();

        Pageable pageable = PageRequest.of(0, 2);

        PageDto<DoctorDto> pageDto = new PageDto<>(
                List.of(d1, d2),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                2L,
                1
        );

        when(doctorService.getDoctors(eq("cardiologist"), eq(pageable))).thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                        .param("specialization", "cardiologist")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.content[0].specialization").value("cardiologist"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].firstName").value("Ann"))
                .andExpect(jsonPath("$.content[1].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[1].specialization").value("cardiologist"));

        verify(doctorService).getDoctors(eq("cardiologist"), eq(pageable));
    }

    @Test
    void shouldReturnEmptyPageWhenNoDoctorsFound() throws Exception {
        Pageable pageable = PageRequest.of(0, 2);

        PageDto<DoctorDto> emptyPage = new PageDto<>(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0L, 0);

        when(doctorService.getDoctors(eq("neurologist"), eq(pageable))).thenReturn(emptyPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors")
                        .param("specialization", "neurologist")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(doctorService).getDoctors(eq("neurologist"), eq(pageable));
    }
}
