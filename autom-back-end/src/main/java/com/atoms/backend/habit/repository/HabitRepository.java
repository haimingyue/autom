package com.atoms.backend.habit.repository;

import com.atoms.backend.habit.entity.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<HabitEntity, Long> {

    List<HabitEntity> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    Optional<HabitEntity> findByIdAndUserId(Long id, Long userId);
}
