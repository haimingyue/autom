package com.atoms.backend.auth.service;

import com.atoms.backend.auth.dto.WechatLoginRequest;
import com.atoms.backend.auth.dto.WechatLoginResponse;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public WechatLoginResponse wechatLogin(WechatLoginRequest request) {
        UserEntity user = userService.getCurrentUserEntity();
        return new WechatLoginResponse(
                "mock-token-" + request.code(),
                false,
                new WechatLoginResponse.UserInfo(
                        user.getId(),
                        user.getDisplayName(),
                        user.getAvatarUrl(),
                        user.getTimezone()
                )
        );
    }
}
