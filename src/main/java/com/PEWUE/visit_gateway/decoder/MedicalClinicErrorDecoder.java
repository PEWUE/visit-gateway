package com.PEWUE.visit_gateway.decoder;

import com.PEWUE.visit_gateway.dto.ErrorMessageDto;
import com.PEWUE.visit_gateway.exception.NotFoundException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.io.InputStream;

public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String s, Response response) {
        ErrorMessageDto message = null;
        try (InputStream bodyIs = response.body()
                .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            message = mapper.readValue(bodyIs, ErrorMessageDto.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
            case 404:
                return new NotFoundException(message.message());
            case 500:
                return new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, message.message());
            case 503:
                FeignException exception = feign.FeignException.errorStatus(s, response);
                return new RetryableException(
                        response.status(),
                        exception.getMessage(),
                        response.request().httpMethod(),
                        exception,
                        50L,
                        response.request()
                );
            default:
                return defaultErrorDecoder.decode(s, response);
        }
    }
}
