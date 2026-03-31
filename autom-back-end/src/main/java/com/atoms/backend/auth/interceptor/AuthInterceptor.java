package com.atoms.backend.auth.interceptor;

import com.atoms.backend.auth.entity.UserSessionEntity;
import com.atoms.backend.auth.service.AuthService;
import com.atoms.backend.auth.support.CurrentUserContext;
import com.atoms.backend.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(40101, "未登录或 token 无效");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BusinessException(40101, "未登录或 token 无效");
        }
        UserSessionEntity session = authService.authenticate(token);
        CurrentUserContext.set(new CurrentUserContext.AuthSession(session.getUser().getId(), session.getId(), session.getToken()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUserContext.clear();
    }
}
