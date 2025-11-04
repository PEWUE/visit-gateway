package com.PEWUE.visit_gateway.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String specialization,
        UserDto user,
        List<Long> institutionsIds,
        List<Long> appointmentsIds
) {
}
