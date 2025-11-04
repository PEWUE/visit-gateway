package com.PEWUE.visit_gateway.config;

import feign.Retryer;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MedicalClinicClientConfiguration {
    @Bean
    public feign.Client feignClient() {
        return new OkHttpClient();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }
}
