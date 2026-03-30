package com.atoms.backend.auth.controller;

import com.atoms.backend.auth.dto.WechatLoginRequest;
import com.atoms.backend.auth.dto.WechatLoginResponse;
import com.atoms.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/wechat/login")
    public ApiResponse<WechatLoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        WechatLoginResponse response = new WechatLoginResponse(
                "mock-token",
                true,
                new WechatLoginResponse.UserInfo(
                        1L,
                        "未命名用户",
                        "",
                        "Asia/Shanghai"
                )
        );
        return ApiResponse.success(response);
    }
}
