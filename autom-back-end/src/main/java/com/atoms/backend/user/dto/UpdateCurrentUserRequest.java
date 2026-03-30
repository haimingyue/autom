package com.atoms.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCurrentUserRequest(
        @NotBlank(message = "displayName 不能为空")
        String displayName,
        String avatarUrl,
        @NotBlank(message = "timezone 不能为空")
        String timezone
) {
}
