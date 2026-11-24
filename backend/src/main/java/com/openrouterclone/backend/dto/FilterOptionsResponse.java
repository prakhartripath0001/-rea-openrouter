package com.openrouterclone.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record FilterOptionsResponse(
        List<String> modalities,
        List<ProviderResponse> providers,
        List<String> tags,
        List<String> supportedParameters,
        Integer minContextLength,
        Integer maxContextLength,
        BigDecimal minPromptRatePerMillion,
        BigDecimal maxPromptRatePerMillion,
        BigDecimal minOutputRatePerMillion,
        BigDecimal maxOutputRatePerMillion) {
}
