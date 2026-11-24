package com.openrouterclone.backend.service;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException() {
        super("Model not found");
    }
}
