package com.atoms.backend.progress.service;

import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.habit.entity.HabitCheckInEntity;
import com.atoms.backend.habit.entity.HabitEntity;
import com.atoms.backend.habit.repository.HabitCheckInRepository;
import com.atoms.backend.habit.repository.HabitRepository;
import com.atoms.backend.habit.service.HabitService;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository habitCheckInRepository;
    private final HabitService habitService;
    private final UserService userService;

    public ProgressService(
            HabitRepository habitRepository,
            HabitCheckInRepository habitCheckInRepository,
            HabitService habitService,
            UserService userService
    ) {
        this.habitRepository = habitRepository;
        this.habitCheckInRepository = habitCheckInRepository;
        this.habitService = habitService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOverview() {
        UserEntity user = userService.getCurrentUserEntity();
        LocalDate today = LocalDate.now();
        List<HabitEntity> habits = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), HabitService.STATUS_ACTIVE);

        List<Map<String, Object>> currentStreaks = habits.stream()
                .map(habit -> Map.of("habitId", (Object) habit.getId(), "streak", habitService.calculateCurrentStreak(habit.getId(), today)))
                .filter(item -> ((Integer) item.get("streak")) > 0)
                .toList();

        int bestStreak = habits.stream()
                .mapToInt(habit -> calculateBestStreak(habit.getId()))
                .max()
                .orElse(0);

        long todayCompletedHabits = habitCheckInRepository.findByUserIdAndCheckInDateAndCompletedTrue(user.getId(), today).stream()
                .map(item -> item.getHabit().getId())
                .distinct()
                .count();

        return Map.of(
                "totalActiveHabits", habits.size(),
                "todayCompletedHabits", todayCompletedHabits,
                "bestStreak", bestStreak,
                "currentStreaks", currentStreaks
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHabitProgress(Long id) {
        HabitEntity habit = habitService.getOwnedHabit(id);
        List<HabitCheckInEntity> checkIns = habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(id);
        Set<LocalDate> completedDates = checkIns.stream()
                .filter(item -> Boolean.TRUE.equals(item.getCompleted()))
                .map(HabitCheckInEntity::getCheckInDate)
                .collect(Collectors.toSet());
        LocalDate startDate = habit.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();
        int totalRepetitions = countDueDays(habit, startDate, today);
        int completedCount = (int) completedDates.stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(today))
                .count();
        int completionRate = totalRepetitions == 0 ? 0 : Math.toIntExact(Math.round(completedCount * 100.0 / totalRepetitions));

        List<Map<String, Object>> heatmap = checkIns.stream()
                .map(item -> Map.of("date", (Object) item.getCheckInDate().toString(), "completed", item.getCompleted()))
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("habitId", id);
        response.put("completionRate", completionRate);
        response.put("currentStreak", habitService.calculateCurrentStreak(id, today));
        response.put("bestStreak", calculateBestStreak(id));
        response.put("totalRepetitions", totalRepetitions);
        response.put("heatmap", heatmap);
        return response;
    }

    private int calculateBestStreak(Long habitId) {
        List<LocalDate> completedDates = habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId).stream()
                .filter(item -> Boolean.TRUE.equals(item.getCompleted()))
                .map(HabitCheckInEntity::getCheckInDate)
                .toList();
        int best = 0;
        int current = 0;
        LocalDate previous = null;
        for (LocalDate date : completedDates) {
            if (previous != null && previous.plusDays(1).equals(date)) {
                current++;
            } else {
                current = 1;
            }
            best = Math.max(best, current);
            previous = date;
        }
        return best;
    }

    private int countDueDays(HabitEntity habit, LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(40001, "进度日期范围非法");
        }
        List<Integer> repeatDays = habitService.parseRepeatDays(habit.getRepeatDays());
        int total = 0;
        for (LocalDate cursor = startDate; !cursor.isAfter(endDate); cursor = cursor.plusDays(1)) {
            int day = switch (cursor.getDayOfWeek()) {
                case MONDAY -> 1;
                case TUESDAY -> 2;
                case WEDNESDAY -> 3;
                case THURSDAY -> 4;
                case FRIDAY -> 5;
                case SATURDAY -> 6;
                case SUNDAY -> 7;
            };
            if (repeatDays.contains(day)) {
                total++;
            }
        }
        return total;
    }
}
