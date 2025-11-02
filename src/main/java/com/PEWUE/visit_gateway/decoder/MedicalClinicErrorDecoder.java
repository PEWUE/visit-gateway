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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String body = Util.toString(response.body().asReader(Charset.defaultCharset()));
            JsonNode jsonNode = mapper.readTree(body);
            String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown error";

            log.warn("Feign error decoding: status={}, method={}, url={}, body={}",
                    response.status(),
                    response.request().httpMethod(),
                    response.request().url(),
                    body);

                    switch (response.status()) {
                case 400:
                    log.error("Decoded 400 BAD REQUEST from backend, message: {}", message);
                    return new BadRequestException(message);
                case 404:
                    log.error("Decoded 404 NOT FOUND from backend, message: {}", message);
                    return new NotFoundException(message);
                case 409:
                    log.error("Decoded 409 CONFLICT from backend, message: {}", message);
                    return new ConflictException(message);
                case 500:
                    log.error("Decoded 500 INTERNAL SERVER ERROR from backend, message: {}", message);
                    return new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, message);
                case 503:
                    log.error("Decoded 503 SERVICE UNAVAILABLE from backend, message: {}", message);
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
                    log.warn("Decoded unhandled status {} from backend, returning default decoder", response.status());
                    return defaultErrorDecoder.decode(s, response);
            }
        } catch (IOException e) {
            log.error("Feign error decoder failed to parse body, returning default decoder", e);
            return defaultErrorDecoder.decode(s, response);
        }
    }
}
