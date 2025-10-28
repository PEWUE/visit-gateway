package com.PEWUE.visit_gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MedicalClinicFallbackFactory implements FallbackFactory<MedicalClinicClient> {
    @Override
    public MedicalClinicClient create(Throwable cause) {
        return null;
    }
}
