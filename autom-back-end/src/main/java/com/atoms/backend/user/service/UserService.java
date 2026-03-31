package com.atoms.backend.user.service;

import com.atoms.backend.auth.support.CurrentUserProvider;
import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.user.dto.UpdateCurrentUserRequest;
import com.atoms.backend.user.dto.UserProfileResponse;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    public UserService(UserRepository userRepository, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {
        return toResponse(getCurrentUserEntity());
    }

    @Transactional
    public UserProfileResponse updateCurrentUser(UpdateCurrentUserRequest request) {
        UserEntity user = getCurrentUserEntity();
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl() == null ? "" : request.avatarUrl());
        user.setTimezone(request.timezone());
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserEntity getCurrentUserEntity() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(40401, "当前用户不存在"));
    }

    private UserProfileResponse toResponse(UserEntity user) {
        return new UserProfileResponse(user.getId(), user.getDisplayName(), user.getAvatarUrl(), user.getTimezone());
    }
}
