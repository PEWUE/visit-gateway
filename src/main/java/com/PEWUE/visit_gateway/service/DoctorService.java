package com.PEWUE.visit_gateway.service;

import com.PEWUE.visit_gateway.client.MedicalClinicClient;
import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final MedicalClinicClient medicalClinicClient;

    public PageDto<DoctorDto> getDoctors(String specialization, Pageable pageable) {
        return medicalClinicClient.getDoctors(specialization, pageable);
    }
}
