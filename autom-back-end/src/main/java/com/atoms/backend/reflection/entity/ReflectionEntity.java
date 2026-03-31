package com.atoms.backend.reflection.entity;

import com.atoms.backend.common.persistence.BaseEntity;
import com.atoms.backend.habit.entity.HabitEntity;
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

@Getter
@Setter
@Entity
@Table(name = "reflections")
public class ReflectionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private HabitEntity habit;

    @Column(name = "reflection_type", nullable = false, length = 32)
    private String reflectionType;

    @Column(name = "prompt_text", nullable = false, length = 255)
    private String promptText;

    @Column(name = "answer_text", nullable = false, columnDefinition = "text")
    private String answerText;
}
