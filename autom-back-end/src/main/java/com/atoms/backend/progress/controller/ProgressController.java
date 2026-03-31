package com.atoms.backend.progress.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.progress.service.ProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        return ApiResponse.success(progressService.getOverview());
    }

    @GetMapping("/habits/{id}")
    public ApiResponse<Map<String, Object>> getHabitProgress(@PathVariable Long id) {
        return ApiResponse.success(progressService.getHabitProgress(id));
    }
}
