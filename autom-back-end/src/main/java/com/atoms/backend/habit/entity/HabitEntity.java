package com.atoms.backend.habit.entity;

import com.atoms.backend.common.persistence.BaseEntity;
import com.atoms.backend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "habits")
public class HabitEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "action_text", nullable = false, length = 64)
    private String actionText;

    @Column(name = "identity_text", nullable = false, length = 64)
    private String identityText;

    @Column(name = "full_statement", nullable = false, length = 255)
    private String fullStatement;

    @Column(name = "repeat_days", nullable = false, length = 32)
    private String repeatDays;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Column(name = "target_unit", nullable = false, length = 32)
    private String targetUnit;

    @Column(name = "reminder_enabled", nullable = false)
    private Boolean reminderEnabled;

    @Column(name = "reminder_time", length = 8)
    private String reminderTime;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;
}
