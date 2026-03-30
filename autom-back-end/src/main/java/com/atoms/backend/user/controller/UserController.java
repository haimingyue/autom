package com.atoms.backend.user.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.user.dto.UpdateCurrentUserRequest;
import com.atoms.backend.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser() {
        return ApiResponse.success(mockProfile());
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateCurrentUser(@Valid @RequestBody UpdateCurrentUserRequest request) {
        UserProfileResponse response = new UserProfileResponse(
                1L,
                request.displayName(),
                request.avatarUrl(),
                request.timezone()
        );
        return ApiResponse.success(response);
    }

    private UserProfileResponse mockProfile() {
        return new UserProfileResponse(1L, "未命名用户", "", "Asia/Shanghai");
    }
}
