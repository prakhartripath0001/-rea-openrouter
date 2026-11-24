package com.openrouterclone.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ModelSearchRequest(
        @Size(max = 200) String q,
        @Size(max = 40) String modality,
        @Size(max = 20) List<@Size(max = 100) String> providers,
        @Size(max = 30) List<@Size(max = 80) String> tags,
        @Min(1) Integer minContextLength,
        @Min(1) Integer maxContextLength,
        @PositiveOrZero @Digits(integer = 12, fraction = 6) BigDecimal maxPromptPricePerMillion,
        @PositiveOrZero @Digits(integer = 12, fraction = 6) BigDecimal maxOutputPricePerMillion,
        Boolean freeOnly,
        Boolean discountedOnly,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size,
        @Size(max = 40) String sort,
        @Size(max = 8) String direction) {
}
