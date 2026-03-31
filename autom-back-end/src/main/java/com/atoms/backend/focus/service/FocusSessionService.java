package com.atoms.backend.focus.service;

import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.focus.dto.CompleteFocusSessionRequest;
import com.atoms.backend.focus.dto.CreateFocusSessionRequest;
import com.atoms.backend.focus.entity.FocusSessionEntity;
import com.atoms.backend.focus.repository.FocusSessionRepository;
import com.atoms.backend.habit.entity.HabitEntity;
import com.atoms.backend.habit.service.HabitService;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final HabitService habitService;
    private final UserService userService;

    public FocusSessionService(
            FocusSessionRepository focusSessionRepository,
            HabitService habitService,
            UserService userService
    ) {
        this.focusSessionRepository = focusSessionRepository;
        this.habitService = habitService;
        this.userService = userService;
    }

    @Transactional
    public Map<String, Object> createFocusSession(CreateFocusSessionRequest request) {
        UserEntity user = userService.getCurrentUserEntity();
        FocusSessionEntity entity = new FocusSessionEntity();
        entity.setUser(user);
        entity.setHabit(resolveHabit(request.habitId()));
        entity.setPlannedDurationMinutes(request.plannedDurationMinutes());
        entity.setStatus("started");
        entity.setCompleted(false);
        entity.setStartedAt(OffsetDateTime.now());
        FocusSessionEntity saved = focusSessionRepository.save(entity);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("habitId", saved.getHabit() == null ? null : saved.getHabit().getId());
        response.put("plannedDurationMinutes", saved.getPlannedDurationMinutes());
        response.put("status", saved.getStatus());
        return response;
    }

    @Transactional
    public Map<String, Object> completeFocusSession(Long id, CompleteFocusSessionRequest request) {
        Long userId = userService.getCurrentUserEntity().getId();
        FocusSessionEntity entity = focusSessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(40401, "专注记录不存在"));
        entity.setActualDurationMinutes(request.actualDurationMinutes());
        entity.setCompleted(request.completed());
        entity.setStatus(request.completed() ? "completed" : "abandoned");
        entity.setCompletedAt(OffsetDateTime.now());
        focusSessionRepository.save(entity);
        return Map.of(
                "id", entity.getId(),
                "actualDurationMinutes", entity.getActualDurationMinutes(),
                "completed", entity.getCompleted()
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTodayFocusSessions() {
        UserEntity user = userService.getCurrentUserEntity();
        ZoneId zoneId = ZoneId.of(user.getTimezone());
        LocalDate today = LocalDate.now(zoneId);
        OffsetDateTime start = today.atStartOfDay(zoneId).toOffsetDateTime();
        OffsetDateTime end = today.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime().minusNanos(1);

        List<Map<String, Object>> items = focusSessionRepository.findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(user.getId(), start, end)
                .stream()
                .map(item -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", item.getId());
                    row.put("habitId", item.getHabit() == null ? null : item.getHabit().getId());
                    row.put("plannedDurationMinutes", item.getPlannedDurationMinutes());
                    row.put("actualDurationMinutes", item.getActualDurationMinutes());
                    row.put("status", item.getStatus());
                    row.put("completed", item.getCompleted());
                    row.put("startedAt", item.getStartedAt());
                    row.put("completedAt", item.getCompletedAt());
                    return row;
                })
                .toList();
        return Map.of("items", items);
    }

    private HabitEntity resolveHabit(Long habitId) {
        if (habitId == null) {
            return null;
        }
        return habitService.getOwnedHabit(habitId);
    }
}
