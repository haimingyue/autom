ALTER TABLE users
    ADD COLUMN wechat_union_id VARCHAR(128) NULL AFTER wechat_open_id,
    ADD UNIQUE KEY uk_users_wechat_union_id (wechat_union_id);

ALTER TABLE user_sessions
    ADD COLUMN session_key VARCHAR(255) NULL AFTER token,
    ADD COLUMN last_accessed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) AFTER expires_at,
    ADD COLUMN revoked_at DATETIME(6) NULL AFTER last_accessed_at;
