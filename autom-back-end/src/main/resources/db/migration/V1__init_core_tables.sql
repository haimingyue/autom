CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    wechat_open_id VARCHAR(128) NULL,
    display_name VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(255) NOT NULL DEFAULT '',
    timezone VARCHAR(64) NOT NULL DEFAULT 'Asia/Shanghai',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_wechat_open_id (wechat_open_id)
);

CREATE TABLE habits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action_text VARCHAR(64) NOT NULL,
    identity_text VARCHAR(64) NOT NULL,
    full_statement VARCHAR(255) NOT NULL,
    repeat_days VARCHAR(32) NOT NULL,
    target_value INT NOT NULL,
    target_unit VARCHAR(32) NOT NULL,
    reminder_enabled BIT NOT NULL,
    reminder_time VARCHAR(8) NULL,
    status VARCHAR(16) NOT NULL,
    archived_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_habits_user_status (user_id, status),
    CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE focus_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    habit_id BIGINT NULL,
    planned_duration_minutes INT NOT NULL,
    actual_duration_minutes INT NULL,
    status VARCHAR(16) NOT NULL,
    completed BIT NOT NULL DEFAULT b'0',
    started_at DATETIME(6) NOT NULL,
    completed_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_focus_sessions_user_started_at (user_id, started_at),
    CONSTRAINT fk_focus_sessions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_focus_sessions_habit FOREIGN KEY (habit_id) REFERENCES habits (id)
);

CREATE TABLE habit_check_ins (
    id BIGINT NOT NULL AUTO_INCREMENT,
    habit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    focus_session_id BIGINT NULL,
    check_in_date DATE NOT NULL,
    completed BIT NOT NULL,
    completion_source VARCHAR(32) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_habit_check_ins_habit_date (habit_id, check_in_date),
    KEY idx_habit_check_ins_user_date (user_id, check_in_date),
    CONSTRAINT fk_habit_check_ins_habit FOREIGN KEY (habit_id) REFERENCES habits (id),
    CONSTRAINT fk_habit_check_ins_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_habit_check_ins_focus_session FOREIGN KEY (focus_session_id) REFERENCES focus_sessions (id)
);

CREATE TABLE reflections (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    habit_id BIGINT NULL,
    reflection_type VARCHAR(32) NOT NULL,
    prompt_text VARCHAR(255) NOT NULL,
    answer_text TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_reflections_user_created_at (user_id, created_at),
    CONSTRAINT fk_reflections_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reflections_habit FOREIGN KEY (habit_id) REFERENCES habits (id)
);

INSERT INTO users (id, wechat_open_id, display_name, avatar_url, timezone)
VALUES (1, 'mock-wechat-user', '未命名用户', '', 'Asia/Shanghai')
ON DUPLICATE KEY UPDATE display_name = VALUES(display_name), timezone = VALUES(timezone);
