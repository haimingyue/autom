package com.atoms.backend.checkin.controller;

import com.atoms.backend.checkin.dto.CheckInRequest;
import com.atoms.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/habits/{habitId}")
public class CheckInController {

    @PostMapping("/check-ins")
    public ApiResponse<Map<String, Object>> createCheckIn(@PathVariable Long habitId, @Valid @RequestBody CheckInRequest request) {
        return ApiResponse.success(Map.of(
                "habitId", habitId,
                "date", request.date(),
                "completed", request.completed(),
                "streak", 1
        ));
    }

    @GetMapping("/logs")
    public ApiResponse<Map<String, Object>> getCheckIns(
            @PathVariable Long habitId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(Map.of(
                "habitId", habitId,
                "startDate", startDate,
                "endDate", endDate,
                "logs", List.of()
        ));
    }
}
