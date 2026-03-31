package com.atoms.backend.habit.service;

import com.atoms.backend.checkin.dto.CheckInRequest;
import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.focus.entity.FocusSessionEntity;
import com.atoms.backend.focus.repository.FocusSessionRepository;
import com.atoms.backend.habit.entity.HabitCheckInEntity;
import com.atoms.backend.habit.entity.HabitEntity;
import com.atoms.backend.habit.repository.HabitCheckInRepository;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckInService {

    private final HabitService habitService;
    private final HabitCheckInRepository habitCheckInRepository;
    private final FocusSessionRepository focusSessionRepository;
    private final UserService userService;

    public CheckInService(
            HabitService habitService,
            HabitCheckInRepository habitCheckInRepository,
            FocusSessionRepository focusSessionRepository,
            UserService userService
    ) {
        this.habitService = habitService;
        this.habitCheckInRepository = habitCheckInRepository;
        this.focusSessionRepository = focusSessionRepository;
        this.userService = userService;
    }

    @Transactional
    public Map<String, Object> createCheckIn(Long habitId, CheckInRequest request) {
        HabitEntity habit = habitService.getOwnedHabit(habitId);
        UserEntity user = userService.getCurrentUserEntity();
        LocalDate checkInDate = LocalDate.parse(request.date());
        HabitCheckInEntity entity = habitCheckInRepository.findByHabitIdAndCheckInDate(habitId, checkInDate)
                .orElseGet(HabitCheckInEntity::new);

        if (entity.getId() != null && Boolean.TRUE.equals(entity.getCompleted()) && Boolean.TRUE.equals(request.completed())) {
            throw new BusinessException(40901, "同一习惯在同一天不能重复完成打卡");
        }

        entity.setHabit(habit);
        entity.setUser(user);
        entity.setCheckInDate(checkInDate);
        entity.setCompleted(request.completed());
        entity.setCompletionSource(request.completionSource());
        entity.setFocusSession(resolveFocusSession(request.focusSessionId(), user.getId()));
        habitCheckInRepository.save(entity);

        return Map.of(
                "habitId", habitId,
                "date", request.date(),
                "completed", request.completed(),
                "streak", habitService.calculateCurrentStreak(habitId, checkInDate)
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCheckIns(Long habitId, String startDate, String endDate) {
        habitService.getOwnedHabit(habitId);
        LocalDate resolvedStartDate = startDate == null || startDate.isBlank() ? LocalDate.now().minusDays(30) : LocalDate.parse(startDate);
        LocalDate resolvedEndDate = endDate == null || endDate.isBlank() ? LocalDate.now() : LocalDate.parse(endDate);

        List<Map<String, Object>> logs = habitCheckInRepository
                .findByHabitIdAndCheckInDateBetweenOrderByCheckInDateAsc(habitId, resolvedStartDate, resolvedEndDate)
                .stream()
                .map(item -> {
                    Map<String, Object> log = new LinkedHashMap<>();
                    log.put("date", item.getCheckInDate().toString());
                    log.put("completed", item.getCompleted());
                    log.put("completionSource", item.getCompletionSource());
                    log.put("focusSessionId", item.getFocusSession() == null ? null : item.getFocusSession().getId());
                    return log;
                })
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("habitId", habitId);
        response.put("startDate", resolvedStartDate.toString());
        response.put("endDate", resolvedEndDate.toString());
        response.put("logs", logs);
        return response;
    }

    private FocusSessionEntity resolveFocusSession(Long focusSessionId, Long userId) {
        if (focusSessionId == null) {
            return null;
        }
        return focusSessionRepository.findByIdAndUserId(focusSessionId, userId)
                .orElseThrow(() -> new BusinessException(40401, "专注记录不存在"));
    }
}
