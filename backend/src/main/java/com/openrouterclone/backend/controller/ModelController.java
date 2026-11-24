package com.openrouterclone.backend.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openrouterclone.backend.dto.ModelResponse;
import com.openrouterclone.backend.dto.ModelSearchRequest;
import com.openrouterclone.backend.dto.PageResponse;
import com.openrouterclone.backend.service.ModelService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/models")
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public PageResponse<ModelResponse> list(@Valid @ModelAttribute ModelSearchRequest request) {
        return modelService.list(request);
    }

    @GetMapping("/{id}")
    public ModelResponse get(@PathVariable UUID id) {
        return modelService.get(id);
    }
}
