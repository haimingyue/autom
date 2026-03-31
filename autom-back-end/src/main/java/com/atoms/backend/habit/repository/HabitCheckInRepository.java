package com.atoms.backend.habit.repository;

import com.atoms.backend.habit.entity.HabitCheckInEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HabitCheckInRepository extends JpaRepository<HabitCheckInEntity, Long> {

    Optional<HabitCheckInEntity> findByHabitIdAndCheckInDate(Long habitId, LocalDate checkInDate);

    List<HabitCheckInEntity> findByHabitIdOrderByCheckInDateAsc(Long habitId);

    List<HabitCheckInEntity> findByHabitIdAndCheckInDateBetweenOrderByCheckInDateAsc(Long habitId, LocalDate startDate, LocalDate endDate);

    List<HabitCheckInEntity> findByHabitIdInAndCheckInDate(Collection<Long> habitIds, LocalDate checkInDate);

    List<HabitCheckInEntity> findByUserIdAndCheckInDateAndCompletedTrue(Long userId, LocalDate checkInDate);
}
