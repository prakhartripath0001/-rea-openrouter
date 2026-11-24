package com.openrouterclone.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record ModelSearchRequest(
        String q,
        String modality,
        List<String> providers,
        List<String> tags,
        @Min(1) Integer minContextLength,
        @Min(1) Integer maxContextLength,
        @PositiveOrZero BigDecimal maxPromptPricePerMillion,
        @PositiveOrZero BigDecimal maxOutputPricePerMillion,
        Boolean freeOnly,
        Boolean discountedOnly,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size,
        String sort,
        String direction) {
}
