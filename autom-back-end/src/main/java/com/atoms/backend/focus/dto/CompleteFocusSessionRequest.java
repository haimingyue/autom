package com.atoms.backend.focus.dto;

import jakarta.validation.constraints.NotNull;

public record CompleteFocusSessionRequest(
        @NotNull(message = "actualDurationMinutes 不能为空")
        Integer actualDurationMinutes,
        @NotNull(message = "completed 不能为空")
        Boolean completed
) {
}
