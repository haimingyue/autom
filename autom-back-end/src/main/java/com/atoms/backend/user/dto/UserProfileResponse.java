package com.atoms.backend.user.dto;

public record UserProfileResponse(
        Long id,
        String displayName,
        String avatarUrl,
        String timezone
) {
}
