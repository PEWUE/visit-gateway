package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public PageDto<DoctorDto> getDoctors(@RequestParam String specialization, Pageable pageable) {
        return doctorService.getDoctors(specialization, pageable);
    }
}
