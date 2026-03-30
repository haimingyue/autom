package com.atoms.backend.reflection.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.reflection.dto.CreateReflectionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reflections")
public class ReflectionController {

    @PostMapping
    public ApiResponse<Map<String, Object>> createReflection(@Valid @RequestBody CreateReflectionRequest request) {
        return ApiResponse.success(Map.of(
                "id", 5001L,
                "habitId", request.habitId(),
                "reflectionType", request.reflectionType(),
                "promptText", request.promptText(),
                "answerText", request.answerText(),
                "createdAt", OffsetDateTime.now().toString()
        ));
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getReflections(
            @RequestParam(required = false) Long habitId,
            @RequestParam(required = false) String reflectionType,
            @RequestParam(required = false, defaultValue = "recent") String view) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("habitId", habitId);
        data.put("reflectionType", reflectionType);
        data.put("view", view);
        data.put("items", List.of());
        return ApiResponse.success(data);
    }
}
