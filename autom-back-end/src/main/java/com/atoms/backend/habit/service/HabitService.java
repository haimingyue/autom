package com.atoms.backend.habit.service;

import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.habit.dto.CreateHabitRequest;
import com.atoms.backend.habit.dto.HabitDetailResponse;
import com.atoms.backend.habit.dto.TodayHabitsResponse;
import com.atoms.backend.habit.entity.HabitCheckInEntity;
import com.atoms.backend.habit.entity.HabitEntity;
import com.atoms.backend.habit.repository.HabitCheckInRepository;
import com.atoms.backend.habit.repository.HabitRepository;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HabitService {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_ARCHIVED = "archived";

    private final HabitRepository habitRepository;
    private final HabitCheckInRepository habitCheckInRepository;
    private final UserService userService;

    public HabitService(
            HabitRepository habitRepository,
            HabitCheckInRepository habitCheckInRepository,
            UserService userService
    ) {
        this.habitRepository = habitRepository;
        this.habitCheckInRepository = habitCheckInRepository;
        this.userService = userService;
    }

    @Transactional
    public HabitDetailResponse createHabit(CreateHabitRequest request) {
        UserEntity user = userService.getCurrentUserEntity();
        HabitEntity habit = new HabitEntity();
        habit.setUser(user);
        habit.setStatus(STATUS_ACTIVE);
        applyRequest(habit, request);
        return toDetailResponse(habitRepository.save(habit));
    }

    @Transactional(readOnly = true)
    public TodayHabitsResponse getTodayHabits() {
        UserEntity user = userService.getCurrentUserEntity();
        LocalDate today = LocalDate.now();
        int currentDay = toRepeatDay(today.getDayOfWeek());
        List<HabitEntity> habits = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), STATUS_ACTIVE)
                .stream()
                .filter(habit -> parseRepeatDays(habit.getRepeatDays()).contains(currentDay))
                .toList();

        Map<Long, HabitCheckInEntity> todayCheckIns = habits.isEmpty()
                ? Map.of()
                : habitCheckInRepository.findByHabitIdInAndCheckInDate(habits.stream().map(HabitEntity::getId).toList(), today)
                .stream()
                .collect(Collectors.toMap(item -> item.getHabit().getId(), item -> item));

        List<TodayHabitsResponse.TodayHabitItem> items = habits.stream()
                .map(habit -> {
                    HabitCheckInEntity checkIn = todayCheckIns.get(habit.getId());
                    return new TodayHabitsResponse.TodayHabitItem(
                            habit.getId(),
                            habit.getActionText(),
                            habit.getIdentityText(),
                            habit.getFullStatement(),
                            checkIn != null && Boolean.TRUE.equals(checkIn.getCompleted()),
                            calculateCurrentStreak(habit.getId(), today),
                            habit.getReminderTime()
                    );
                })
                .toList();

        return new TodayHabitsResponse(today.toString(), items);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getHabits(String status) {
        UserEntity user = userService.getCurrentUserEntity();
        String normalizedStatus = normalizeStatus(status);
        List<HabitDetailResponse> items = habitRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), normalizedStatus)
                .stream()
                .map(this::toDetailResponse)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", normalizedStatus);
        response.put("items", items);
        return response;
    }

    @Transactional(readOnly = true)
    public HabitDetailResponse getHabitDetail(Long id) {
        return toDetailResponse(getOwnedHabit(id));
    }

    @Transactional
    public HabitDetailResponse updateHabit(Long id, CreateHabitRequest request) {
        HabitEntity habit = getOwnedHabit(id);
        applyRequest(habit, request);
        return toDetailResponse(habitRepository.save(habit));
    }

    @Transactional
    public Map<String, Object> archiveHabit(Long id) {
        HabitEntity habit = getOwnedHabit(id);
        habit.setStatus(STATUS_ARCHIVED);
        habit.setArchivedAt(OffsetDateTime.now());
        habitRepository.save(habit);
        return Map.of("habitId", id, "status", STATUS_ARCHIVED);
    }

    @Transactional(readOnly = true)
    public HabitEntity getOwnedHabit(Long id) {
        Long userId = userService.getCurrentUserEntity().getId();
        return habitRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(40401, "习惯不存在"));
    }

    @Transactional(readOnly = true)
    public int calculateCurrentStreak(Long habitId, LocalDate endDate) {
        Set<LocalDate> completedDates = habitCheckInRepository.findByHabitIdOrderByCheckInDateAsc(habitId).stream()
                .filter(item -> Boolean.TRUE.equals(item.getCompleted()))
                .map(HabitCheckInEntity::getCheckInDate)
                .collect(Collectors.toSet());
        int streak = 0;
        LocalDate cursor = endDate;
        while (completedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    @Transactional(readOnly = true)
    public List<Integer> parseRepeatDays(String repeatDays) {
        if (repeatDays == null || repeatDays.isBlank()) {
            return List.of();
        }
        return Arrays.stream(repeatDays.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private void applyRequest(HabitEntity habit, CreateHabitRequest request) {
        habit.setActionText(request.actionText());
        habit.setIdentityText(request.identityText());
        habit.setFullStatement(request.fullStatement());
        habit.setRepeatDays(request.repeatDays().stream().sorted().map(String::valueOf).collect(Collectors.joining(",")));
        habit.setTargetValue(request.targetValue());
        habit.setTargetUnit(request.targetUnit());
        habit.setReminderEnabled(request.reminderEnabled());
        habit.setReminderTime(request.reminderTime());
    }

    private HabitDetailResponse toDetailResponse(HabitEntity habit) {
        return new HabitDetailResponse(
                habit.getId(),
                habit.getActionText(),
                habit.getIdentityText(),
                habit.getFullStatement(),
                parseRepeatDays(habit.getRepeatDays()),
                habit.getTargetValue(),
                habit.getTargetUnit(),
                habit.getReminderEnabled(),
                habit.getReminderTime(),
                habit.getStatus()
        );
    }

    private String normalizeStatus(String status) {
        return STATUS_ARCHIVED.equalsIgnoreCase(status) ? STATUS_ARCHIVED : STATUS_ACTIVE;
    }

    private int toRepeatDay(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };
    }
}
