package com.atoms.backend.habit.dto;

import java.util.List;

public record TodayHabitsResponse(
        String date,
        List<TodayHabitItem> items
) {
    public record TodayHabitItem(
            Long habitId,
            String actionText,
            String identityText,
            String fullStatement,
            Boolean completed,
            Integer streak,
            String reminderTime
    ) {
    }
}
