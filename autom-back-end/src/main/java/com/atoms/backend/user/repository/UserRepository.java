package com.atoms.backend.user.repository;

import com.atoms.backend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByWechatOpenId(String wechatOpenId);

    Optional<UserEntity> findByWechatUnionId(String wechatUnionId);
}
