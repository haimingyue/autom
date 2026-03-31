package com.atoms.backend.auth.controller;

import com.atoms.backend.auth.dto.WechatLoginRequest;
import com.atoms.backend.auth.dto.WechatLoginResponse;
import com.atoms.backend.auth.service.AuthService;
import com.atoms.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wechat/login")
    public ApiResponse<WechatLoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        return ApiResponse.success(authService.wechatLogin(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logoutCurrentSession();
        return ApiResponse.success();
    }
}
