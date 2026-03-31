package com.atoms.backend.auth.repository;

import com.atoms.backend.auth.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findByTokenAndRevokedAtIsNull(String token);

    void deleteByExpiresAtBefore(OffsetDateTime expiresAt);
}
