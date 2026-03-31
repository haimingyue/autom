package com.atoms.backend.focus.repository;

import com.atoms.backend.focus.entity.FocusSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FocusSessionRepository extends JpaRepository<FocusSessionEntity, Long> {

    Optional<FocusSessionEntity> findByIdAndUserId(Long id, Long userId);

    List<FocusSessionEntity> findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(
            Long userId,
            OffsetDateTime startedAtFrom,
            OffsetDateTime startedAtTo
    );
}
