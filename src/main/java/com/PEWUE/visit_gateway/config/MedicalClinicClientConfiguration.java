package com.PEWUE.visit_gateway.config;

import com.PEWUE.visit_gateway.decoder.MedicalClinicErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class MedicalClinicClientConfiguration {
    @Bean
    public feign.Client feignClient() {
        return new OkHttpClient();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new MedicalClinicErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5);
    }
}
