package com.atoms.backend.habit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateHabitRequest(
        @NotBlank(message = "actionText 不能为空")
        String actionText,
        @NotBlank(message = "identityText 不能为空")
        String identityText,
        @NotBlank(message = "fullStatement 不能为空")
        String fullStatement,
        @NotEmpty(message = "repeatDays 不能为空")
        List<@NotNull @Min(value = 1, message = "repeatDays 最小为 1") @Max(value = 7, message = "repeatDays 最大为 7") Integer> repeatDays,
        @NotNull(message = "targetValue 不能为空")
        Integer targetValue,
        @NotBlank(message = "targetUnit 不能为空")
        String targetUnit,
        @NotNull(message = "reminderEnabled 不能为空")
        Boolean reminderEnabled,
        String reminderTime
) {
}
