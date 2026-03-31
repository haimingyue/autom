package com.atoms.backend.auth.service;

import com.atoms.backend.auth.dto.WechatLoginRequest;
import com.atoms.backend.auth.dto.WechatLoginResponse;
import com.atoms.backend.auth.dto.WechatCode2SessionResponse;
import com.atoms.backend.auth.entity.UserSessionEntity;
import com.atoms.backend.auth.repository.UserSessionRepository;
import com.atoms.backend.auth.support.CurrentUserContext;
import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final WechatAuthClient wechatAuthClient;
    private final long sessionTtlDays;
    private final long sessionRenewBeforeHours;

    public AuthService(
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            WechatAuthClient wechatAuthClient,
            @Value("${app.security.session-ttl-days:30}") long sessionTtlDays,
            @Value("${app.security.session-renew-before-hours:72}") long sessionRenewBeforeHours
    ) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.wechatAuthClient = wechatAuthClient;
        this.sessionTtlDays = sessionTtlDays;
        this.sessionRenewBeforeHours = sessionRenewBeforeHours;
    }

    @Transactional
    public WechatLoginResponse wechatLogin(WechatLoginRequest request) {
        WechatCode2SessionResponse code2Session = wechatAuthClient.exchangeCode(request.code().trim());
        boolean isNewUser = false;

        UserEntity user = resolveUser(code2Session);
        if (user.getId() == null) {
            user.setDisplayName(defaultDisplayName(code2Session.openid()));
            user.setAvatarUrl("");
            user.setTimezone("Asia/Shanghai");
            user = userRepository.save(user);
            isNewUser = true;
        } else {
            user.setWechatOpenId(code2Session.openid());
            if (StringUtils.hasText(code2Session.unionid())) {
                user.setWechatUnionId(code2Session.unionid());
            }
            user = userRepository.save(user);
        }

        userSessionRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
        UserSessionEntity session = new UserSessionEntity();
        session.setUser(user);
        session.setToken(UUID.randomUUID().toString().replace("-", ""));
        session.setSessionKey(code2Session.sessionKey());
        session.setExpiresAt(OffsetDateTime.now().plusDays(sessionTtlDays));
        session.setLastAccessedAt(LocalDateTime.now());
        userSessionRepository.save(session);

        return new WechatLoginResponse(
                session.getToken(),
                isNewUser,
                new WechatLoginResponse.UserInfo(
                        user.getId(),
                        user.getDisplayName(),
                        user.getAvatarUrl(),
                        user.getTimezone()
                )
        );
    }

    @Transactional
    public UserSessionEntity authenticate(String token) {
        UserSessionEntity session = userSessionRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new BusinessException(40101, "未登录或 token 无效"));
        if (session.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new BusinessException(40101, "登录态已过期");
        }
        session.setLastAccessedAt(LocalDateTime.now());
        if (session.getExpiresAt().isBefore(OffsetDateTime.now().plus(sessionRenewBeforeHours, ChronoUnit.HOURS))) {
            session.setExpiresAt(OffsetDateTime.now().plusDays(sessionTtlDays));
        }
        return userSessionRepository.save(session);
    }

    @Transactional
    public void logoutCurrentSession() {
        Long sessionId = CurrentUserContext.getSessionId();
        if (sessionId == null) {
            throw new BusinessException(40101, "未登录或 token 无效");
        }
        UserSessionEntity session = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(40101, "未登录或 token 无效"));
        session.setRevokedAt(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    private UserEntity resolveUser(WechatCode2SessionResponse response) {
        if (StringUtils.hasText(response.unionid())) {
            UserEntity existing = userRepository.findByWechatUnionId(response.unionid()).orElse(null);
            if (existing != null) {
                existing.setWechatOpenId(response.openid());
                existing.setWechatUnionId(response.unionid());
                return existing;
            }
        }

        return userRepository.findByWechatOpenId(response.openid())
                .orElseGet(() -> {
                    UserEntity created = new UserEntity();
                    created.setWechatOpenId(response.openid());
                    created.setWechatUnionId(response.unionid());
                    return created;
                });
    }

    private String defaultDisplayName(String openId) {
        String suffix = openId.length() <= 6 ? openId : openId.substring(openId.length() - 6);
        return "微信用户" + suffix;
    }
}
