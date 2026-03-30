package com.atoms.backend.progress.controller;

import com.atoms.backend.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        return ApiResponse.success(Map.of(
                "totalActiveHabits", 1,
                "todayCompletedHabits", 0,
                "bestStreak", 0,
                "currentStreaks", List.of()
        ));
    }

    @GetMapping("/habits/{id}")
    public ApiResponse<Map<String, Object>> getHabitProgress(@PathVariable Long id) {
        return ApiResponse.success(Map.of(
                "habitId", id,
                "completionRate", 0,
                "currentStreak", 0,
                "bestStreak", 0,
                "totalRepetitions", 0,
                "heatmap", List.of()
        ));
    }
}
