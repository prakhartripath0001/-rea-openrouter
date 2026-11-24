package com.openrouterclone.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openrouterclone.backend.dto.FilterOptionsResponse;
import com.openrouterclone.backend.service.FilterService;

@RestController
@RequestMapping("/api/v1/filters")
public class FilterController {
    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    @GetMapping
    public FilterOptionsResponse getOptions() {
        return filterService.getOptions();
    }
}
