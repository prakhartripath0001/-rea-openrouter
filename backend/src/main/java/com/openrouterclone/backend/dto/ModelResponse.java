package com.openrouterclone.backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ModelResponse(
        UUID id,
        String modelKey,
        String name,
        String description,
        Integer contextLength,
        List<ModelProviderResponse> providers,
        List<String> modalities,
        List<String> tags,
        List<String> supportedParameters,
        Instant createdAt,
        Instant updatedAt) {
}
