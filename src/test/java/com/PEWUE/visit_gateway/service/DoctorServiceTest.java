package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {
    MedicalClinicClient medicalClinicClient;
    DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.medicalClinicClient = mock(MedicalClinicClient.class);
        this.doctorService = new DoctorService(medicalClinicClient);
    }

    @Test
    void getDoctors_DataCorrect_DoctorsReturned() {
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

        when(medicalClinicClient.getDoctors(eq("cardiologist"), eq(pageable))).thenReturn(pageDto);

        PageDto<DoctorDto> result = doctorService.getDoctors("cardiologist", pageable);

        assertAll(
                () -> assertEquals(2, result.content().size()),
                () -> assertEquals(1L, result.content().get(0).id()),
                () -> assertEquals("John", result.content().get(0).firstName()),
                () -> assertEquals("Doe", result.content().get(0).lastName()),
                () -> assertEquals("cardiologist", result.content().get(0).specialization()),

                () -> assertEquals(2L, result.content().get(1).id()),
                () -> assertEquals("Ann", result.content().get(1).firstName()),
                () -> assertEquals("Smith", result.content().get(1).lastName()),
                () -> assertEquals("cardiologist", result.content().get(1).specialization())
        );
        verify(medicalClinicClient).getDoctors(eq("cardiologist"), eq(pageable));
    }

    @Test
    void getDoctors_Empty_ReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 2);

        PageDto<DoctorDto> emptyPage = new PageDto<>(
                List.of(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0L,
                0
        );

        when(medicalClinicClient.getDoctors(eq("neurologist"), eq(pageable))).thenReturn(emptyPage);

        PageDto<DoctorDto> result = doctorService.getDoctors("neurologist", pageable);

        assertAll(
                () -> assertTrue(result.content().isEmpty()),
                () -> assertEquals(0, result.totalElements())
        );
        verify(medicalClinicClient).getDoctors(eq("neurologist"), eq(pageable));
    }
}
