package com.openrouterclone.backend.service;

import java.util.UUID;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(UUID id) {
        super("Model not found: " + id);
    }
}
