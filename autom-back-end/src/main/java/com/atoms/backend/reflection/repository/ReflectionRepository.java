package com.atoms.backend.reflection.repository;

import com.atoms.backend.reflection.entity.ReflectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReflectionRepository extends JpaRepository<ReflectionEntity, Long> {

    List<ReflectionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
