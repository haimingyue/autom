package com.atoms.backend.auth.support;

import com.atoms.backend.common.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getCurrentUserId() {
        Long userId = CurrentUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(40101, "未登录或 token 无效");
        }
        return userId;
    }
}
