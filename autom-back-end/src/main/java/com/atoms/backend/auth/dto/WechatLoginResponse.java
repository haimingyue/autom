package com.atoms.backend.auth.dto;

public record WechatLoginResponse(
        String token,
        boolean isNewUser,
        UserInfo user
) {
    public record UserInfo(
            Long id,
            String displayName,
            String avatarUrl,
            String timezone
    ) {
    }
}
