package com.atoms.backend.focus.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.focus.dto.CompleteFocusSessionRequest;
import com.atoms.backend.focus.dto.CreateFocusSessionRequest;
import com.atoms.backend.focus.service.FocusSessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/focus-sessions")
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    public FocusSessionController(FocusSessionService focusSessionService) {
        this.focusSessionService = focusSessionService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createFocusSession(@Valid @RequestBody CreateFocusSessionRequest request) {
        return ApiResponse.success(focusSessionService.createFocusSession(request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Map<String, Object>> completeFocusSession(@PathVariable Long id, @Valid @RequestBody CompleteFocusSessionRequest request) {
        return ApiResponse.success(focusSessionService.completeFocusSession(id, request));
    }

    @GetMapping("/today")
    public ApiResponse<Map<String, Object>> getTodayFocusSessions() {
        return ApiResponse.success(focusSessionService.getTodayFocusSessions());
    }
}
