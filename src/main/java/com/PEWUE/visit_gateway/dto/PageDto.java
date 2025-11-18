package com.PEWUE.visit_gateway.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
