package com.atoms.backend.focus.dto;

import jakarta.validation.constraints.NotNull;

public record CreateFocusSessionRequest(
        Long habitId,
        @NotNull(message = "plannedDurationMinutes 不能为空")
        Integer plannedDurationMinutes
) {
}
