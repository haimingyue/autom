package com.atoms.backend.reflection.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.reflection.dto.CreateReflectionRequest;
import com.atoms.backend.reflection.service.ReflectionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reflections")
public class ReflectionController {

    private final ReflectionService reflectionService;

    public ReflectionController(ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createReflection(@Valid @RequestBody CreateReflectionRequest request) {
        return ApiResponse.success(reflectionService.createReflection(request));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getReflections(
            @RequestParam(required = false) Long habitId,
            @RequestParam(required = false) String reflectionType,
            @RequestParam(required = false, defaultValue = "recent") String view) {
        return ApiResponse.success(reflectionService.getReflections(habitId, reflectionType, view));
    }
}
