package com.atoms.backend.auth.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final Long mockUserId;

    public CurrentUserProvider(@Value("${app.security.mock-user-id:1}") Long mockUserId) {
        this.mockUserId = mockUserId;
    }

    public Long getCurrentUserId() {
        return mockUserId;
    }
}
