package com.PEWUE.visit_gateway.controller;

import com.PEWUE.visit_gateway.dto.DoctorDto;
import com.PEWUE.visit_gateway.dto.ErrorMessageDto;
import com.PEWUE.visit_gateway.dto.PageDto;
import com.PEWUE.visit_gateway.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(
            summary = "Get a paginated list of doctors filtered by specialization",
            description = "Returns a paginated list of doctors who match the provided specialization filter. Specialization is required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of doctors matching the specialization",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageDto.class))
            )
    })
    @GetMapping
    public PageDto<DoctorDto> getDoctors(@RequestParam(required = false) String specialization, @ParameterObject Pageable pageable) {
        log.info("GET /doctors called with specialization='{}', pageable={}", specialization, pageable);
        return doctorService.getDoctors(specialization, pageable);
    }
}
