package com.atoms.backend.reflection.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReflectionRequest(
        Long habitId,
        @NotBlank(message = "reflectionType 不能为空")
        String reflectionType,
        @NotBlank(message = "promptText 不能为空")
        String promptText,
        @NotBlank(message = "answerText 不能为空")
        String answerText
) {
}
