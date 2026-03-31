package com.atoms.backend.checkin.controller;

import com.atoms.backend.checkin.dto.CheckInRequest;
import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.habit.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/habits/{habitId}")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/check-ins")
    public ApiResponse<Map<String, Object>> createCheckIn(@PathVariable Long habitId, @Valid @RequestBody CheckInRequest request) {
        return ApiResponse.success(checkInService.createCheckIn(habitId, request));
    }

    @GetMapping("/logs")
    public ApiResponse<Map<String, Object>> getCheckIns(
            @PathVariable Long habitId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ApiResponse.success(checkInService.getCheckIns(habitId, startDate, endDate));
    }
}
