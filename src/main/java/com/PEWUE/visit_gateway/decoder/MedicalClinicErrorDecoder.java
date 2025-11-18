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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@RequiredArgsConstructor
@Component
public class MedicalClinicErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();
    private final ObjectMapper mapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = getResponseBody(response);
        String message = extractMessage(body);

        log.warn("Feign error decoding: status={}, method={}, url={}, body={}",
                response.status(),
                response.request().httpMethod(),
                response.request().url(),
                body);

        return mapToException(methodKey, response, message);
    }

    private String getResponseBody(Response response) {
        try {
            return Util.toString(response.body().asReader(Charset.defaultCharset()));
        } catch (IOException | NullPointerException e) {
            log.error("Failed to read response body", e);
            return "";
        }
    }

    private String extractMessage(String body) {
        try {
            JsonNode jsonNode = mapper.readTree(body);
            return jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown error";
        } catch (IOException e) {
            log.error("Failed to parse error message", e);
            return "Unknown error";
        }
    }

    private Exception mapToException(String methodKey, Response response, String message) {
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
                FeignException exception = feign.FeignException.errorStatus(methodKey, response);
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
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
