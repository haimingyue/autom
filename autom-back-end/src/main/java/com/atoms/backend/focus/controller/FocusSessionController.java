package com.atoms.backend.focus.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.focus.dto.CompleteFocusSessionRequest;
import com.atoms.backend.focus.dto.CreateFocusSessionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/focus-sessions")
public class FocusSessionController {

    @PostMapping
    public ApiResponse<Map<String, Object>> createFocusSession(@Valid @RequestBody CreateFocusSessionRequest request) {
        return ApiResponse.success(Map.of(
                "id", 2001L,
                "habitId", request.habitId(),
                "plannedDurationMinutes", request.plannedDurationMinutes(),
                "status", "started"
        ));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Map<String, Object>> completeFocusSession(@PathVariable Long id, @Valid @RequestBody CompleteFocusSessionRequest request) {
        return ApiResponse.success(Map.of(
                "id", id,
                "actualDurationMinutes", request.actualDurationMinutes(),
                "completed", request.completed()
        ));
    }

    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayFocusSessions() {
        return ApiResponse.success(Map.of("items", List.of()));
    }
}
