package com.openrouterclone.backend.service;

public class InvalidModelQueryException extends RuntimeException {
    public InvalidModelQueryException(String message) {
        super(message);
    }
}
