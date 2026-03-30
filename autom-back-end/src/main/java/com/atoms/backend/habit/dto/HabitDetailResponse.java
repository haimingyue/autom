package com.atoms.backend.habit.dto;

import java.util.List;

public record HabitDetailResponse(
        Long id,
        String actionText,
        String identityText,
        String fullStatement,
        List<Integer> repeatDays,
        Integer targetValue,
        String targetUnit,
        Boolean reminderEnabled,
        String reminderTime,
        String status
) {
}
