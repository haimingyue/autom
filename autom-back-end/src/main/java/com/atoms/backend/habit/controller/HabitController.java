package com.atoms.backend.habit.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.habit.dto.CreateHabitRequest;
import com.atoms.backend.habit.dto.HabitDetailResponse;
import com.atoms.backend.habit.dto.TodayHabitsResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/habits")
public class HabitController {

    @PostMapping
    public ApiResponse<HabitDetailResponse> createHabit(@Valid @RequestBody CreateHabitRequest request) {
        return ApiResponse.success(toResponse(101L, request, "active"));
    }

    @GetMapping("/today")
    public ApiResponse<TodayHabitsResponse> getTodayHabits() {
        TodayHabitsResponse response = new TodayHabitsResponse(
                LocalDate.now().toString(),
                List.of(
                        new TodayHabitsResponse.TodayHabitItem(
                                101L,
                                "穿上跑鞋",
                                "成为一个健康的人",
                                "我将在每天早上8点穿上跑鞋，这样我能成为一个健康的人",
                                false,
                                0,
                                "08:00"
                        )
                )
        );
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getHabits(@RequestParam(required = false, defaultValue = "active") String status) {
        return ApiResponse.success(Map.of("status", status, "items", List.of()));
    }

    @GetMapping("/{id}")
    public ApiResponse<HabitDetailResponse> getHabitDetail(@PathVariable Long id) {
        CreateHabitRequest request = new CreateHabitRequest(
                "穿上跑鞋",
                "成为一个健康的人",
                "我将在每天早上8点穿上跑鞋，这样我能成为一个健康的人",
                List.of(1, 2, 3, 4, 5, 6, 7),
                1,
                "time",
                true,
                "08:00"
        );
        return ApiResponse.success(toResponse(id, request, "active"));
    }

    @PutMapping("/{id}")
    public ApiResponse<HabitDetailResponse> updateHabit(@PathVariable Long id, @Valid @RequestBody CreateHabitRequest request) {
        return ApiResponse.success(toResponse(id, request, "active"));
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<Map<String, Object>> archiveHabit(@PathVariable Long id) {
        return ApiResponse.success(Map.of("habitId", id, "status", "archived"));
    }

    private HabitDetailResponse toResponse(Long id, CreateHabitRequest request, String status) {
        return new HabitDetailResponse(
                id,
                request.actionText(),
                request.identityText(),
                request.fullStatement(),
                request.repeatDays(),
                request.targetValue(),
                request.targetUnit(),
                request.reminderEnabled(),
                request.reminderTime(),
                status
        );
    }
}
