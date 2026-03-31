package com.atoms.backend.reflection.service;

import com.atoms.backend.common.exception.BusinessException;
import com.atoms.backend.habit.entity.HabitEntity;
import com.atoms.backend.habit.service.HabitService;
import com.atoms.backend.reflection.dto.CreateReflectionRequest;
import com.atoms.backend.reflection.entity.ReflectionEntity;
import com.atoms.backend.reflection.repository.ReflectionRepository;
import com.atoms.backend.user.entity.UserEntity;
import com.atoms.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReflectionService {

    private final ReflectionRepository reflectionRepository;
    private final HabitService habitService;
    private final UserService userService;

    public ReflectionService(
            ReflectionRepository reflectionRepository,
            HabitService habitService,
            UserService userService
    ) {
        this.reflectionRepository = reflectionRepository;
        this.habitService = habitService;
        this.userService = userService;
    }

    @Transactional
    public Map<String, Object> createReflection(CreateReflectionRequest request) {
        ReflectionEntity entity = new ReflectionEntity();
        UserEntity user = userService.getCurrentUserEntity();
        entity.setUser(user);
        entity.setHabit(resolveHabit(request.habitId()));
        entity.setReflectionType(request.reflectionType());
        entity.setPromptText(request.promptText());
        entity.setAnswerText(request.answerText());
        ReflectionEntity saved = reflectionRepository.save(entity);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("habitId", saved.getHabit() == null ? null : saved.getHabit().getId());
        response.put("reflectionType", saved.getReflectionType());
        response.put("promptText", saved.getPromptText());
        response.put("answerText", saved.getAnswerText());
        response.put("createdAt", saved.getCreatedAt());
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReflections(Long habitId, String reflectionType, String view) {
        Long userId = userService.getCurrentUserEntity().getId();
        long limit = "recent".equalsIgnoreCase(view) ? 20 : Long.MAX_VALUE;
        List<Map<String, Object>> items = reflectionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(item -> habitId == null || (item.getHabit() != null && habitId.equals(item.getHabit().getId())))
                .filter(item -> reflectionType == null || reflectionType.isBlank() || reflectionType.equals(item.getReflectionType()))
                .limit(limit)
                .map(item -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", item.getId());
                    row.put("habitId", item.getHabit() == null ? null : item.getHabit().getId());
                    row.put("reflectionType", item.getReflectionType());
                    row.put("promptText", item.getPromptText());
                    row.put("answerText", item.getAnswerText());
                    row.put("createdAt", item.getCreatedAt());
                    return row;
                })
                .toList();
        return Map.of("items", items);
    }

    private HabitEntity resolveHabit(Long habitId) {
        if (habitId == null) {
            return null;
        }
        HabitEntity habit = habitService.getOwnedHabit(habitId);
        if (HabitService.STATUS_ARCHIVED.equals(habit.getStatus())) {
            throw new BusinessException(40901, "已归档习惯不能新增反思");
        }
        return habit;
    }
}
