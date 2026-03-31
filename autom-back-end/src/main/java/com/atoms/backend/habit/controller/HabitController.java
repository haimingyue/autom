package com.atoms.backend.habit.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.habit.dto.CreateHabitRequest;
import com.atoms.backend.habit.dto.HabitDetailResponse;
import com.atoms.backend.habit.dto.TodayHabitsResponse;
import com.atoms.backend.habit.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    public ApiResponse<HabitDetailResponse> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        return ApiResponse.success(habitService.createHabit(request));
    }

    @GetMapping("/today")
    public ApiResponse<TodayHabitsResponse> getTodayHabits() {
        return ApiResponse.success(habitService.getTodayHabits());
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getHabits(@RequestParam(required = false, defaultValue = "active") String status) {
        return ApiResponse.success(habitService.getHabits(status));
    }

    @GetMapping("/{id}")
    public ApiResponse<HabitDetailResponse> getHabitDetail(@PathVariable Long id) {
        return ApiResponse.success(habitService.getHabitDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<HabitDetailResponse> updateHabit(@PathVariable Long id, @Valid @RequestBody CreateHabitRequest request) {
        return ApiResponse.success(habitService.updateHabit(id, request));
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<Map<String, Object>> archiveHabit(@PathVariable Long id) {
        return ApiResponse.success(habitService.archiveHabit(id));
    }
}
