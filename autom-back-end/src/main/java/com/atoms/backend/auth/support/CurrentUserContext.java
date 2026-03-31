package com.atoms.backend.auth.support;

public final class CurrentUserContext {

    private static final ThreadLocal<AuthSession> SESSION_HOLDER = new ThreadLocal<>();

    private CurrentUserContext() {
    }

    public static void set(AuthSession authSession) {
        SESSION_HOLDER.set(authSession);
    }

    public static Long getUserId() {
        AuthSession session = SESSION_HOLDER.get();
        return session == null ? null : session.userId();
    }

    public static String getToken() {
        AuthSession session = SESSION_HOLDER.get();
        return session == null ? null : session.token();
    }

    public static Long getSessionId() {
        AuthSession session = SESSION_HOLDER.get();
        return session == null ? null : session.sessionId();
    }

    public static void clear() {
        SESSION_HOLDER.remove();
    }

    public record AuthSession(Long userId, Long sessionId, String token) {
    }
}
