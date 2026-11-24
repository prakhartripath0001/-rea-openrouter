package com.openrouterclone.backend.dto;

import java.util.UUID;

public record ProviderResponse(
        UUID id,
        String slug,
        String name,
        String description,
        long modelCount) {
}
