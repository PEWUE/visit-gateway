package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicClient medicalClinicClient;

    public PageDto<DoctorDto> getDoctors(String specialization, Pageable pageable) {
        log.debug("Fetching doctors with specialization='{}', pageable={}", specialization, pageable);
        PageDto<DoctorDto> pageDto = medicalClinicClient.getDoctors(specialization, pageable);
        log.debug("Found {} doctors", pageDto.totalElements());
        return pageDto;
    }
}
