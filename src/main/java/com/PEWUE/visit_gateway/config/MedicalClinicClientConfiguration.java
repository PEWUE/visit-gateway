package com.PEWUE.visit_gateway.config;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;

public class MedicalClinicClientConfiguration {
    @Bean
    public feign.Client feignClient() {
        return new OkHttpClient();
    }
}
