package com.atoms.backend.user.controller;

import com.atoms.backend.common.api.ApiResponse;
import com.atoms.backend.user.dto.UpdateCurrentUserRequest;
import com.atoms.backend.user.dto.UserProfileResponse;
import com.atoms.backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateCurrentUser(@Valid @RequestBody UpdateCurrentUserRequest request) {
        return ApiResponse.success(userService.updateCurrentUser(request));
    }
}
