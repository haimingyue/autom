package com.atoms.backend.checkin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotBlank(message = "date 不能为空")
        String date,
        @NotNull(message = "completed 不能为空")
        Boolean completed,
        @NotBlank(message = "completionSource 不能为空")
        String completionSource,
        Long focusSessionId
) {
}
