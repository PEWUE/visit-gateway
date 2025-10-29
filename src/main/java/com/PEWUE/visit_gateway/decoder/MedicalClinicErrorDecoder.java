package com.PEWUE.visit_gateway.decoder;

import com.PEWUE.visit_gateway.exception.BadRequestException;
import com.PEWUE.visit_gateway.exception.ConflictException;
import com.PEWUE.visit_gateway.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.Charset;

public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String body = Util.toString(response.body().asReader(Charset.defaultCharset()));
            JsonNode jsonNode = mapper.readTree(body);
            String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown error";

            switch (response.status()) {
                case 400:
                    return new BadRequestException(message);
                case 404:
                    return new NotFoundException(message);
                case 409:
                    return new ConflictException(message);
                case 500:
                    return new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, message);
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
        } catch (IOException e) {
            return defaultErrorDecoder.decode(s, response);
        }
    }
}
